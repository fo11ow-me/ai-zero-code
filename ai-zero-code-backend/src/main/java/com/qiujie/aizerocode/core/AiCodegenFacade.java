package com.qiujie.aizerocode.core;


import com.qiujie.aizerocode.ai.AiCodegenService;
import com.qiujie.aizerocode.ai.model.HtmlCodeResult;
import com.qiujie.aizerocode.ai.model.MultiFileCodeResult;
import com.qiujie.aizerocode.core.parser.CodeParserExecutor;
import com.qiujie.aizerocode.core.saver.CodeSaverExecutor;
import com.qiujie.aizerocode.exception.BusinessException;
import com.qiujie.aizerocode.exception.ErrorCode;
import com.qiujie.aizerocode.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * 代码生成门面类，组合代码生成和保存功能
 */

@Service
@Slf4j
public class AiCodegenFacade {


    @Autowired
    private AiCodegenService aiCodegenService;


    /**
     * 生成代码并保存
     *
     * @param userMessage 用户提示词
     * @param codeGenType 代码生成模式
     * @param appId       应用id
     * @return
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenType, Long appId) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成模式不能为空");
        }
        return switch (codeGenType) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodegenService.generateHtmlCode(userMessage);
                yield CodeSaverExecutor.execute(htmlCodeResult, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodegenService.generateMultiFileCode(userMessage);
                yield CodeSaverExecutor.execute(multiFileCodeResult, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成模式" + codeGenType.getValue());
        };
    }


    /**
     * 流式生成代码并保存
     *
     * @param userMessage 用户提示词
     * @param codeGenType 代码生成模式
     * @param appId       应用id
     * @return
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenType, Long appId) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成模式不能为空");
        }
        return switch (codeGenType) {
            case HTML -> {
                Flux<String> flux = aiCodegenService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(CodeGenTypeEnum.HTML, flux, appId);
            }
            case MULTI_FILE -> {
                Flux<String> flux = aiCodegenService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(CodeGenTypeEnum.MULTI_FILE, flux, appId);
            }
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成模式：" + codeGenType.getValue());
        };
    }


    /**
     * 处理代码流
     *
     * @param codeGenType
     * @param flux
     * @param appId
     * @return
     */
    private Flux<String> processCodeStream(CodeGenTypeEnum codeGenType, Flux<String> flux, Long appId) {
        StringBuilder sb = new StringBuilder();
        // 拼接每个字符块到末尾，接收完成后保存到文件
        return flux.doOnNext(sb::append).doOnComplete(() -> {
            try {
                Object obj = CodeParserExecutor.execute(sb.toString(), codeGenType);
                CodeSaverExecutor.execute(obj, codeGenType, appId);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "出现错误，" + e.getMessage());
            }
        });
    }

}

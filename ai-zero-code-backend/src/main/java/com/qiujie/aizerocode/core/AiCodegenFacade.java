package com.qiujie.aizerocode.core;


import cn.hutool.json.JSONUtil;
import com.qiujie.aizerocode.ai.AiCodegenService;
import com.qiujie.aizerocode.ai.AiCodegenServiceFactory;
import com.qiujie.aizerocode.ai.model.HtmlCodeResult;
import com.qiujie.aizerocode.ai.model.MultiFileCodeResult;
import com.qiujie.aizerocode.ai.model.message.AiResponseMessage;
import com.qiujie.aizerocode.ai.model.message.ToolExecutedMessage;
import com.qiujie.aizerocode.ai.model.message.ToolRequestMessage;
import com.qiujie.aizerocode.core.parser.CodeParserExecutor;
import com.qiujie.aizerocode.core.saver.CodeSaverExecutor;
import com.qiujie.aizerocode.exception.BusinessException;
import com.qiujie.aizerocode.exception.ErrorCode;
import com.qiujie.aizerocode.model.enums.CodeGenTypeEnum;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
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
    private AiCodegenServiceFactory aiCodegenServiceFactory;


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
        // 根据应用id获取AiCodegenService实例
        AiCodegenService aiCodegenService = aiCodegenServiceFactory.getAiCodegenService(appId, codeGenType);
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
        // 根据应用id获取AiCodegenService实例
        AiCodegenService aiCodegenService = aiCodegenServiceFactory.getAiCodegenService(appId, codeGenType);
        return switch (codeGenType) {
            case HTML -> {
                Flux<String> flux = aiCodegenService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(CodeGenTypeEnum.HTML, flux, appId);
            }
            case MULTI_FILE -> {
                Flux<String> flux = aiCodegenService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(CodeGenTypeEnum.MULTI_FILE, flux, appId);
            }
            case VUE_PROJECT -> {
                TokenStream tokenStream = aiCodegenService.generateVueProjectTokenStream(userMessage, appId);
                yield processTokenStream(tokenStream);
            }
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成模式：" + codeGenType.getValue());
        };
    }

    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    .beforeToolExecution((beforeToolExecution) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(beforeToolExecution.request());
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    .onCompleteResponse((ChatResponse response) -> {
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        log.error("TokenStream处理错误：{}", error.getMessage(), error);
                        sink.error(error);
                    })
                    .start();
        });
    }


    /**
     * 拼接流中的每个字符块，得到完整内容，解析其中的代码，然后分别保存到文件中
     *
     * @param codeGenType
     * @param flux
     * @param appId
     * @return
     */
    private Flux<String> processCodeStream(CodeGenTypeEnum codeGenType, Flux<String> flux, Long appId) {
        StringBuilder sb = new StringBuilder();
        return flux.doOnNext(sb::append).doOnComplete(() -> {
            try {
                log.info("LLM生成结果：{}", sb);
                Object obj = CodeParserExecutor.execute(sb.toString(), codeGenType);
                CodeSaverExecutor.execute(obj, codeGenType, appId);
            } catch (Exception e) {
                log.error("在代码解析、文件保存的过程中，出现了错误：{}", e.getMessage());
            }
        });
    }

}

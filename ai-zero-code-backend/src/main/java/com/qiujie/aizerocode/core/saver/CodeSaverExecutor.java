package com.qiujie.aizerocode.core.saver;

import com.qiujie.aizerocode.ai.model.HtmlCodeResult;
import com.qiujie.aizerocode.ai.model.MultiFileCodeResult;
import com.qiujie.aizerocode.exception.BusinessException;
import com.qiujie.aizerocode.exception.ErrorCode;
import com.qiujie.aizerocode.model.enums.CodeGenTypeEnum;

import java.io.File;

public class CodeSaverExecutor {

    private static final CodeSaver<HtmlCodeResult> htmlCodeSaver = new HtmlCodeSaver();

    private static final CodeSaver<MultiFileCodeResult> multiFileCodeSaver = new MultiFileCodeSaver();


    /**
     * 保存代码
     *
     * @param result
     * @param type
     * @return
     */
    public static File execute(Object result, CodeGenTypeEnum type) {
        return switch (type) {
            case HTML -> htmlCodeSaver.save((HtmlCodeResult) result);
            case MULTI_FILE -> multiFileCodeSaver.save((MultiFileCodeResult) result);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        };
    }
}

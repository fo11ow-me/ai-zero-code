package com.qiujie.aizerocode.core.saver;

import cn.hutool.core.util.StrUtil;
import com.qiujie.aizerocode.ai.model.HtmlCodeResult;
import com.qiujie.aizerocode.exception.BusinessException;
import com.qiujie.aizerocode.exception.ErrorCode;
import com.qiujie.aizerocode.model.enums.CodeGenTypeEnum;

public class HtmlCodeSaver extends CodeSaver<HtmlCodeResult> {

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码不能为空");
        }
    }


    @Override
    protected void saveToFile(String dirPath, HtmlCodeResult result) {
        writeToFile(dirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected CodeGenTypeEnum getCodeGenType() {
        return CodeGenTypeEnum.HTML;
    }
}

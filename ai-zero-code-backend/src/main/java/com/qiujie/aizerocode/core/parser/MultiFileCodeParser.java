package com.qiujie.aizerocode.core.parser;

import com.qiujie.aizerocode.ai.model.MultiFileCodeResult;

/**
 * 代码解析器
 * 提供静态方法解析不同类型的代码内容
 *
 */
public class MultiFileCodeParser implements CodeParser<MultiFileCodeResult> {


    /**
     * 解析多文件代码（HTML + CSS + JS）
     *
     * @param codeContent
     * @return
     */

    public MultiFileCodeResult parse(String codeContent) {
        MultiFileCodeResult result = new MultiFileCodeResult();
        // 提取各类代码
        String htmlCode = CodeParser.extractCodeByPattern(codeContent, HTML_CODE_PATTERN);
        String cssCode = CodeParser.extractCodeByPattern(codeContent, CSS_CODE_PATTERN);
        String jsCode = CodeParser.extractCodeByPattern(codeContent, JS_CODE_PATTERN);
        // 设置HTML代码
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        }
        // 设置CSS代码
        if (cssCode != null && !cssCode.trim().isEmpty()) {
            result.setCssCode(cssCode.trim());
        }
        // 设置JS代码
        if (jsCode != null && !jsCode.trim().isEmpty()) {
            result.setJsCode(jsCode.trim());
        }
        return result;
    }


}

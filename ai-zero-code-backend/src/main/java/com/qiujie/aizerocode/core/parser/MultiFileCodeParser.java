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
        // 提取各类代码：以下一个语言块的起始为右边界，块内最后一个 ``` 为闭合标记
        // 这样即使代码内容中包含 Markdown 代码块示例也不会被误截断
        String htmlCode = CodeParser.extractCodeBlock(codeContent, "html", "css|javascript|js");
        String cssCode = CodeParser.extractCodeBlock(codeContent, "css", "javascript|js");
        String jsCode = CodeParser.extractCodeBlock(codeContent, "javascript|js", null);
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

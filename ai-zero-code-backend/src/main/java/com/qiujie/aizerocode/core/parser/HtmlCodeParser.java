package com.qiujie.aizerocode.core.parser;

import com.qiujie.aizerocode.ai.model.HtmlCodeResult;
import com.qiujie.aizerocode.ai.model.MultiFileCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML代码解析器
 */
public class HtmlCodeParser implements CodeParser<HtmlCodeResult> {

    /**
     * 解析HTML代码
     *
     * @param codeContent 源代码内容
     * @return 解析结果
     */
    public HtmlCodeResult parse(String codeContent) {
        HtmlCodeResult result = new HtmlCodeResult();
        // 提取 HTML 代码：以内容末尾为右边界，块内最后一个 ``` 为闭合标记
        String htmlCode = CodeParser.extractCodeBlock(codeContent, "html", null);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        } else {
            // 如果没有找到代码块，将整个内容作为HTML
            result.setHtmlCode(codeContent.trim());
        }
        return result;
    }
}

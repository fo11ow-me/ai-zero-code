package com.qiujie.aizerocode.core;

import com.qiujie.aizerocode.ai.model.HtmlCodeResult;
import com.qiujie.aizerocode.ai.model.MultiFileCodeResult;

import java.util.regex.Pattern;

/**
 * 代码解析器
 * 提供静态方法解析不同类型的代码内容
 *
 */
@Deprecated
public class CodeParser {

    /**
     * 从 AI 响应中按 Markdown 代码块规则逐行提取指定语言的代码。
     * <p>
     * 遵循 CommonMark 规范：代码块起止标记必须出现在行首（最多允许 3 个空格缩进）。
     * 结合"下一个语言块起始"作为右边界，在边界内倒序查找闭合标记。
     *
     * @param content      AI 响应的完整文本
     * @param lang         要提取的语言（如 "html"、"css"、"javascript|js"）
     * @param boundaryLang 下一个语言块的标签，作为提取的右边界；传 {@code null} 表示以内容末尾为边界
     * @return 提取到的代码内容，未找到时返回 {@code null}
     */
    private static String extractCodeBlock(String content, String lang, String boundaryLang) {
        String[] lines = content.split("\n", -1);

        // 1. 正序找到开头 fence —— 行首 ```lang
        Pattern openPattern = Pattern.compile("^\\s{0,3}```(?:" + lang + ")\\s*$", Pattern.CASE_INSENSITIVE);
        int openLine = -1;
        for (int i = 0; i < lines.length; i++) {
            if (openPattern.matcher(lines[i]).matches()) {
                openLine = i;
                break;
            }
        }
        if (openLine == -1) {
            return null;
        }
        int contentStart = openLine + 1;

        // 2. 确定右边界：下一个语言块起始行，或末尾
        int boundary = lines.length;
        if (boundaryLang != null && !boundaryLang.isEmpty()) {
            Pattern boundaryPattern = Pattern.compile(
                    "^\\s{0,3}```(?:" + boundaryLang + ")\\s*$", Pattern.CASE_INSENSITIVE);
            for (int i = contentStart; i < lines.length; i++) {
                if (boundaryPattern.matcher(lines[i]).matches()) {
                    boundary = i;
                    break;
                }
            }
        }

        // 3. 倒序从 boundary-1 到 contentStart 找闭合 fence —— 行首 ```（无语言标识）
        Pattern closePattern = Pattern.compile("^\\s{0,3}```\\s*$");
        int closeLine = -1;
        for (int i = boundary - 1; i >= contentStart; i--) {
            if (closePattern.matcher(lines[i]).matches()) {
                closeLine = i;
                break;
            }
        }
        if (closeLine == -1) {
            return null;
        }

        // 4. 拼接 contentStart 到 closeLine-1 行的内容
        StringBuilder sb = new StringBuilder();
        for (int i = contentStart; i < closeLine; i++) {
            if (i > contentStart) {
                sb.append('\n');
            }
            sb.append(lines[i]);
        }
        return sb.toString();
    }

    /**
     * 解析HTML代码
     *
     * @param codeContent 源代码内容
     * @return 解析结果
     */
    public static HtmlCodeResult parseHtmlCode(String codeContent) {
        HtmlCodeResult result = new HtmlCodeResult();
        // 提取 HTML 代码
        String htmlCode = extractHtmlCode(codeContent);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        } else {
            // 如果没有找到代码块，将整个内容作为HTML
            result.setHtmlCode(codeContent.trim());
        }
        return result;
    }

    /**
     * 解析多文件代码（HTML + CSS + JS）
     *
     * @param codeContent
     * @return
     */
    public static MultiFileCodeResult parseMultiFileCode(String codeContent) {
        MultiFileCodeResult result = new MultiFileCodeResult();
        // 提取各类代码：以下一个语言块的起始为右边界，块内最后一个 ``` 为闭合标记
        String htmlCode = extractCodeBlock(codeContent, "html", "css|javascript|js");
        String cssCode = extractCodeBlock(codeContent, "css", "javascript|js");
        String jsCode = extractCodeBlock(codeContent, "javascript|js", null);
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

    /**
     * 提取HTML代码内容
     *
     * @param content 原始内容
     * @return HTML代码
     */
    private static String extractHtmlCode(String content) {
        return extractCodeBlock(content, "html", null);
    }
}

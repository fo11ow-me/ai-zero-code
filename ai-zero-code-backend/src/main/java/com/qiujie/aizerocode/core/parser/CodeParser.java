package com.qiujie.aizerocode.core.parser;

import java.util.regex.Pattern;

public interface CodeParser<T> {

    /**
     * 解析代码
     *
     * @param codeContent
     * @return
     */
    T parse(String codeContent);


    /**
     * 从 AI 响应中按 Markdown 代码块规则逐行提取指定语言的代码。
     * <p>
     * 遵循 CommonMark 规范：代码块起止标记必须出现在行首（最多允许 3 个空格缩进）。
     * 结合"下一个语言块起始"作为右边界，在边界内倒序查找闭合标记，
     * 从而正确处理代码内容中嵌入的 Markdown 代码块。
     *
     * @param content      AI 响应的完整文本
     * @param lang         要提取的语言（如 "html"、"css"、"javascript|js"）
     * @param boundaryLang 下一个语言块的标签（如 "css|javascript|js"），作为提取的右边界；
     *                     传 {@code null} 表示以内容末尾为边界
     * @return 提取到的代码内容，未找到时返回 {@code null}
     */
    static String extractCodeBlock(String content, String lang, String boundaryLang) {
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
}

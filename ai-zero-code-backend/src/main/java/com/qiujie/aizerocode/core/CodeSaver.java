package com.qiujie.aizerocode.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.qiujie.aizerocode.ai.model.HtmlCodeResult;
import com.qiujie.aizerocode.ai.model.MultiFileCodeResult;
import com.qiujie.aizerocode.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Deprecated
public class CodeSaver {


    // 文件保存的根目录
    private static final String FILE_ROOT_PATH = System.getProperty("user.dir") + "/tmp/code";

    /**
     * 保存html代码
     *
     * @param htmlCodeResult
     * @return
     */
    public static File saveHtmlCode(HtmlCodeResult htmlCodeResult) {
        String dirPath = createFileDir(CodeGenTypeEnum.HTML.getValue());
        saveToFile(dirPath, "index.html", htmlCodeResult.getHtmlCode());
        return new File(dirPath);
    }

    /**
     * 保存多文件代码
     *
     * @param multiFileCodeResult
     * @return
     */
    public static File saveMultiFileCode(MultiFileCodeResult multiFileCodeResult) {
        String dirPath = createFileDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        saveToFile(dirPath, "index.html", multiFileCodeResult.getHtmlCode());
        saveToFile(dirPath, "style.css", multiFileCodeResult.getCssCode());
        saveToFile(dirPath, "script.js", multiFileCodeResult.getJsCode());
        return new File(dirPath);
    }

    /**
     * 创建文件保存的目录：FILE_ROOT_PATH + File.separator + type_雪花id
     *
     * @param type
     * @return
     */
    private static String createFileDir(String type) {
        String dirPath = FILE_ROOT_PATH + File.separator + type + "_" + IdUtil.getSnowflakeNextIdStr();
        FileUtil.mkdir(dirPath);
        return dirPath;
    }


    /**
     * 保存文件
     *
     * @param dirPath
     * @param fileName
     * @param content
     */
    private static void saveToFile(String dirPath, String fileName, String content) {
        // AI 响应不一定包含所有代码块，跳过空内容避免 NPE
        if (content == null || content.isEmpty()) {
            return;
        }
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}

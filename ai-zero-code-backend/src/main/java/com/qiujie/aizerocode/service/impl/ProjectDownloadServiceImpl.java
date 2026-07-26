package com.qiujie.aizerocode.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.qiujie.aizerocode.exception.BusinessException;
import com.qiujie.aizerocode.exception.ErrorCode;
import com.qiujie.aizerocode.exception.ThrowUtils;
import com.qiujie.aizerocode.service.ProjectDownloadService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;


@Service
@Slf4j
public class ProjectDownloadServiceImpl implements ProjectDownloadService {

    /**
     * 忽略的目录
     */
    private static final Set<String> IGNORED_DIRS = Set.of(
            "node_modules",
            ".git",
            "dist",
            "build",
            ".DS_Store",
            ".env",
            "target",
            ".mvn",
            ".idea",
            ".vscode"
    );

    /**
     * 忽略的文件扩展名
     */
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".cache",
            ".tmp",
            ".log"
    );


    @Override
    public void downloadProjectAsZip(String projectPath, String downloadFilename, HttpServletResponse response) {
        // 基础校验
        ThrowUtils.throwIf(StrUtil.isBlank(projectPath), ErrorCode.PARAMS_ERROR, "项目路径不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(downloadFilename), ErrorCode.PARAMS_ERROR, "下载文件名不能为空");
        File projectDir = new File(projectPath);
        ThrowUtils.throwIf(!projectDir.exists(), ErrorCode.NOT_FOUND_ERROR, "项目不存在");
        ThrowUtils.throwIf(!projectDir.isDirectory(), ErrorCode.PARAMS_ERROR, "项目路径不是目录");
        // 设置http响应头
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + downloadFilename + ".zip");
        // 定义文件过滤器
        FileFilter filter = file -> isReserved(projectDir.toPath(), file.toPath());
        // 压缩
        try {
            ZipUtil.zip(response.getOutputStream(), StandardCharsets.UTF_8, false, filter, projectDir);
            log.info("项目打包成功：{} -> {}.zip", projectPath, downloadFilename);
        } catch (IOException e) {
            log.error("项目打包失败：{}", projectPath, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "项目打包失败");
        }
    }


    /**
     * 检查文件是否应该被忽略
     *
     * @param projectPath  项目路径
     * @param absolutePath 文件的绝对路径
     * @return 是否应该被忽略
     */
    private boolean isReserved(Path projectPath, Path absolutePath) {
        // 获取相对路径
        Path relativePath = projectPath.relativize(absolutePath);
        // 检查路径中的每一部分是否符合要求
        for (Path part : relativePath) {
            String partName = part.toString();
            // 检查是否在忽略名称列表中
            if (IGNORED_DIRS.contains(partName)) {
                return false;
            }
            // 检查是否在忽略扩展名列表中
            if (IGNORED_EXTENSIONS.stream().anyMatch(extension -> partName.toLowerCase().endsWith(extension))) {
                return false;
            }
        }
        return true;
    }
}

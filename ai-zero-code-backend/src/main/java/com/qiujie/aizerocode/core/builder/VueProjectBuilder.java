package com.qiujie.aizerocode.core.builder;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class VueProjectBuilder {


    /**
     * 异步构建 Vue 项目
     *
     * @param path 项目路径
     */
    public void buildProjectAsync(String path) {
        Thread.ofVirtual().name("vue-builder-" + System.currentTimeMillis())
                .start(() -> {
                    try {
                        buildProject(path);
                    } catch (Exception e) {
                        log.error("异步构建Vue 项目失败：{}", e.getMessage());
                    }
                });
    }


    /**
     * 构建 Vue 项目
     *
     * @param path 项目路径
     * @return 是否构建成功
     */
    public boolean buildProject(String path) {
        File projectDir = new File(path);
        if (!projectDir.exists()) {
            log.error("项目目录不存在： {}", path);
            return false;
        }
        // 检查是否有package.json文件
        File packageJson = new File(projectDir, "package.json");
        if (!packageJson.exists()) {
            log.error("项目目录下不存在 package.json 文件： {}", path);
            return false;
        }
        // 执行npm install
        if (!executeNpmInstall(projectDir)) {
            log.error("npm install 执行失败： {}", path);
            return false;
        }
        // 执行npm run build
        if (!executeNpmBuild(projectDir)) {
            log.error("npm run build 执行失败： {}", path);
            return false;
        }
        // 检查是否有dist目录
        File distDir = new File(projectDir, "dist");
        if (!distDir.exists() || !distDir.isDirectory()) {
            log.error("项目目录下不存在 dist 目录： {}", path);
            return false;
        }
        log.info("项目构建成功： {}", path);
        return true;

    }


    /**
     * 执行 npm install 命令
     */
    private boolean executeNpmInstall(File projectDir) {
        log.info("执行 npm install...");
        String command = String.format("%s install", buildCommand("npm"));
        return executeCommand(projectDir, command, 300); // 5分钟超时
    }

    /**
     * 执行 npm run build 命令
     */
    private boolean executeNpmBuild(File projectDir) {
        log.info("执行 npm run build...");
        String command = String.format("%s run build", buildCommand("npm"));
        return executeCommand(projectDir, command, 180); // 3分钟超时
    }


    /**
     * 构建 npm 命令（win下，直接执行npm命令会保报错）
     *
     * @param baseCommand 基础命令
     * @return 构建后的命令
     */
    private String buildCommand(String baseCommand) {
        if (isWindows()) {
            return baseCommand + ".cmd";
        }
        return baseCommand;
    }


    /**
     * 判断当前操作系统是否为 Windows
     *
     * @return true 表示当前操作系统为 Windows，false 表示当前操作系统为 Linux 或 MacOS
     */
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }


    /**
     * 执行命令
     *
     * @param workingDir     工作目录
     * @param command        命令字符串
     * @param timeoutSeconds 超时时间（秒）
     * @return 是否执行成功
     */
    private boolean executeCommand(File workingDir, String command, int timeoutSeconds) {
        try {
            log.info("在目录 {} 中执行命令：： {}", workingDir.getAbsolutePath(), command);
            Process process = RuntimeUtil.exec(
                    null,
                    workingDir,
                    command.split("\\s+") // 按空格将命令分割为数组
            );
            // 等待进程完成，设置超时
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                log.error("命令执行超时（{}秒），强制终止进程", timeoutSeconds);
                process.destroyForcibly();
                return false;
            }
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("命令执行成功： {}", command);
                return true;
            } else {
                log.error("命令执行失败，退出码： {}", exitCode);
                return false;
            }
        } catch (Exception e) {
            log.error("执行命令失败： {}, 错误信息： {}", command, e.getMessage());
            return false;
        }
    }

}


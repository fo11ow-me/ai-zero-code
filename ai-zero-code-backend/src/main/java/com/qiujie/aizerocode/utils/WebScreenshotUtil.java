package com.qiujie.aizerocode.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.qiujie.aizerocode.exception.BusinessException;
import com.qiujie.aizerocode.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

import static com.qiujie.aizerocode.constant.AppConstant.SCREENSHOT_SAVE_PATH;

@Slf4j
public class WebScreenshotUtil {

    private static final WebDriver webDriver;

    static {
        final int DEFAULT_WIDTH = 1600;
        final int DEFAULT_HEIGHT = 900;
        webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @PreDestroy
    public void destroy() {
        webDriver.quit();
    }


    /**
     * 获取网页截图，并保存到本地
     *
     * @param webUrl
     * @return
     */
    public static String takeScreenshot(String webUrl) {
        // 非空校验
        if (StrUtil.isBlank(webUrl)) {
            log.error("webUrl不能为空");
        }
        try {
            // 创建目录
            String dirPath = SCREENSHOT_SAVE_PATH + File.separator + RandomUtil.randomString(10);
            FileUtil.mkdir(dirPath);
            // 图片后缀
            String imgSuffix = ".png";
            String imgPath = dirPath + File.separator + RandomUtil.randomString(10) + imgSuffix;
            // 访问网页
            webDriver.get(webUrl);
            // 等待页面加载完成
            waitForPageLoad(webDriver);
            // 截图
            byte[] screenshotAs = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES); // 也可以直接返回文件
            // 保存图片
            saveScreenshot(screenshotAs, imgPath);
            // 压缩图片
            String compressedImgSuffix = "_compress.jpg";
            String compressedImgPath = dirPath + File.separator + RandomUtil.randomString(10) + compressedImgSuffix;
            compressImage(imgPath, compressedImgPath);
            // 删除原始图片
            FileUtil.del(imgPath);
            return compressedImgPath;
        } catch (Exception e) {
            log.error("获取网页截图失败：{}", e.getMessage(), e);
            return null;
        }
    }


    /**
     * 初始化 Chrome 浏览器驱动
     */
    private static WebDriver initChromeDriver(int width, int height) {
        try {
            // 自动管理 ChromeDriver
            WebDriverManager.chromedriver().setup();
            // 配置 Chrome 选项
            ChromeOptions options = new ChromeOptions();
            // 无头模式
            options.addArguments("--headless");
            // 禁用GPU（在某些环境下避免问题）
            options.addArguments("--disable-gpu");
            // 禁用沙盒模式（Docker环境需要）
            options.addArguments("--no-sandbox");
            // 禁用开发者shm使用
            options.addArguments("--disable-dev-shm-usage");
            // 设置窗口大小
            options.addArguments(String.format("--window-size=%d,%d", width, height));
            // 禁用扩展
            options.addArguments("--disable-extensions");
            // 设置用户代理
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            // 创建驱动
            WebDriver driver = new ChromeDriver(options);
            // 设置页面加载超时
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // 设置隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;
        } catch (Exception e) {
            log.error("初始化 Chrome 浏览器失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "初始化 Chrome 浏览器失败");
        }
    }


    /**
     * 保存截图
     *
     * @param bytes
     * @param filePath
     */
    private static void saveScreenshot(byte[] bytes, String filePath) {
        try {
            FileUtil.writeBytes(bytes, filePath);
            log.info("截图保存成功：{}", filePath);
        } catch (Exception e) {
            log.error("保存截图失败：{}", filePath, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存截图失败");
        }
    }


    /**
     * 压缩图片
     *
     * @param sourcePath
     * @param targetPath
     */
    private static void compressImage(String sourcePath, String targetPath) {
        final float quality = 0.3f;
        try {
            ImgUtil.compress(FileUtil.file(sourcePath), FileUtil.file(targetPath), quality);
            log.info("图片压缩成功：{} -> {}", sourcePath, targetPath);
        } catch (IORuntimeException e) {
            log.error("图片压缩失败：{} -> {}", sourcePath, targetPath, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片压缩失败");
        }
    }


    /**
     * 等待页面加载完成
     *
     * @param driver
     */
    private static void waitForPageLoad(WebDriver driver) {
        try {
            // 创建 WebDriverWait 对象
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // 等待 document.readyState 为 complete
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState")
                    .equals("complete"));
            // 额外等待一段时间，确保页面完全加载
            Thread.sleep(2000);
            log.info("页面加载完成");
        } catch (InterruptedException e) {
            log.error("等待页面加载失败", e);
        }
    }
}

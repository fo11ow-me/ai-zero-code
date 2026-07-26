package com.qiujie.aizerocode.service;

public interface ScreenshotService {


    /**
     * 获取网页截图并上传
     *
     * @param webUrl
     * @return
     */
    String takeAndUploadScreenshot(String webUrl);
}

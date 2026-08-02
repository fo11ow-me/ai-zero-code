package com.qiujie.aizerocode.constant;


import java.io.File;

/**
 * 应用常量
 */
public interface AppConstant {

    /**
     * 精选应用的优先级
     */
    Integer GOOD_APP_PRIORITY = 99;


    /**
     * 默认应用优先级
     */
    Integer DEFAULT_APP_PRIORITY = 0;


    /**
     * 代码保存路径
     */
    String CODE_SAVE_PATH = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "code";

    /**
     * 截图保存路径
     */
    String SCREENSHOT_SAVE_PATH = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "screenshot";


    /**
     * 应用部署路径
     */
    String APP_DEPLOY_PATH = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "deploy";


    String APP_DEPLOY_HOST = "http://localhost";

    String GOOD_APP_CACHE_KEY = "GOOD_APP";

}

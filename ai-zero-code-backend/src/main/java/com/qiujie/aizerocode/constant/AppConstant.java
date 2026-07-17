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
    public static final String CODE_SAVE_PATH = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "code";


    /**
     * 应用部署路径
     */
    public static final String APP_DEPLOY_PATH = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "deploy";


    public static final String APP_DEPLOY_HOST = "http://localhost";

}

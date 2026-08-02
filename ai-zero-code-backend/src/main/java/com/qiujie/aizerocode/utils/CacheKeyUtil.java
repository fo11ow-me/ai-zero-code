package com.qiujie.aizerocode.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;

public class CacheKeyUtil {


    /**
     * 获取缓存的key
     *
     * @param object
     * @return
     */
    public static String getCacheKey(Object object) {
        if (object == null) {
            return DigestUtil.md5Hex("null");
        }
        String jsonStr = JSONUtil.toJsonStr(object);
        return DigestUtil.md5Hex(jsonStr);
    }

}

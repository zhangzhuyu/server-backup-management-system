package com.ly.cloud.quartz.singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: ljb
 * @date: 2022 5 27
 */
public class SystemCache {

    private SystemCache() {

    }

    private static SystemCache sysCache = new SystemCache();

    private Map<String, Object> mapCache = new HashMap();

    public static SystemCache getInstance() {
        return sysCache;
    }

    public Map<String, Object> getCacheMap() {
        return mapCache;
    }


}

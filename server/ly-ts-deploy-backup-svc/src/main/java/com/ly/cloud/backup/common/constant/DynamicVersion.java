package com.ly.cloud.backup.common.constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: gyc
 * @Date: 2020/2/13 14:28
 */
public enum DynamicVersion {

    VERSION_1("1.0"),
    VERSION_2("2.0"),
    VERSION_3("3.0"),
    VERSION_4("4.0"),
    VERSION_5("5.0"),
    VERSION_6("6.0"),
    VERSION_7("7.0"),
    VERSION_8("8.0"),
    VERSION_ORACLE_11g("11g"),
    VERSION_ORACLE_12c("12c"),
    VERSION_ORACLE_10g("10g");


    private String version;

     DynamicVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }


    public  static Map<String,Object> currentVersionInfo  = new ConcurrentHashMap();
}

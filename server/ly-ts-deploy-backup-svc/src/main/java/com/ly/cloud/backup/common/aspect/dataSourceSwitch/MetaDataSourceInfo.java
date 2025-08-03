package com.ly.cloud.backup.common.aspect.dataSourceSwitch;

import lombok.Data;

@Data
public class MetaDataSourceInfo {

    /**
     * 数据源编号SJYBH
     */
    private String sjybh;
    /**
     * 数据库名称SJKKMC
     */
    private String sjkkmc;

    /**
     * 数据库密码SJKMM
     */
    private String sjkmm;

    /**
     * 数据库用户SJKYH
     */
    private String sjkyh;

    private String schema;

    /**
     * 数据库类型SJYLX
     */
    private String sjylx;

    /**
     * 数据源驱动SJYQD
     */
    private String sjyqd;

    /**
     * 数据源URL
     */
    private String sjyurl;

    /**
     * 系统编号
     */
    private long xtbh;
    /*
     * 数据源名称
     */
    private String sjymc;

    private String csljzt;

}
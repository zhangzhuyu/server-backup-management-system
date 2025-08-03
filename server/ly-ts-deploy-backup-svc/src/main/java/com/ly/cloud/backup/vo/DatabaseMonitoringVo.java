package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据库监控信息表
 * @author SYC
 *
 */
@Data
public class DatabaseMonitoringVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 最大连接数
     */
   private String maxConnections;

    /**
     * 当前的数据库连接数
     */
    private String currentConnections;

    /**
     * 实例名称
     */
    private String instanceName;

    /**
     * 实数据库版本
     */
    private String version;

    /**
     * 数据库状态
     */
    private String databaseStatus;

    /**
     * 实例状态
     */
    private String instanceStatus;

    /**
     * 表空间名称
     */
    private String tablespaceName;

    /**
     * 是否开启表空间容量自增长(YES)
     */
    private String autoextensible;

    /**
     * 使用量(G)
     */
    private String usageAmountG;

    /**
     * 总量(G)
     */
    private String amountsG;

    /**
     * 总量(B)
     */
    private String amounts;

    /**
     * 使用量(B)
     */
    private String usageAmount;

    /**
     * 使用率
     */
    private String usageRate;



}

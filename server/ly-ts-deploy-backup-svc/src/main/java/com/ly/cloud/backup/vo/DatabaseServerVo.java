package com.ly.cloud.backup.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ly.cloud.backup.common.annotation.Sm4Field;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 数据库服务器关系表：ly_rm_database_server
 * @author SYC
 *
 */
@Data
public class DatabaseServerVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
     /**
     * 数据库ID
     */
    private Long databaseId;
    
     /**
     * 服务器ID
     */
    private Long serverId;
    
     /**
     * 操作时间
     */
    private java.util.Date operationTime;
    
     /**
     * 操作人ID
     */
    private String operatorId;
    
     /**
     * 备注
     */
    private String remark;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源类型（枚举）
     */
    private String sourceType;

    /**
     * 端口
     */
    private String port;

    /**
     * IP地址(ipv4)
     */
    private String ipv4;

    /**
     * 适配器ID（外键）
     */
    private String adapterId;

    /**
     * 数据库名称（实例）
     */
    private String databaseName;

    /**
     * 数据源驱动
     */
    private String driver;

    /**
     * 账号
     */
    private String user;

    /**
     * 密码
     */
    @Sm4Field
    private String password;

    /**
     * URL地址
     */
    private String url;

    /**
     * 是否需要核验（枚举）
     */
    private String whetherCheck;

    /**
     * 核验规则ID
     */
    private String ruleId;

    /**
     * 是否加入运维大屏展示（枚举）
     */
    private String whetherMonitoring;

    /**
     * 测试状态（枚举）
     */
    private String testStatus;


    /**
     * 所属公司
     */
    private Long affiliatedCompany;

    /**
     * 序号
     */
    private int serialNumber;

    /**
     * 业务领域（枚举）
     */
    private String businessDomain;

    /**
     * hostName
     */
    private String hostName;

    /**
     * 业务角色（枚举）
     */
    private String businessWorker;

    /**
     * IP地址(ipv6)
     */
    private String ipv6;

    /**
     * 服务器类型（枚举）
     */
    private String serverType;


    /**
     * 是否开放外网：1：是,0：否
     */
    private String whetherOuterNet;


    /**
     * 健康状态（枚举）
     */
    private String healthStatus;

    /**
     * 健康状态（枚举） 中文
     */
    private String healthStatusChinese;

    /**
     * 防火墙状态
     */
    private String firewallState;

    /**
     * 系统类型
     */
    private String systemType;


    /**
     * CPU使用率
     */
    private double utilizationRateCPU;

    /**
     * 内存使用率
     */
    private double utilizationRateRAM;
    /**
     * 内存使用Map
     */
    private Map<String, Double> mapRAM;

    /**
     * 硬盘使用率
     */
    private double utilizationRateHardDisk;
    /**
     * 硬盘使用Map
     */
    private Map<String, Double> mapHardDisk;


    /**
     * cpu（单位：核数）
     */
    private String totalCpu;

    /**
     * ram内存（单位：千兆数）
     */
    private String  totalRam;

    /**
     * 系统盘（单位：千兆数）
     */
    private String  totalHardDisk;

    /**
     * cpu使用率（单位：核数）
     */
    private String useCpu;

    /**
     * ram内存使用率（单位：千兆数）
     */
    private String  useRam;

    /**
     * 系统盘使用率（单位：千兆数）
     */
    private String  useHardDisk;

    /**
     * cpu（单位）
     */
    private String unitCpu;

    /**
     * ram内存（单位）
     */
    private String  unitRam;

    /**
     * 系统盘（单位）
     */
    private String  unitDisk;

    /**
     * 当前连接数
     */
    private String databaseCurrentConnectCount;

    /**
     * 允许连接数
     */
    private String databaseMaxConnectCount;


    /**
     * 锁数量
     */
    private String databaseLockCount;




}

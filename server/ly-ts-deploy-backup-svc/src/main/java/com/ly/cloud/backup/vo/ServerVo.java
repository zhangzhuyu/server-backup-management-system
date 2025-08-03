package com.ly.cloud.backup.vo;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ly.cloud.backup.common.annotation.Sm4Field;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务器信息表
 *
 * @author chenguoqing
 */
@Data
public class ServerVo implements Serializable {

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
     * 业务领域（枚举）
     */
    private String businessDomain;

    /**
     * hostName
     */
    private String hostName;

    /**
     * 名称
     */
    private String name;

    /**
     * 业务角色（枚举）
     */
    private String businessWorker;

    /**
     * IP地址(ipv4)
     */
    private String ipv4;

    /**
     * IP地址(ipv6)
     */
    private String ipv6;

    /**
     * 服务器类型（枚举）
     */
    private String serverType;

    /**
     * 端口
     */
    private String port;

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
     * 是否加入运维大屏展示（枚举）
     */
    private String whetherMonitoring;

    /**
     * 序号
     */
    private Integer serialNumber;

    /**
     * 是否开放外网：1：是,0：否
     */
    private String whetherOuterNet;

    /**
     * 所属公司
     */
    private Long affiliatedCompany;
    private String affiliatedCompanyName;

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
     * 测试状态（枚举）
     */
    private String testStatus;

    /**
     * 配置
     */
    private String configuration;

    /**
     * Agent状态(1:未安装，2:安装中,3:安装失败,4:运行中,5:已停止)
     */
    private String agentStatus;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private java.util.Date operationTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * CPU使用率
     */
    private Double utilizationRateCPU;

    /**
     * 内存使用率
     */
    private Double utilizationRateRAM;
    /**
     * 内存使用Map
     */
    private Map<String, Double> mapRAM;

    /**
     * 硬盘使用率
     */
    private Double utilizationRateHardDisk;
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
    private String totalRam;

    /**
     * 系统盘（单位：千兆数）
     */
    private String totalHardDisk;

    /**
     * cpu使用率（单位：核数）
     */
    private String useCpu;

    /**
     * ram内存使用率（单位：千兆数）
     */
    private String useRam;

    /**
     * 系统盘使用率（单位：千兆数）
     */
    private String useHardDisk;

    /**
     * cpu（单位）
     */
    private String unitCpu;

    /**
     * ram内存（单位）
     */
    private String unitRam;

    /**
     * ram内存使用量（单位）
     */
    private String useUnitRam;

    /**
     * 系统盘（单位）
     */
    private String unitDisk;

    /**
     * 系统盘使用量（单位）
     */
    private String useUnitDisk;

    /**
     * 开放端口数量
     */
    private Integer openPortCount;

    /**
     * oracle用户
     */
    private String oracleUser;

    /**
     * oracle密码
     */
    private String oraclePassword;

    /**
     * oracle备份路径
     */
    private String oracleBackupPath;

    /**
     * mysql备份路径
     */
    private String mysqlBackupPath;

    /**
     * mongodb备份路径
     */
    private String mongodbBackupPath;

    /**
     * tidb备份路径
     */
    private String tidbBackupPath;


}

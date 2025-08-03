package com.ly.cloud.backup.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ly.cloud.backup.common.annotation.Sm4Field;
import lombok.Data;

/**
 * 数据库信息表
 *
 * @author chenguoqing
 */
@Data
public class DatabaseVo implements Serializable {

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
     * 密码过期时间
     */
    private String passwordExpireTime;

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
     * 关联服务器（可多选）
     */
    private List<String> serverId;
    private List<String> serverNames;
//    private String[] serverId;

    /**
     * 测试状态（枚举）
     */
    private String testStatus;

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
     * 所属公司
     */
    private Long affiliatedCompany;

    /**
     * 序号
     */
    private int serialNumber;

    /**
     * 健康状态（枚举）
     */
    private String healthStatus;

}

package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 数据库信息表：ly_rm_database
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_rm_database")
public class DatabasePo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 数据源名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 数据源类型（枚举）
     */
    @TableField(value = "source_type")
    private String sourceType;

    /**
     * 端口
     */
    @TableField(value = "port")
    private String port;

    /**
     * IP地址(ipv4)
     */
    @TableField(value = "ipv4")
    private String ipv4;

    /**
     * 适配器ID（外键）
     */
    @TableField(value = "adapter_id")
    private String adapterId;

    /**
     * 数据库名称（实例）
     */
    @TableField(value = "database_name")
    private String databaseName;

    /**
     * 数据源驱动
     */
    @TableField(value = "driver")
    private String driver;

    /**
     * 账号
     */
    @TableField(value = "user")
    private String user;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 密码过期时间
     */
    @TableField(value = "password_expire_time")
    private String passwordExpireTime;

    /**
     * URL地址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 是否需要核验（枚举）
     */
    @TableField(value = "whether_check")
    private String whetherCheck;

    /**
     * 核验规则ID
     */
    @TableField(value = "rule_id")
    private String ruleId;

    /**
     * 是否加入运维大屏展示（枚举）
     */
    @TableField(value = "whether_monitoring")
    private String whetherMonitoring;

/*    *//**
     * 关联服务器（可多选）
     *//*
    @TableField(value = "server_id")
    private String serverId;*/

    /**
     * 测试状态（枚举）
     */
    @TableField(value = "test_status")
    private String testStatus;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time", fill = FieldFill.INSERT_UPDATE)
    private java.util.Date operationTime;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 所属公司
     */
    @TableField("affiliated_company")
    private Long affiliatedCompany;

    /**
     * 序号
     */
    @TableField("serial_number")
    private int serialNumber;

    /**
     * 健康状态（枚举）
     */
    @TableField(value = "health_status")
    private String healthStatus;

}


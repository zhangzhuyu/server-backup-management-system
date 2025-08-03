package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 防火墙信息表
 * @TableName ly_rm_firewall
 */
@TableName(value ="ly_rm_firewall")
@Data
public class FirewallPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id")
    private Long id;

    /**
     * 规则名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 应用类型(枚举)
     */
    @TableField(value = "application_type")
    private String applicationType;

    /**
     * 限制来源
     */
    @TableField(value = "whether_limited_source")
    private String whetherLimitedSource;

    /**
     * 策略IP地址
     */
    @TableField(value = "source_ip")
    private String sourceIp;

    /**
     * 协议(枚举)
     */
    @TableField(value = "agreement")
    private String agreement;

    /**
     * 端口
     */
    @TableField(value = "port")
    private String port;

    /**
     * 策略(枚举)
     */
    @TableField(value = "strategy")
    private String strategy;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private Long operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 关联server表的id
     */
    @TableField(value = "server_id")
    private Long serverId;

}
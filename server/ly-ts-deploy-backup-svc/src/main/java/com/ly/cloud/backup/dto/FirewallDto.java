package com.ly.cloud.backup.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 防火墙信息表
 *
 * @author chenguoqing
 */
@Data
public class FirewallDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 规则名称
     */
    @ApiModelProperty("规则名称")
    private String name;

    /**
     * 应用类型(枚举)
     */
    @ApiModelProperty("应用类型(枚举)")
    private String applicationType;

    /**
     * 限制来源
     */
    @ApiModelProperty("限制来源")
    private String whetherLimitedSource;

    /**
     * 策略IP地址
     */
    @ApiModelProperty("策略IP地址")
    private String sourceIp;

    /**
     * 协议(枚举)
     */
    @ApiModelProperty("协议(枚举)")
    private String agreement;

    /**
     * 端口
     */
    @ApiModelProperty("端口")
    private String port;

    /**
     * 策略(枚举)
     */
    @ApiModelProperty("策略(枚举)")
    private String strategy;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private java.util.Date operationTime;

    /**
     * serverId
     */
    @ApiModelProperty("服务器ID")
    private Long serverId;

    @ApiModelProperty("备注")
    private String remark;

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

}

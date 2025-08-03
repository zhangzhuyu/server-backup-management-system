package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 监控预警-推送记录视图类
 */
@Data
@ApiModel
public class WarningPushRecordVo implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = 5969174612975898769L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 发送方式（钉钉:1、邮件:2、短信:3、企业微信:0）
     */
    @ApiModelProperty("发送方式（钉钉:1、邮件:2、短信:3、企业微信:0）")
    private Integer messageType;


    /**
     * 发送状态（1：成功，0：失败）
     */
    @ApiModelProperty("（1：成功，0：失败）")
    private Integer messageStatus;

    /**
     * 发送时间
     */
    @ApiModelProperty("发送时间")
    private java.util.Date messageTime;

    /**
     * 收件人ID
     */
    @ApiModelProperty("收件人ID")
    private String recipientsId;

    @ApiModelProperty("收件人名字")
    private String recipientsName;

    /**
     * 发送失败原因
     */
    @ApiModelProperty("发送失败原因")
    private String remark;


    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;


    /**
     * 预警规则名称
     */
    @ApiModelProperty("预警规则名称")
    private String name;

    @ApiModelProperty("记录对应的规则id")
    private String ruleId;

    private Long total;

    /**
     * 规则对应的发送方式
     */
    private String enableContent;


}

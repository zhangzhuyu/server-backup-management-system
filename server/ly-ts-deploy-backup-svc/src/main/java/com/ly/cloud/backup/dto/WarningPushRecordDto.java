package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 监控预警-推送记录
 */
@Data
public class WarningPushRecordDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3969174612975898769L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 发送方式（钉钉:1、邮件:2、短信:3、企业微信:0）
     */
    @ApiModelProperty("发送方式（钉钉:1、邮件:2、短信:3、企业微信:0）")
    private int message_type;

    /**
     * 发送方式列表(钉钉:1、邮件:2、短信:3、企业微信:0, 用于条件查询多选)
     */
    @ApiModelProperty("发送方式列表(钉钉:1、邮件:2、短信:3、企业微信:0, 用于条件查询多选)")
    private List<String> messageTypeList;

    /**
     * 发送状态（1：成功，0：失败）
     */
    @ApiModelProperty("发送状态（1：成功，0：失败")
    private int messageStatus;
    /**
     * 发送状态列表（1：成功，0：失败  用于条件查询多选）
     */
    @ApiModelProperty("发送状态列表（1：成功，0：失败  用于条件查询多选）")
    private List<String> messageStatusList;

    /**
     * 发送时间
     */
    @ApiModelProperty("发送时间")
    private java.util.Date messageTime;

    /**
     * 收件人ID
     */
    @ApiModelProperty("订阅人")
    private String recipientsId;

    /**
     * 发送失败原因
     */
    @ApiModelProperty("原因")
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

    @ApiModelProperty("搜索内容")
    private String content;

    @ApiModelProperty("搜索内容")
    private String ruleId;

    @ApiModelProperty("推送记录id集合(用于批量删除)")
    private List<String> ids;

    @ApiModelProperty("排序字段")
    private String columnKey;

    @ApiModelProperty("排序順序")
    private String order;
}

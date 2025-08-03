package com.ly.cloud.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订阅报告表
 * @TableName ly_or_report_subscription
 */
@Data
public class ReportSubscriptionDto implements Serializable {

    private static final long serialVersionUID = 3826707057079399370L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 订阅标题
     */
    @ApiModelProperty("订阅标题")
    private String subscriptionTitle;

    /**
     * 订阅类型（0、周报,1、月报,2、年报）
     */
    @ApiModelProperty("订阅类型（0、周报,1、月报,2、年报）")
    private String subscriptionType;

    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用")
    private String enable;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private Date operationTime;

    //自增属性
    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

    /**
     * 订阅人id集合
     */
    @ApiModelProperty("订阅人id集合")
    private List<Long> subscriberIds;

    /**
     * 订阅类型过滤查询
     */
    @ApiModelProperty("订阅类型过滤查询")
    private List<String> subscriptionTypeList;

}
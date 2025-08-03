package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订阅报告表
 * @TableName ly_or_report_subscription
 */
@Data
public class ReportSubscriptionVo implements Serializable {

    private static final long serialVersionUID = 3826707057079399370L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 订阅标题
     */
    private String subscriptionTitle;

    /**
     * 订阅类型（0、周报,1、月报,2、年报）
     */
    private String subscriptionType;

    /**
     * 是否启用
     */
    private String enable;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private Date operationTime;

    // 自增属性
    /**
     * 订阅人id集合
     */
    private List<Map<String,String>> subscribers;

    /**
     * 订阅人名称
     */
    private String subscriberNames;

}
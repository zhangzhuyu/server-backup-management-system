package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ly_or_report_subscriber
 */
@Data
public class ReportSubscriberVo implements Serializable {

    private static final long serialVersionUID = 2692527547534470434L;
    
    /**
     * 主键
     */
    private Long id;

    /**
     * 订阅报告id
     */
    private Long reportSubscriptionId;

    /**
     * 订阅人id
     */
    private Long subscriberId;

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
     * 订阅人名称
     */
    private String subscriberNmae;

}
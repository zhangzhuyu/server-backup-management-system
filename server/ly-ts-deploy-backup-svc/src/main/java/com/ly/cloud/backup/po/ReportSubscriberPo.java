package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 报告订阅人表
 * @TableName ly_or_report_subscriber
 */
@Data
@TableName("ly_or_report_subscriber")
public class ReportSubscriberPo implements Serializable {

    private static final long serialVersionUID = 2692527547534470434L;
    
    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 订阅报告id
     */
    @TableField(value = "report_subscription_id")
    private Long reportSubscriptionId;

    /**
     * 订阅人id
     */
    @TableField(value = "subscriber_id")
    private Long subscriberId;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private Date operationTime;

}
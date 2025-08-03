package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订阅报告表
 * @TableName ly_or_report_subscription
 */
@Data
@TableName("ly_or_report_subscription")
public class ReportSubscriptionPo implements Serializable {

    private static final long serialVersionUID = 3826707057079399370L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 订阅标题
     */
    @TableField(value = "subscription_title")
    private String subscriptionTitle;

    /**
     * 订阅类型（0、周报,1、月报,2、年报）
     */
    @TableField(value = "subscription_type")
    private String subscriptionType;

    /**
     * 是否启用
     */
    @TableField(value = "enable")
    private String enable;

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
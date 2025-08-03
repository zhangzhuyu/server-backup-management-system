package com.ly.cloud.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ly_or_report_subscriber
 */
@Data
public class ReportSubscriberDto implements Serializable {

    private static final long serialVersionUID = 2692527547534470434L;
    
    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 订阅报告id
     */
    @ApiModelProperty("订阅报告id")
    private Long reportSubscriptionId;

    /**
     * 订阅人id
     */
    @ApiModelProperty("订阅人id")
    private Long subscriberId;

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

}
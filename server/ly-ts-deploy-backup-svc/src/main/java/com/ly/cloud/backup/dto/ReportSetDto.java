package com.ly.cloud.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ly_or_report_set
 */
@Data
public class ReportSetDto implements Serializable {

    private static final long serialVersionUID = 1896730468888273630L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 参数设置1
     */
    @ApiModelProperty("参数设置1")
    private String parameter1;

    /**
     * 参数设置2
     */
    @ApiModelProperty("参数设置2")
    private String parameter2;

    /**
     * 是否启用(1:启用,0:不启用)
     */
    @ApiModelProperty("是否启用(1:启用,0:不启用)")
    private String enable;

    /**
     * 时间类别(0:周,1:月,2:年)
     */
    @ApiModelProperty("时间类别(0:周,1:月,2:年)")
    private String timeType;

    /**
     * 设置类别(1:调度配置设置,2:保留时长设置)
     */
    @ApiModelProperty("设置类别(1:调度配置设置,2:保留时长设置)")
    private String setType;

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

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

}
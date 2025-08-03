package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ly_or_report_set
 */
@Data
public class ReportSetVo implements Serializable {

    private static final long serialVersionUID = -2601727833175360471L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 参数设置1
     */
    private String parameter1;

    /**
     * 参数设置2
     */
    private String parameter2;

    /**
     * 是否启用(1:启用,0:不启用)
     */
    private String enable;

    /**
     * 时间类别(0:周,1:月,2:年)
     */
    private String timeType;

    /**
     * 设置类别(1:调度配置设置,2:保留时长设置)
     */
    private String setType;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private Date operationTime;

    /**
     * 备注
     */
    private String remark;

}
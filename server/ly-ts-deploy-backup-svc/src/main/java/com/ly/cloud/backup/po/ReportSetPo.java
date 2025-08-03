package com.ly.cloud.backup.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * 
 * @TableName ly_or_report_set
 */
@TableName("ly_or_report_set")
@Data
public class ReportSetPo implements Serializable {

    private static final long serialVersionUID = -4048194644942628682L;

    /**
     * 主键
     */
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 参数设置1
     */
    @TableField(value = "parameter1")
    private String parameter1;

    /**
     * 参数设置2
     */
    @TableField(value = "parameter2")
    private String parameter2;

    /**
     * 是否启用(1:启用,0:不启用)
     */
    @TableField(value = "enable")
    private String enable;

    /**
     * 时间类别(0:周,1:月,2:年)
     */
    @TableField(value = "time_type")
    private String timeType;

    /**
     * 设置类别(1:调度配置设置,2:保留时长设置)
     */
    @TableField(value = "set_type")
    private String setType;

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

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}
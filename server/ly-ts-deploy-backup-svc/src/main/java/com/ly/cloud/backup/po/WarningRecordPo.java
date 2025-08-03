package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 告警记录信息表：ly_mw_warning_record
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_mw_warning_record")
public class WarningRecordPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 关联告警规则ID（外键）
     */
    @TableField(value = "rule_id")
    private String ruleId;

    /**
     * 警告目标
     */
    @TableField(value = "warning_object")
    private String warningObject;

    @TableField(value = "reality_warning_object")
    private String realityWarningObject;

    /**
     *告警接口
     */
    @TableField(value = "warning_interface")
    private String warningInterface;

    /**
     * 告警等级（枚举）
     */
    @TableField(value = "warning_level")
    private String warningLevel;

    /**
     * 异常告警类型（枚举）
     */
    @TableField(value = "exception_type")
    private String exceptionType;

    /**
     * 告警记录标记类型 (0.异常记录  1. 有效记录)
     */
    @TableField(value = "target")
    private String target;


    /**
     * 告警描述
     */
    @TableField(value = "warning_description")
    private String warningDescription;

    /**
     * 最近告警时间
     */
    @TableField(value = "warning_time")
    private java.util.Date warningTime;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time", fill = FieldFill.INSERT_UPDATE, update = "sysdate()")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;




}


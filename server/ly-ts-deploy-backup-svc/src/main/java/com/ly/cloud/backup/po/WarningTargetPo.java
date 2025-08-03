package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警记录信息表：ly_mw_warning_target
 *
 * @author ljb
 */
@Data
@TableName("ly_mw_warning_target")
public class WarningTargetPo implements Serializable {

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
     * 指标类型
     */
    @TableField(value = "target_type")
    private String targetType;

    /**
     * 指标名字
     */
    @TableField(value = "target_name")
    private String targetName;

    /**
     * sql语句
     */
    @TableField(value = "sql_line")
    private String sqlLine;

    /**
     * 操作符类型
     */
    @TableField(value = "operator_type")
    private String operatorType;

    /**
     *  是否开启
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
    @TableField(value = "operation_time", fill = FieldFill.INSERT_UPDATE, update = "sysdate()")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}


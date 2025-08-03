package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 数据库核验信息表：ly_rm_database_check
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_rm_database_check")
public class DatabaseCheckPo implements Serializable {

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
     * 数据源ID（外键）
     */
    @TableField(value = "source_id")
    private String sourceId;

    /**
     * 异常表名称
     */
    @TableField(value = "exception_table")
    private String exceptionTable;

    /**
     * 异常字段名称
     */
    @TableField(value = "exception_column")
    private String exceptionColumn;

    /**
     * 异常字段类型
     */
    @TableField(value = "column_type")
    private String columnType;

    /**
     * 异常字段长度
     */
    @TableField(value = "column_length")
    private String columnLength;

    /**
     * 异常描述
     */
    @TableField(value = "exception_description")
    private String exceptionDescription;

    /**
     * 处理状态（枚举）
     */
    @TableField(value = "work_status")
    private String workStatus;

    /**
     * 处理人
     */
    @TableField(value = "worker_id")
    private String workerId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time", fill = FieldFill.INSERT_UPDATE, update = "sysdate()")
    private java.util.Date operationTime;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}


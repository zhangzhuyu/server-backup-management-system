package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 异常工单信息表：ly_mw_work_order
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_mw_work_order")
public class WorkOrderPo implements Serializable {

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
     * 关联告警记录ID（外键）
     */
    @TableField(value = "record_id")
    private String recordId;

    /**
     * 处理状态（枚举）
     */
    @TableField(value = "work_status")
    private String workStatus;

    /**
     * 结果反馈
     */
    @TableField(value = "result_feedback")
    private String resultFeedback;

    /**
     * 异常反馈时间
     */
    @TableField(value = "feedback_time")
    private java.util.Date feedbackTime;

    /**
     * 异常处理开始时间
     */
    @TableField(value = "start_time")
    private java.util.Date startTime;

    /**
     * 异常处理结束时间
     */
    @TableField(value = "end_time")
    private java.util.Date endTime;

    /**
     * 是否忽略（枚举）
     */
    @TableField(value = "disregard")
    private String disregard;

    /**
     * 处理人ID
     */
    @TableField(value = "worker_id")
    private String workerId;

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


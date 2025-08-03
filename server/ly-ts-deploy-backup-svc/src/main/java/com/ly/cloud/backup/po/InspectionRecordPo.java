package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 巡检记录信息表：ly_si_inspection_record
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_si_inspection_record")
public class InspectionRecordPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 报告名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 报告文件ID
     */
    @TableField(value = "file_id")
    private String file_id;

    /**
     * 执行方式（枚举）
     */
    @TableField(value = "executive_mode")
    private String executive_mode;

    /**
     * 巡检策略ID（外键）
     */
    @TableField(value = "strategy_id")
    private String strategy_id;

    /**
     * 巡检结果（枚举）
     */
    @TableField(value = "inspection_result")
    private String inspection_result;

    /**
     * 巡检结果反馈
     */
    @TableField(value = "inspection_feedback")
    private String inspection_feedback;

    /**
     * 最近巡检开始时间
     */
    @TableField(value = "start_time")
    private java.util.Date start_time;

    /**
     * 最近巡检结束时间
     */
    @TableField(value = "end_time")
    private java.util.Date end_time;

    /**
     * 推送结果（枚举）
     */
    @TableField(value = "push_result")
    private String push_result;

    /**
     * 推送结果反馈
     */
    @TableField(value = "push_feedback")
    private String push_feedback;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operator_id;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operation_time;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}


package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 巡检策略信息表：ly_si_inspection_strategy
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_si_inspection_strategy")
public class InspectionStrategyPo implements Serializable {

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
     * 任务名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 巡检类型（枚举）
     */
    @TableField(value = "inspection_type")
    private String inspection_type;

    /**
     * 巡检调度方式（枚举）
     */
    @TableField(value = "scheduling_mode")
    private String scheduling_mode;

    /**
     * 巡检调度周期（枚举）【每月、每周、每天】
     */
    @TableField(value = "scheduling_period")
    private String scheduling_period;

    /**
     * 巡检调度执行时间
     */
    @TableField(value = "execution_time")
    private java.util.Date execution_time;

    /**
     * 巡检调度间隔时间
     */
    @TableField(value = "interval_time")
    private java.util.Date interval_time;

    /**
     * 首次巡检是否立即执行（枚举）
     */
    @TableField(value = "whether_immediate")
    private String whether_immediate;

    /**
     * 推送邮箱
     */
    @TableField(value = "push_email")
    private String push_email;

    /**
     * 推送钉钉
     */
    @TableField(value = "push_ding_talk")
    private String push_ding_talk;

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

    /**
     * cron表达式
     */
    @TableField(value = "cron")
    private String cron;

}


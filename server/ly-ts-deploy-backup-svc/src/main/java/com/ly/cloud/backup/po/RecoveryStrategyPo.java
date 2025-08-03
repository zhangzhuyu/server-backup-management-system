package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 自愈策略信息表：ly_fr_recovery_strategy
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_fr_recovery_strategy")
public class RecoveryStrategyPo implements Serializable {

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
     * 指标名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 自愈类型（枚举）
     */
    @TableField(value = "recovery_type")
    private String recovery_type;

    /**
     * 自愈对象（服务）
     */
    @TableField(value = "recovery_object")
    private String recovery_object;

    /**
     * 自愈条件（枚举）
     */
    @TableField(value = "recovery_condition")
    private String recovery_condition;

    /**
     * 是否启用故障自愈（枚举）
     */
    @TableField(value = "whether_enable")
    private String whether_enable;

    /**
     * 自愈尝试次数
     */
    @TableField(value = "recovery_count")
    private String recovery_count;

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


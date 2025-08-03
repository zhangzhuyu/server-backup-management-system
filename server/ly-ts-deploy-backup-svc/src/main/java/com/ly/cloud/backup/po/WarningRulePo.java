package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 告警规则信息表：ly_mw_warning_rule
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_mw_warning_rule")
public class WarningRulePo implements Serializable {

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
     * 指标对象（枚举）
     */
    @TableId(value = "target")
    private String target;

    /**
     * 指标类型（枚举）
     */
    @TableId(value = "type")
    private String type;

    /**
     * 指标名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 阈值操作符
     */
    @TableField(value = "threshold_operators1")
    private String thresholdOperators1;

    /**
     * 阈值
     */
    @TableField(value = "threshold1")
    private String threshold1;

    /**
     * 阈值单位
     */
    @TableField("threshold_unit1")
    private String thresholdUnit1;

    /**
     * 运行时间
     */
    @TableField(value = "running_time")
    private java.lang.String runningTime;

    /**
     * 运行时间单位
     */
    @TableField(value = "running_time_unit")
    private java.lang.String runningTimeUnit;

    /**
     * 重复次数操作符
     */
    @TableField(value = "repeat_operators")
    private java.lang.String repeatOperators;

    /**
     * 重复次数
     */
    @TableField(value = "repeat_value")
    private java.lang.String repeatValue;


    /**
     * 告警等级（枚举）
     */
    @TableField(value = "warning_level")
    private String warningLevel;

    /**
     * 预警对象
     */
    @TableField(value = "warning_object")
    private String warningObject;

    /**
     * 其他参数
     */
    @TableField(value = "param0")
    private String param0 ;

    /**
     * 是否启用 0 或 1
     */
    @TableField(value = "enable")
    private String enable;

    /**
     * 调度间隔
     */
    @TableField(value = "gap_value")
    private java.lang.String gapValue;

    /**
     * 调度间隔单位
     */
    @TableField(value = "gap_value_unit")
    private java.lang.String gapValueUnit;

    /**
     * 是否需要生成工单，0 或 1
     */
    @TableField(value = "enable_work_order")
    private java.lang.String enableWorkOrder;

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

    /**
     * 延迟值操作符
     */
    @TableField(value = "delay_operators")
    private String delayOperators;

    /**
     * 延迟值
     */
    @TableField(value = "delay")
    private String delay;

    /**
     * 延迟单位
     */
    @TableField(value = "delay_unit")
    private String delayUnit;


    /**
     *吞吐量操作符
     */
    @TableField(value = "throughput_operators")
    private String throughputOperators;

    /**
     * 吞吐值
     */
    @TableField(value = "throughput")
    private String throughput;

    /**
     * 吞吐单位
     */
    @TableField(value = "throughput_unit")
    private String throughputUnit;

    /**
     * 延迟与吞吐量条件关系符
     */
    @TableField(value = "delay_throughput_relator")
    private String delayThroughputRelator;

    /**
     * 推送模式(1-一次性,2-连续性,3-工作日,4-自定义)
     */
    @TableField(value = "push_mode")
    private String pushMode;

    /**
     * 间隔时间值（连续性推送方式专用字段）
     */
    @TableField(value = "interval_value")
    private Integer intervalValue;

    /**
     * 间隔时间单位(连续性推送方式专用字段)
     */
    @TableField(value = "interval_unit")
    private String intervalUnit;

    /**
     * 工作日(1-周一,2-周二,3-周三,4-周四,5-周五,6-周六,7-周日 工作日之间用逗号隔开)
     */
    @TableField("weekday")
    private String weekday;

    /**
     * 开始日期(只有自定义模式才有开始日期是时间)
     */
    @TableField("start_date")
    private String startDate;

    /**
     * 结束日期(只有自定义模式才有结束日期时间)
     */
    @TableField("end_date")
    private String endDate;

    /**
     * 开始时间(时，分，秒)
     */
    @TableField("start_time")
    private String startTime;

    /**
     * 结束时间(时，分，秒)
     */
    @TableField("end_time")
    private String endtime;

    /**
     * 统计数量
     */
    @TableField(exist = false)
    private int count;


}


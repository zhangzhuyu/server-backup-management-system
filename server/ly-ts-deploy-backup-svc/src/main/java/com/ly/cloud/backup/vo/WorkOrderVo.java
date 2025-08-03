package com.ly.cloud.backup.vo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 异常工单信息表
 *
 * @author chenguoqing
 */
@Data
@ApiModel
public class WorkOrderVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 关联告警记录ID（外键）
     */
    private String recordId;

    /**
     * 关联告警规则ID（外键）
     */
    private String ruleId;

    /**
     * 处理状态（枚举）
     */
    @ApiModelProperty("处理状态(-1 待处理 0 正在处理 1 忽略 2 已处理并解决 3 已处理未解决)")
    private String workStatus;


    /**
     * 结果反馈
     */
    private String resultFeedback;

    /**
     * 异常反馈时间
     */
    private java.util.Date feedbackTime;

    /**
     * 异常处理开始时间
     */
    private java.util.Date startTime;

    /**
     * 异常处理结束时间
     */
    private java.util.Date endTime;

    /**
     * 处理人ID
     */
    private String workerId;

    /**
     * 处理人
     */
    private String workerName;

    /**
     * 是否忽略（枚举）
     */
    private String disregard;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private java.util.Date operationTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 耗时
     */
    private String elapsedTime;

    /**
     * 指标对象（枚举）
     */
    private String targetObject;

    /**
     * 指标类型（枚举）
     */
    private String targetType;

    /**
     * 指标名称
     */
    private String targetName;

    /**
     * 告警等级
     */
    private String warningLevel;

    /**
     * 告警目标
     */
    private String warningObject;

    /**
     * 触发预警的对象
     */
    private String realityWarningObject;

    /**
     * 告警描述
     */
    private String warningDescription;

    /**
     * 异常告警类型
     */
    private String exceptionType;

    /**
     * 耗时
     */
    private String spend;

    /**
     * 处理时间段
     */
    private String handlerTime;

    /**
     * 处理状态中文
     */
    private String WorkStatusChinese;


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




}

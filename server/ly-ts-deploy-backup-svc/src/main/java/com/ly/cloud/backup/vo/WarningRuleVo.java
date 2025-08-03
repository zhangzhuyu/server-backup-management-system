package com.ly.cloud.backup.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 告警规则信息表
 *
 * @author chenguoqing
 */
@Data
public class WarningRuleVo implements Serializable {

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
     * 指标对象（枚举）
     */
    private String target;

    /**
     * 指标类型（枚举）
     */
    private String type;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 阈值
     */
    private String thresholdOperators1;

    /**
     * 阈值1
     */
    private String threshold1;

    /**
     * 阈值单位
     */
    private String thresholdUnit1;

    private java.lang.String runningTime;

    /**
     * 运行时间单位
     */
    private java.lang.String runningTimeUnit;

    /**
     * 重复次数操作符
     */
    private java.lang.String repeatOperators;

    /**
     * 重复次数
     */
    private java.lang.String repeatValue;



    /**
     * 告警等级（枚举）
     */
    private String warningLevel;

    /**
     * 预警对象
     */
    private String warningObject;

    /**
     * 是否启用
     */
    private String enable;

    /**
     * 调度间隔
     */
    private java.lang.String gapValue;

    /**
     * 调度间隔单位
     */
    private java.lang.String gapValueUnit;

    /**
     * 是否需要生成工单，1要，0不要，
     */
    private java.lang.String enableWorkOrder;

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

}

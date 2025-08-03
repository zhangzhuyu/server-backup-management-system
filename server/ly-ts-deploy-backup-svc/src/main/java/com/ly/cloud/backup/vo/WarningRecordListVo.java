package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警记录信息视图类
 *
 * @author jiangzhongxin
 */
@Data
public class WarningRecordListVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 4171523968592784624L;

    /**
     * 告警记录信息主键
     */
    private String id;

    /**
     * 规则id
     */
    private String ruleId;

    /**
     * 指标对象（枚举）
     */
    private String ruleObject;

    /**
     * 指标名称
     */
    private String ruleName;

    /**
     * 预警对象
     */
    private String warningObject;

    /**
     * 触发预警的对象
     */
    private String realityWarningObject;

    /**
     * 告警等级
     */
    private String warningLevel;

    /**
     * 异常告警类型
     */
    private String exceptionType;

    /**
     * 告警描述
     */
    private String warningDescription;

    /**
     * 最近告警时间
     */
    private String warningTime;

    /**
     * 是否监控，0 或 1
     */
//    private String whetherMonitor;

}

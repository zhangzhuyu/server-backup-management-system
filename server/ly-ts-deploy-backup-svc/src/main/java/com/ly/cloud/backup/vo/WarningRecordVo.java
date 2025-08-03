package com.ly.cloud.backup.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 告警记录信息表
 *
 * @author chenguoqing
 */
@Data
public class WarningRecordVo implements Serializable {

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
     * 关联告警规则ID（外键）
     */
    private String ruleId;

    /**
     *
     */
    private String warningObject;

    /**
     *告警接口
     */
    private String warningInterface;

    /**
     * IP地址(ipv4)
     */
    private String ipv4;

    /**
     * 告警等级（枚举）
     */
    private String warningLevel;

    /**
     * 异常告警类型（枚举）
     */
    private String exceptionType;

    /**
     * 告警描述
     */
    private String warningDescription;

    /**
     * 最近告警时间
     */
    private java.util.Date warningTime;

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
     * 记录类型:告警（1）和异常（0）
     */
    private String target;

}

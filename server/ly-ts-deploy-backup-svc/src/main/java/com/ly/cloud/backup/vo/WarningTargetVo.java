package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警记录信息表
 *
 * @author ljb
 */
@Data
public class WarningTargetVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9013609292510835983L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 指标类型
     */
    private String targetType;

    /**
     * 指标类型名称
     */
    private String targetTypeName;

    /**
     *  指标名称
     */
    private String targetName;

    /**
     * es-sql语句
     */
    private String sqlLine;

    /**
     * 操作符类型
     */
    private String operatorType;

    /**
     * 操作符类型
     */
    private String operatorTypeName;

    /**
     * 指标是否开启
     */
    private String enable;


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

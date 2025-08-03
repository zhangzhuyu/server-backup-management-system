package com.ly.cloud.backup.vo;

import lombok.Data;

/**
 * @Author ljb
 * @Date 2022/5/23
 */


@Data
public class WarningRecordObjectVo {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -5113609292510835997L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 关联告警规则ID（外键）
     */
    private String recordId;

    /**
     * 预警对象ID
     */
    private String warningObject;

    /**
     * 预警对象名称
     */
    private String warningObjectName;

    /**
     *告警接口
     */
    private String warningInterface;

    /**
     * 预警对象类型
     */
    private String objectType;

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
     * 统计数量
     */
    private int count;

    /**
     * 告警等级
     */
    private int warningLevel;

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 数据库名称
     */
    private String databaseName;


}

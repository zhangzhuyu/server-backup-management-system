package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警记录信息表
 *
 * @author chenguoqing
 */
@Data
public class WarningSubMethodVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -7003609292510835997L;

    /**
     * 主键
     */
    private Long id;

    /**
     *警告等级ID
     */
    private String levelId;

    /**
     *等级名字
     */
    private String levelName;

    /**
     *订阅方式列表
     */
    private String[] methodList;

    /**
     *是否启用钉钉
     */
    private String enableContent;

    /**
     * 等级对应的规则数
     */
    private Long levelRuleNum;


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
     * 对应等级的预警数
     */
    private Long num;


}

package com.ly.cloud.backup.dto;

import java.io.Serializable;

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
public class WorkOrderDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 关联告警记录ID（外键）
     */
    @ApiModelProperty("关联告警记录ID（外键）")
    private String recordId;

    /**
     * 处理状态（枚举）
     */
    @ApiModelProperty("处理状态(-1 待处理 0 正在处理 1 忽略 2 已处理并解决 3 已处理未解决)")
    private String workStatus;


    /**
     * 处理状态（枚举）
     */
    @ApiModelProperty("处理状态列表（多个状态之间使用逗号隔开）")
    private java.util.List<String> workStatusList;



    /**
     * 关告警规则类型
     */
    @ApiModelProperty("告警规则类型")
    private String target;

    @ApiModelProperty("告警规则类型列表（多个类型之间使用逗号隔开）")
    private java.util.List<String> targetList;

    /**
     * 告警等级（枚举）
     */
    @ApiModelProperty("告警等级（枚举）")
    private String warningLevel;

    @ApiModelProperty("告警等级列表（多个等级之间使用逗号隔开）")
    private java.util.List<String> warningLevelList;

    /**
     * 结果反馈
     */
    @ApiModelProperty("结果反馈")
    private String resultFeedback;

    /**
     * 异常反馈时间
     */
    @ApiModelProperty("异常反馈时间")
    private java.util.Date feedbackTime;

    /**
     * 异常处理开始时间
     */
    @ApiModelProperty("异常处理开始时间")
    private java.util.Date startTime;

    /**
     * 异常处理结束时间
     */
    @ApiModelProperty("异常处理结束时间")
    private java.util.Date endTime;

    /**
     * 处理人ID
     */
    @ApiModelProperty("处理人ID")
    private String workerId;

    /**
     * 是否忽略（枚举）
     */
    @ApiModelProperty("是否忽略")
    private String disregard;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

    /**
     * 主键数组
     */
    @ApiModelProperty("主键数组")
    private String[] ids;

    @ApiModelProperty("排序字段")
    private String columnKey;

    @ApiModelProperty("排序顺序")
    private String order;

}

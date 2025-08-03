package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 告警记录信息表
 *
 * @author chenguoqing
 */
@Data
public class WarningRecordDto implements Serializable {

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
     * 关联告警规则ID（外键）
     */
    @ApiModelProperty("关联告警规则ID（外键）")
    private String ruleId;

    /**
     * 关告警规则类型
     */
    @ApiModelProperty("告警规则类型")
    private String target;

    @ApiModelProperty("告警规则类型集合")
    private List<String> targetList;

    /**
     *告警对象名字(字符)
     */
    @ApiModelProperty("告警规则对应的对象")
    private String warningObject;

    /**
     * 实际触发的告警对象
     */
    @ApiModelProperty("实际触发的告警对象")
    private String realityWarningObject;

    /**
     *告警接口
     */
    @ApiModelProperty("告警接口")
    private String warningInterface;

    /**
     * 告警对象ID集合
     */
    @ApiModelProperty("告警对象ID集合")
    private List<String> warningObjectIds = new ArrayList<>();

    /**
     * 告警等级（枚举）
     */
    @ApiModelProperty("告警等级（枚举）")
    private String warningLevel;

    @ApiModelProperty("告警等级集合（枚举）")
    private List<String> warningLevelList;

    /**
     * 异常告警类型（枚举）
     */
    @ApiModelProperty("异常告警类型（枚举）")
    private String exceptionType;

    /**
     * 告警记录类型
     */
    @ApiModelProperty("告警记录类型(0.异常 1.有效)")
    private String recordType;

    @ApiModelProperty("告警记录类型(0.异常 1.有效)")
    private List<String> recordTypeList;

    /**
     * 告警描述
     */
    @ApiModelProperty("告警描述")
    private String warningDescription;

    /**
     * 警告描述 markdown 格式
     */
    private String warningDescriptionMarkdown;

    /**
     * 最近告警时间
     */
    @ApiModelProperty("最近告警时间")
    private java.util.Date warningTime;

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
     * 记录类型:告警（1）和异常（0）
     */
    @ApiModelProperty("记录类型")
    private String type;

    @ApiModelProperty("排序字段")
    private String columnKey;

    @ApiModelProperty("排序顺序")
    private String order;




}

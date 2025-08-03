package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 告警记录信息表
 *
 * @author ljb
 */
@Data
public class WarningTargetDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9013609292510835983L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 指标类型
     */
    @ApiModelProperty("指标类型")
    private String targetType;

    /**
     * 指标类型(列表)
     */
    @ApiModelProperty("指标类型列表(用于多选)")
    private List<String> targetTypeList;

    /**
     *  指标名称
     */
    @ApiModelProperty("指标名称")
    private String targetName;

    /**
     * es-sql语句
     */
    @ApiModelProperty("es-sql语句")
    private String sqlLine;

    /**
     * 操作符类型
     */
    @ApiModelProperty("操作符类型")
    private String operatorType;

    /**
     * 指标是否开启
     */
    @ApiModelProperty("指标状态(1-开启，0-禁用)")
    private String enable;


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

}

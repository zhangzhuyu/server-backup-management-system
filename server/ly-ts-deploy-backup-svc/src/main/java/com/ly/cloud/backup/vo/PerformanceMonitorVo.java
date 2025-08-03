package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class PerformanceMonitorVo implements Serializable {

    private static final long serialVersionUID = 7969422290253922556L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "自定义检测名称")
    private String name;

    @ApiModelProperty(value = "检测项")
    private String testItems;

    @ApiModelProperty(value = "是否启用(1-启用，0-禁用)")
    private String enable;

    @ApiModelProperty(value = "运行周期(1-每天，2-每周，3-每月)")
    private String operatingCycle;

    @ApiModelProperty(value = "运行时间")
    private String runTime;

    @ApiModelProperty(value = "目标源(字符串)")
    private String targetSource;

    @ApiModelProperty(value = "目标源(集合)")
    private List<String> targetSourceList;

    @ApiModelProperty(value = "指标/策略(字符串)")
    private String napeType;

    @ApiModelProperty(value = "指标/策略(集合)")
    private List<String> napeTypeList;

    @ApiModelProperty("运行日期(当运行周期为每周是(1-星期一...7-星期日),当运行周期为每月时(1-这个月的第一天,...31-这个月的第三十一天))之间使用逗号隔开")
    private String operationDate;

    @ApiModelProperty("策略模式id")
    private String strategicModeId;

}

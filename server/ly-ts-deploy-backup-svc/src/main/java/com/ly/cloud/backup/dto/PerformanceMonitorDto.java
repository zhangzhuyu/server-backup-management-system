package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class PerformanceMonitorDto implements Serializable {

    private static final long serialVersionUID = -4103089400407774618L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty(value = "自定义检测名称")
    private String name;

    @ApiModelProperty(value = "关联项类型，多个项目类型使用逗号隔开")
    private String napeType;

    @ApiModelProperty(value = "关联项类型，集合")
    private List<String> napeTypeList;

    @ApiModelProperty(value = "是否启用(1-启用，0-禁用)")
    private String enable;

    @ApiModelProperty(value = "运行周期(1-每天，2-每周，3-每月)")
    private String operatingCycle;

    @ApiModelProperty(value = "运行时间")
    private String runTime;

    @ApiModelProperty(value = "关联目标源id，多个目标源使用逗号隔开")
    private String targetSourceId;

    @ApiModelProperty(value = "关联目标源")
    private List<String> targetSourceList;

    @ApiModelProperty("关键字(查询条件)")
    private String searchKey;

    @ApiModelProperty("运行日期(当运行周期为每周是(1-星期一...7-星期日),当运行周为每月时(1-这个月的第一天,...31-这个月的第三十一天))之间使用逗号隔开")
    private String operationDate;

    @ApiModelProperty("策略模式id")
    private String strategicModeId;

}

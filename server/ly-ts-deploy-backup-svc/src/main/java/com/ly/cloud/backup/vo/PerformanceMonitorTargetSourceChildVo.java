package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class PerformanceMonitorTargetSourceChildVo {

    @ApiModelProperty("名称")
    private String name;


    @ApiModelProperty("数据")
    private List<PerformanceMonitorTargetSourceChildDataVo> data;
}

package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PerformanceMonitorTargetSourceChildDataVo {

    @ApiModelProperty("关联的资源id")
    private String sourceId;

    @ApiModelProperty("值")
    private String value;
}

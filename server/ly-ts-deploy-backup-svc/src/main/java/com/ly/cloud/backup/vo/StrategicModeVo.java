package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class StrategicModeVo {

    @ApiModelProperty(value = "主键")
    private String value;

    @ApiModelProperty(value = "策略模式名称")
    private String label;
}

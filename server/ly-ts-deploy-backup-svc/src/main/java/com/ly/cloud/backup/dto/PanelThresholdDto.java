package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("监控面板预警阈值")
@Data
public class PanelThresholdDto {
    @ApiModelProperty("小于或等于")
    private Double lte;
    @ApiModelProperty ("大于或等于")
    private Double gte;
    @ApiModelProperty("介于底线")
    private Double btwLow;
    @ApiModelProperty("介于顶线")
    private Double btwUpper;
}

package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("解决返回前端的图表，一个时间好多个值的问题用的map结构体")
@Data
public class PanelAvgMapDto {
    @ApiModelProperty("个数")
    private Integer count;
    @ApiModelProperty("总值")
    private Double total;
}

package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("饼图vo")
@Data
public class RingPieChartVo {
    @ApiModelProperty("成份名")
    private String name;
    @ApiModelProperty("数值")
    private String value;
}

package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("oracle osstat/pga/sga饼图vo")
@Data
public class RingItemDetailVo {
    @ApiModelProperty("百分比")
    private Double value;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("总量")
    private String size;
}

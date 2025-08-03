package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("oracle系统,sga,pga内存饼图vo")
@Data
public class OracleMemRingPieChartVo {
    @ApiModelProperty("使用率")
    List<RingItemDetailVo> data;
}

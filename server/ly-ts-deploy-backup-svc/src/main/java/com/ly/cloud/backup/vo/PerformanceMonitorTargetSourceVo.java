package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class PerformanceMonitorTargetSourceVo {
    private static final long serialVersionUID = 5833419163187425148L;

    @ApiModelProperty("目标源类型下的名称")
    private String name;

    @ApiModelProperty("目标源类型(1-主机,2-数据库,4-应用,5-中间件)")
    private String type;

    @ApiModelProperty("目标源类型名称(值为主机，数据库，应用，中间件)")
    private String typeName;

    @ApiModelProperty("子类数据")
    private List<PerformanceMonitorTargetSourceChildVo> list;


}

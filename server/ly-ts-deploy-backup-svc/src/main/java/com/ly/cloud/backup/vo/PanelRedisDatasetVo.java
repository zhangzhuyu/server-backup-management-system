package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("redis监控面板列表项")
@Data
public class PanelRedisDatasetVo {
    @ApiModelProperty("属性")
    private String attribute;
    @ApiModelProperty("键值")
    private Object keycode;
}

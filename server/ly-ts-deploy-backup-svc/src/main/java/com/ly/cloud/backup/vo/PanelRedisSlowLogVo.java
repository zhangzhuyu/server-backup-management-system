package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("redis面板慢查询列表项")
@Data
public class PanelRedisSlowLogVo {
    @ApiModelProperty("指令")
    private String command;
    @ApiModelProperty("处理于")
    private String timestamp;
    @ApiModelProperty("duration")
    private String duration;
}

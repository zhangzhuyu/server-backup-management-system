package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("监控面板对像ip及健康状态")
@Data
public class PanelObjIPStatusDto {
    @ApiModelProperty("ip")
    private String ip;
    @ApiModelProperty("健康状态")
    private String status;
}

package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("获取主机真实ip地址")
@Data
public class PanelExcludeIPDto {
    @ApiModelProperty("排除片断")
    private String excludedIpSegment;
    @ApiModelProperty("排除ip")
    private String excludedFixedIp;
}

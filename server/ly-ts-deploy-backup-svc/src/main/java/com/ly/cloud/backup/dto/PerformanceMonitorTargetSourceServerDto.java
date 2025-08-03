package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PerformanceMonitorTargetSourceServerDto {
    private static final long serialVersionUID = 7464440650558842949L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty(value = "所属ip")
    private String ip;

    @ApiModelProperty(value = "所属主机id")
    private Long serverId;

    @ApiModelProperty(value = "域名,多个域名使用逗号隔开")
    private String realmName;

    @ApiModelProperty(value = "磁盘，多个磁盘使用逗号隔开")
    private String disc;


}

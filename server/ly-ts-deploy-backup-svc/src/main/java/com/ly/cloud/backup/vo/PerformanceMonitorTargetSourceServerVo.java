package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class PerformanceMonitorTargetSourceServerVo {
    private static final long serialVersionUID = 7464440650558842949L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty(value = "所属ip")
    private String ip;

    @ApiModelProperty(value = "关联主机id")
    private Long serverId;

    @ApiModelProperty(value = "域名(字符串类型)")
    private String realmName;

    @ApiModelProperty(value = "域名(集合)")
    private List<String> realmNameList;

    @ApiModelProperty(value = "磁盘(字符串类型)")
    private String disc;

    @ApiModelProperty(value = "磁盘(集合)")
    private List<String> discList;

}

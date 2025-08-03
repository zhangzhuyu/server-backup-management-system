package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PerformanceMonitorTargetSourceDatabaseVo {
    private static final long serialVersionUID = -3603921422817260577L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("关联数据库id")
    private Long dataBaseId;


    @ApiModelProperty("关联数据库类型")
    private String dataBaseType;

    @ApiModelProperty("关联数据库url")
    private String dataBaseUrl;



}

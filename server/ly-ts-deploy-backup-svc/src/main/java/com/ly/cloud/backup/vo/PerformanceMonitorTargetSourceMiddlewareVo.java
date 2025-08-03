package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PerformanceMonitorTargetSourceMiddlewareVo {
    private static final long serialVersionUID = 4855240235062647093L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("关联中间件id")
    private Long middlewareId;


    @ApiModelProperty("中间件类型")
    private String middlewareType;

    @ApiModelProperty("中间件url")
    private String middlewareUrl;

}

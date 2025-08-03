package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PerformanceMonitorTargetSourceApplicationVo {
    private static final long serialVersionUID = -4230927952713600873L;

    @ApiModelProperty("主键")
    private Long id;


    @ApiModelProperty("关联应用id")
    private Long applicationId;


    @ApiModelProperty("关联应用url")
    private String applicationUrl;

}

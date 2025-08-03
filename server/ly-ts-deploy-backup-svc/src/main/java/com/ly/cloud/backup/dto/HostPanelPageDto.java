package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("主机监控面板分页列表请求参数")
@Data
public class HostPanelPageDto {
    @ApiModelProperty("字段列表")
    private List<String> fields;

    @ApiModelProperty("时间起点")
    private String startTime;

    @ApiModelProperty("终点时间")
    private String endTime;

//    @ApiModelProperty("勾选字段是否有改动")
//    private Boolean fieldsChanged;

}

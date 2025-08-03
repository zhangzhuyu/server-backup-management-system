package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("监控面板各组件列表--主机,nginx,docker,redis,mysql,oracle,rabbitmq等")
@Data
public class PanelIndexVo {
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("名字")
    private String name;
    @ApiModelProperty("描述")
    private String desc;
    @ApiModelProperty("实例数")
    private String instance;
    @ApiModelProperty("告警数")
    private String alert;

}

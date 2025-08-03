package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class LyDbBackupDetailsVo implements Serializable {

    private static final long serialVersionUID = -2821042226118037765L;

    @ApiModelProperty("详情")
    private String details;

    @ApiModelProperty("状态")
    private String status;


}

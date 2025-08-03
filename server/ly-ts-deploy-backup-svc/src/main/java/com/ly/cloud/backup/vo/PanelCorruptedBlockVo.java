package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("监控面板oracle数据库坏块vo")
@Data
public class PanelCorruptedBlockVo {
    @ApiModelProperty("")
    private Object owner;
    @ApiModelProperty("")
    private Object type;
    @ApiModelProperty("")
    private Object segment_name ;
    @ApiModelProperty("")
    private Object partition_name;
    @ApiModelProperty("")
    private Object file;
    @ApiModelProperty("")
    private Object start_block;
    @ApiModelProperty("")
    private Object end_block;
    @ApiModelProperty("")
    private Object block_corrupted ;
    @ApiModelProperty("")
    private Object description;
}

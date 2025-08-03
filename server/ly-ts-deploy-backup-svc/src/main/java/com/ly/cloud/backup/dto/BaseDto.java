package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页的参数
 */
@Data
public class BaseDto implements Serializable {

    private static final long serialVersionUID = 8763700094891693074L;


    /**
     * 第几页（3.0版本的，需要修改）
     */
    @ApiModelProperty("第几页")
    private long page;

    /**
     * 每页多少条（3.0版本的，需要修改）
     */
    @ApiModelProperty("每页多少条")
    private long pageSize;
/*
    *//**
     * 第几页（2.0版本的，需要修改）
     *//*
    @ApiModelProperty("第几页")
    private long current;

    *//**
     * 每页多少条（2.0版本的，需要修改）
     *//*
    @ApiModelProperty("每页多少条")
    private long size;*/

}

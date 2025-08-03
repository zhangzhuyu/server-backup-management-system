package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * taier新增数据库数据源信息
 *
 * @author zhangzhuyu
 */
@Data
public class AddOrUpdateSourceDto implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 数据源类型
     */
    @ApiModelProperty("数据源类型")
    private String dataType;

    /**
     * 数据源名称
     */
    @ApiModelProperty("数据源名称")
    private String dataName;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String dataDesc;

    /**
     * dataJsonString
     */
    @ApiModelProperty("dataJsonString")
    private String dataJsonString;

    /**
     * jdbc的Url
     */
    @ApiModelProperty("jdbc的Url")
    private String jdbcUrl;

    /**
     * 数据库用户
     */
    @ApiModelProperty("数据库用户")
    private String username;

    /**
     * 数据库密码
     */
    @ApiModelProperty("数据库密码")
    private String password;

    //以下为卡夫卡新增字段

    /**
     * kafka
     */
    @ApiModelProperty("kafka类型")
    private String kafkaType;

    /**
     * kafka版本
     */
    @ApiModelProperty("kafka版本")
    private String dataVersion;

    /**
     * authentication
     */
    @ApiModelProperty("authentication")
    private String authentication;

    /**
     * brokerList
     */
    @ApiModelProperty("brokerList")
    private String brokerList;
}





































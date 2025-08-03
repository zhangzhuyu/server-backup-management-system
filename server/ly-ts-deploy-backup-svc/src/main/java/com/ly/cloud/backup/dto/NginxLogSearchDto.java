package com.ly.cloud.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * nginx日志信息搜索dto
 *
 * @author chenguoqing
 */
@Data
public class NginxLogSearchDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 5381180955919645975L;

    /**
     * id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 服务名
     */
    @ApiModelProperty(value = "服务名")
    private String name;

    /**
     * hostName
     */
    @ApiModelProperty(value = "hostName")
    private String hostName;

    /**
     * ip
     */
    @ApiModelProperty(value = "ip")
    private String ip;

    /**
     * 不限行数
     */
    @ApiModelProperty("不限行数")
    private Boolean unlimitLine;

    /**
     * 行数
     */
    @ApiModelProperty("行数")
    private Integer tail;

    /**
     * 错误行定位
     */
    @ApiModelProperty("错误行定位：true or false")
    private Boolean errorLinesAutoPositioning;

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

}

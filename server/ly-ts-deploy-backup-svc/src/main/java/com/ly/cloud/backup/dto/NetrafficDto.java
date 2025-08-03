package com.ly.cloud.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 流量监控列表
 * @author jiangzhongxin
 */
@Data
public class NetrafficDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -489765828718568551L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * NginxId
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long nginxId;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 应用访问地址
     */
    private String appAccessAddress;

    /**
     * 告警等级 -1 正常 0 一般 1 告警 2 严重 3 紧急
     */
    @ApiModelProperty("告警等级 -1 正常 0 一般 1 告警 2 严重 3 紧急")
    private String level;

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

}
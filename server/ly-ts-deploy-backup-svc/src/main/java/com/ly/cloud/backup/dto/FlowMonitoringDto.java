package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 流量监控表
 * @author chenguoqing
 *
 */
@Data
public class FlowMonitoringDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
     @ApiModelProperty("主键")
    private Long id;
    
     /**
     * 应用id
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
     @ApiModelProperty("应用id")
    private Long applicationId;

     /**
     * NginxId
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
     @ApiModelProperty("NginxId")
    private Long nginxId;
    
     /**
     * Nginx反向代理地址
     */
     @ApiModelProperty("Nginx反向代理地址")
    private String agentAddress;

    /**
     * Nginx反向代理地址(location)
     */
    @ApiModelProperty("agentAddressName")
    private String agentAddressName;

     /**
     * 操作时间
     */
     @ApiModelProperty("操作时间")
    private java.util.Date operationTime;
    
     /**
     * 操作人ID
     */
     @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 序号
     */
    @ApiModelProperty("序号")
    private String serialNumber;

     /**
     * 备注
     */
     @ApiModelProperty("备注")
    private String remark;

     //自增属性
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

    /**
     * 时间类型
     */
    @ApiModelProperty("时间类型")
    private int time;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("开始结束时间数组")
    private Date[] startAndEndTime;

}

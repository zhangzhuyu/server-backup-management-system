package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author admin
 */
@Data
public class ElasticSearchDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -2326863976229920371L;

    /**
     * 名称
     */
    @NotBlank
    @ApiModelProperty("名称")
    private String name;

    /**
     * ip
     */
    @ApiModelProperty("ip")
    private String ip;

    /**
     * 服务ID
     */
    @ApiModelProperty("service_id")
    private Long serviceId;

    /**
     * 服务containerID
     */
    @ApiModelProperty("containerID")
    private String containerID;

    /**
     * 换行符是否为html格式(0不是，1是 非必传)
     */
    @ApiModelProperty("换行符是否为html格式(0不是，1是 非必传)")
    private Integer lineBreak;

    /**
     * 时间类型
     */
    @ApiModelProperty("时间类型")
    private String dateType;

    /**
     * 时间参数
     */
    @ApiModelProperty("时间参数")
    private Integer dateParam;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;


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
     * 时间类型
     */
    @ApiModelProperty("时间类型")
    private int time;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String startTimeString;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private String endTimeString;

    /**
     * 组件类型
     */
    @ApiModelProperty("组件类型")
    private String componentType;

    /**
     * 日志类型
     */
    @ApiModelProperty("日志类型")
    private String logType;
}

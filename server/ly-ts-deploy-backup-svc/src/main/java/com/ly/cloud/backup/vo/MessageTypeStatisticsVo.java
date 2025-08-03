package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class MessageTypeStatisticsVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 5969174682979898769L;

    @ApiModelProperty("发送方式(枚举)（钉钉:1、邮件:2、短信:3、企业微信:0）")
    private Integer messageType;

    @ApiModelProperty("发送成功次数")
    private Long sendingSucceeded;

    @ApiModelProperty("发送失败次数")
    private Long sendingFail;

    @ApiModelProperty("总发送次数")
    private Long sendingStatistics;
}

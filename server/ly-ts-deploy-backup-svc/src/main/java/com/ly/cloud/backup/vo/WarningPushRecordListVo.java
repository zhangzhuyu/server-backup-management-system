package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class WarningPushRecordListVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 5969174682975898769L;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;


    /**
     * 预警规则名称
     */
    @ApiModelProperty("预警规则名称")
    private String name;

    /**
     * 预警规则id
     */
    @ApiModelProperty("规则id")
    private String ruleId;


    /**
     * 规则下的发送方式次数的统计信息
     */
    @ApiModelProperty("规则下发送方式次数的统计信息")
    List<MessageTypeStatisticsVo> messageTypeStatistics;

}

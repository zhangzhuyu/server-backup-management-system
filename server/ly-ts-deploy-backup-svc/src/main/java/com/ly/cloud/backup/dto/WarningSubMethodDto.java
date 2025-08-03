package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警记录信息表
 *
 * @author chenguoqing
 */
@Data
public class WarningSubMethodDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -8003609292510835997L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     *
     */
    @ApiModelProperty("警告等级ID")
    private String levelId;

    /**
     *
     */
    @ApiModelProperty("等级名字")
    private String levelName;

    /**
     *
     */
    @ApiModelProperty("订阅方式列表")
    private String[]  methodList;


    /**
     *
     */
    @ApiModelProperty("是否启用的方式")
    private String enableContent;



    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;


}

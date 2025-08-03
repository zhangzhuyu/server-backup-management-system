package com.ly.cloud.backup.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据库适配器信息
 *
 * @author chenguoqing
 */
@Data
public class AdapterDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 适配器名称
     */
    @ApiModelProperty("适配器名称")
    private String name;

    /**
     * 资源包名称
     */
    @ApiModelProperty("资源包名称")
    private String resourceName;

    /**
     * 资源包版本
     */
    @ApiModelProperty("资源包版本")
    private String resourceVerson;

    /**
     * 资源包兼容版本
     */
    @ApiModelProperty("资源包兼容版本")
    private String compatibleVerson;

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
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;


}

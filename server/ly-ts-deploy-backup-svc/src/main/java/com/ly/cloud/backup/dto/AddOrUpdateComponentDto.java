package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据库适配器信息
 *
 * @author zhangzhuyu
 */
@Data
public class AddOrUpdateComponentDto implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 是否默认
     */
    @ApiModelProperty("是否默认")
    private Boolean isDefault;

    /**
     * 适配器名称
     */
    @ApiModelProperty("适配器名称")
    private Integer storeType;

    /**
     * 资源包名称
     */
    @ApiModelProperty("资源包名称")
    private String principal;

    /**
     * 资源包版本
     */
    @ApiModelProperty("资源包版本")
    private String principals;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private String versionName;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String componentConfig;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private Integer deployType;

    /**
     * 资源包兼容版本
     */
    @ApiModelProperty("资源包兼容版本")
    private Integer clusterId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private String componentCode;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String kerberosFileName;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String resources1;


}

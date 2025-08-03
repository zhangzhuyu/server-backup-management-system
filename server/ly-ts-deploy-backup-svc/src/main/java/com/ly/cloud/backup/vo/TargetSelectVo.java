package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 公共下拉数据源
 * @author jiangzhongxin
 */
@Data
public class TargetSelectVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3772094455938952958L;

    /**
     * 下拉框中的文本
     */
    @ApiModelProperty("下拉框中的文本")
    private String label;

    /**
     * 下拉框中的值
     */
    @ApiModelProperty("下拉框中的值")
    private String value;

    /**
     * fid
     */
    @ApiModelProperty("fid")
    private String parentValue;

    /**
     * type
     */
    @ApiModelProperty("type")
    private String type;
}
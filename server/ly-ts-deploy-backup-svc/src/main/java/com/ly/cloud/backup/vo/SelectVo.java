package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 公共下拉数据源
 * @author jiangzhongxin
 */
@Data
public class SelectVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3992094455938952958L;

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
     * 下拉框中的值
     */
    @ApiModelProperty("下拉框中的url名称")
    private String title;

    /**
     * fid
     */
    @ApiModelProperty("fid")
    private String parentValue;

    /**
     * 统计值
     */
    private int count;

}
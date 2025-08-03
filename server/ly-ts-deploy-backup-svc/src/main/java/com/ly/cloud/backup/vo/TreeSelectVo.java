package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 树选择器数据源
 *
 * @author admin
 */
@Data
public class TreeSelectVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 2158077139491324528L;

    /**
     * 唯一key值
     */
    @ApiModelProperty("唯一key值")
    private String key;

    /**
     * 下拉框中的值
     */
    @ApiModelProperty("下拉框中的值")
    private String value;

    /**
     * 下拉框中的文本
     */
    @ApiModelProperty("下拉框中的文本")
    private String title;

    /**
     * 下拉框中的其他文本
     */
    @ApiModelProperty("下拉框中的其他文本")
    private String otherTitle;

    /**
     * 父级value值
     */
    @ApiModelProperty("父级value值")
    private String parentValue;


    /**
     * 子集
     */
    @ApiModelProperty("子集")
    private List<TreeSelectVo> children;

}
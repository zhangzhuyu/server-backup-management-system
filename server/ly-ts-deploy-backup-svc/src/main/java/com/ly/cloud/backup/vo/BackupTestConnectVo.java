package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 公共下拉数据源
 * @author zhangzhuyu
 */
@Data
public class BackupTestConnectVo implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = 3922094454918962957L;

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

}























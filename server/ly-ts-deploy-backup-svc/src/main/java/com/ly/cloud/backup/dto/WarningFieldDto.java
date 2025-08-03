package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警字段信息表
 *
 * @author ljb
 */
@Data
public class WarningFieldDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -3190609292510835903L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 字段中文名
     */
    @ApiModelProperty("字段中文名")
    private String chineseName;

    /**
     *  字段英文列名
     */
    @ApiModelProperty("字段英文列名")
    private String englishName;

    /**
     * 所属es表名
     */
    @ApiModelProperty("所属es表名")
    private String esTable;

    /**
     * 测试sql
     */
    @ApiModelProperty("sql")
    private String sql;

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

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

}

package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警字段信息表
 *
 * @author ljb
 */
@Data
public class WarningFieldVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -3190609292510835903L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 字段中文名
     */
    private String chineseName;

    /**
     *  字段英文列名
     */
    private String englishName;

    /**
     * 所属es表名
     */
    private String esTable;



    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private java.util.Date operationTime;

    /**
     * 备注
     */
    private String remark;



}

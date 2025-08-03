package com.ly.cloud.backup.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 业务领域代码视图 dto
 * @author SYC
 */
@Data
public class BusinessDomainDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 代码项中文名称
     */
    private String codeName;

    /**
     * 代码项值
     */
    private String codeKey;

    /**
     * 操作时间
     */
    private java.util.Date operationTime;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 检索关键字
     */
    private String keyword;

}


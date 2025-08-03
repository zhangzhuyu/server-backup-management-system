package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 业务领域代码视图 dto
 * @author SYC
 */
@Data
public class BusinessDomainVo implements Serializable {

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
     * 统计数量
     */
    private int appCount;

    /**
     * 角度
     */
    private int degree;

    /**
     *  健康状态（枚举）
     */
    private String healthStatus;

    /**
     * 是否在中央展示
     */
    private boolean centerTextShow;

    /**
     * 应用列表
     */
    private List<ApplicationVo> appList;
}


package com.ly.cloud.backup.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 数据库适配器信息
 *
 * @author chenguoqing
 */
@Data
public class AdapterVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 适配器名称
     */
    private String name;

    /**
     * 资源包名称
     */
    private String resourceName;

    /**
     * 资源包版本
     */
    private String resourceVerson;

    /**
     * 资源包兼容版本
     */
    private String compatibleVerson;

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


}

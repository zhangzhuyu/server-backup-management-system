package com.ly.cloud.backup.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 数据库核验信息表
 *
 * @author chenguoqing
 */
@Data
public class DatabaseCheckVo implements Serializable {

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
     * 数据源ID（外键）
     */
    private String sourceId;

    /**
     * 异常表名称
     */
    private String exceptionTable;

    /**
     * 异常字段名称
     */
    private String exceptionColumn;

    /**
     * 异常字段类型
     */
    private String columnType;

    /**
     * 异常字段长度
     */
    private String columnLength;

    /**
     * 异常描述
     */
    private String exceptionDescription;

    /**
     * 处理状态（枚举）
     */
    private String workStatus;

    /**
     * 处理人
     */
    private String workerId;

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

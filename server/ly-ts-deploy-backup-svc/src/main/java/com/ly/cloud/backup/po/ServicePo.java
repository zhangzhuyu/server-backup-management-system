package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 服务信息表：ly_rm_service
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_rm_service")
public class ServicePo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id")
    private Long id;

    /**
     * 服务英文名称
     */
    @TableField(value = "english_name")
    private String englishName;

    /**
     * 服务中文名称
     */
    @TableField(value = "chinese_name")
    private String chineseName;

    /**
     * 应用ID（外键）
     */
    @TableField(value = "application_id")
    private String applicationId;

    /**
     * 关联数据库（可多选）
     */
    @TableField(value = "database_id")
    private String databaseId;

    /**
     * 部署方式（中间件）（枚举）
     */
    @TableField(value = "deployment_way")
    private String deploymentWay;

    /**
     * 健康状态（枚举）
     */
    @TableField(value = "health_status")
    private String healthStatus;

    /**
     * 服务来源（1：apm,2:本地）
     */
    @TableField(value = "source")
    private String source;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 配置文件所在服务器id
     */
    @TableField(value = "server_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long serverId;

    /**
     * 配置文件路径
     */
    @TableField(value = "configuration_path")
    private String configurationPath;

}


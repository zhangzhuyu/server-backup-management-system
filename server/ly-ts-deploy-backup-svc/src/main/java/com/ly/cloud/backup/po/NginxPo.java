package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * nginx信息表：ly_rm_nginx
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_rm_nginx")
public class NginxPo implements Serializable {

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
     * 应用名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 访问地址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 关联服务器（可多选）
     */
    @TableField(value = "server_id")
    private String serverId;

    /**
     * 部署方式（中间件）（枚举）
     */
    @TableField(value = "deployment_way")
    private String deploymentWay;

    /**
     * 部署路径
     */
    @TableField(value = "deployment_path")
    private String deploymentPath;

    /**
     * 健康状态
     */
    @TableField("health_status")
    private String healthStatus;

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
     * 序号
     */
    @TableField("serial_number")
    private String serialNumber;

}


package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 应用信息表：ly_rm_application
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_rm_application")
public class ApplicationPo implements Serializable {

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
     * 业务领域（枚举）
     */
    @TableField(value = "business_domain")
    private String businessDomain;

    /**
     * 应用中文名称
     */
    @TableField(value = "chinese_name")
    private String chineseName;

    /**
     * 应用英文名称
     */
    @TableField(value = "english_name")
    private String englishName;

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
     * 是否加入运维大屏展示（枚举）
     */
    @TableField(value = "whether_monitoring")
    private String whetherMonitoring;

    /**
     * 是否初次运行（枚举）
     */
    @TableField("initial_operation")
    private String initialOperation;

    /**
     * 健康状态（枚举）
     */
    @TableField(value = "health_status")
    private String healthStatus;

    /**
     * 健康检测地址
     */
    @TableField(value = "health_monitoring_url")
    private String healthMonitoringUrl;

    /**
     * 序号
     */
    @TableField(value = "serial_number")
    private String serialNumber;

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
     * 所属公司
     */
    @TableField("affiliated_company")
    private Long affiliatedCompany;

    /**
     * 部署路径
     */
    @TableField("deployment_path")
    private String deploymentPath;

    /**
     * 统计数量
     */
    @TableField(exist = false)
    private int appCount;

    /**
     * 部署路径
     */
    @TableField("logo")
    private String logo;

}


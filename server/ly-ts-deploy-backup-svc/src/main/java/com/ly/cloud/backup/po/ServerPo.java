package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ly.cloud.backup.common.annotation.Sm4Field;
import lombok.Data;

/**
 * 服务器信息表：ly_rm_server
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_rm_server")
public class ServerPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * hostName
     */
    @TableField(value = "hostname")
    private String hostName;

    /**
     * 业务角色（枚举）
     */
    @TableField(value = "business_worker")
    private String businessWorker;

    /**
     * IP地址(ipv4)
     */
    @TableField(value = "ipv4")
    private String ipv4;

    /**
     * IP地址(ipv6)
     */
    @TableField(value = "ipv6")
    private String ipv6;

    /**
     * 服务器类型（枚举）
     */
    @TableField(value = "server_type")
    private String serverType;

    /**
     * 端口
     */
    @TableField(value = "port")
    private String port;

    /**
     * 账号
     */
    @TableField(value = "user")
    private String user;

    /**
     * 密码
     */
    @Sm4Field
    @TableField(value = "password")
    private String password;

    /**
     * 是否加入运维大屏展示（枚举）
     */
    @TableField(value = "whether_monitoring")
    private String whetherMonitoring;

    /**
     * 所属公司
     */
    @TableField("affiliated_company")
    private Long affiliatedCompany;

    /**
     * 健康状态（枚举）
     */
    @TableField(value = "health_status")
    private String healthStatus;

    /**
     * 防火墙状态
     */
    @TableField(value = "firewall_state")
    private String firewallState;

    /**
     * 系统类型
     */
    @TableField(value = "system_type")
    private String systemType;

    /**
     * 测试状态
     */
    @TableField("test_status")
    private String testStatus;

    /**
     * 配置
     */
    @TableField("configuration")
    private String configuration;

    /**
     * Agent状态(1:未安装，2:安装中,3:安装失败,4:运行中,5:已停止)
     */
    @TableField("agent_status")
    private String agentStatus;

    /**
     * oracle用户
     */
    @TableField("oracle_user")
    private String oracleUser;

    /**
     * oracle密码
     */
    @TableField("oracle_password")
    private String oraclePassword;

    /**
     * oracle备份路径
     */
    @TableField("oracle_backup_path")
    private String oracleBackupPath;

    /**
     * mongodb备份路径
     */
    @TableField("mongodb_backup_path")
    private String mongodbBackupPath;

    /**
     * mysql备份路径
     */
    @TableField("mysql_backup_path")
    private String mysqlBackupPath;

    /**
     * tidb备份路径
     */
    @TableField("tidb_backup_path")
    private String tidbBackupPath;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 序号
     */
    @TableField(value = "serial_number")
    private Integer serialNumber;

    /**
     * 是否开通外网
     */
    @TableField(value = "whether_outer_net")
    private String whetherOuterNet;

    @TableField(value = "auth_dept_id")
    private String authDeptId;

    @TableField(value = "deployment_ip")
    private String deploymentIp;

    @TableField(value = "metricbeat_log_backup_path")
    private String metricbeatLogBackupPath;

    @TableField(value = "filebeat_log_backup_path")
    private String filebeatLogBackupPath;

    @TableField(value = "agent_status_remark")
    private String agentStatusRemark;


}


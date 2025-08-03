package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ly_db_backup_management")
public class BackupManagementPo implements Serializable {

    private static final long serialVersionUID = 1251139891893471379L;

    @TableId(value = "id")
    private Long id;

    /**
     * 备份方式（枚举）（1为数据库备份、2为服务器工作目录备份、3为核心表备份）
     */
    @TableField(value = "backup_way")
    private String backupWay;

    /**
     * 数据源类型（1为Oracle、2为mysql、6为mongo）
     */
    @TableField(value = "data_source_type")
    private String dataSourceType;

    /**
     * 全部方式（1为mysqldump，2为exp，3为expdb，4为mongodump，5为http，6为ssh，7为ftp，8为cifs，9为cdc）
     */
    @TableField(value = "total_method")
    private String totalMethod;

    /**
     * 客户端服务器id
     *strategy = FieldStrategy.IGNORED 解决当改属性为空时，不修改
     */
    @TableField(value = "server_id", strategy = FieldStrategy.IGNORED)
    private Long serverId;


    /**
     * 备份记录保留时间 单位为天
     */
    @TableField(value = "record_retention_time")
    private Integer recordRetentionTime;

    /**
     * 备份版本保留几个
     */
    @TableField(value = "version_retention_number")
    private Integer versionRetentionNumber;

    /**
     * 连接或备份失败的次数
     */
    @TableField(value = "connection_fail_times")
    private String connectionFailTimes;

    /**
     * redis的键过期时间（天）
     */
    @TableField(value = "expiration_time")
    private String expirationTime;

    /**
     * 服务器所允许的最小空间阈值
     */
    @TableField(value = "minimum_space")
    private String minimumSpace;

    @TableField(value = "ipv4")
    private String ipv4;

    @TableField(value = "port")
    private String port;

    @TableField(value = "user")
    private String user;

    @TableField(value = "password")
    private String password;

    @TableField(value = "test_status")
    private String testStatus;

    @TableField(value = "oracle_user")
    private String oracleUser;

    @TableField(value = "oracle_backup_path")
    private String oracleBackupPath;

    @TableField(value = "mysql_backup_path")
    private String mysqlBackupPath;

    @TableField(value = "name")
    private String name;

    @TableField(value = "oracle_password")
    private String oraclePassword;



}

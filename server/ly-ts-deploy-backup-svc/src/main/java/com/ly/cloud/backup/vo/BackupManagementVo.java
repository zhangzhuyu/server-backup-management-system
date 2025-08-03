package com.ly.cloud.backup.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class BackupManagementVo implements Serializable {

    private static final long serialVersionUID = 5758686951843545527L;

    @ApiModelProperty("主键")
    private Long id;

    /**
     * 备份方式（枚举）（1为数据库备份、2为服务器工作目录备份、3为核心表备份）
     */
    @ApiModelProperty("1为数据库备份、2为服务器工作目录备份、3为核心表备份")
    private String backupWay;

    /**
     * 数据源类型（1为Oracle、2为mysql、6为mongo）
     */
    @ApiModelProperty("1为Oracle、2为mysql、6为mongo 、SQLServer")
    private String dataSourceType;

    /**
     * 全部方式（1为mysqldump，2为exp，3为expdb，4为mongodump，5为http，6为ssh，7为ftp，8为cifs，9为cdc）
     */
    @ApiModelProperty("备份方式(1为mysqldump，2为exp，3为expdb，4为mongodump，5为http，6为ssh，7为ftp，8为cifs，9为cdc)")
    private String totalMethod;

    /**
     * 客户端服务器id
     */
    @ApiModelProperty(value = "客户端服务器id")
    private Long serverId;

    @ApiModelProperty(value = "客户端服务器url")
    private String serverUrl;


    /**
     * 备份记录保留时间 单位为天
     */
    @ApiModelProperty("备份记录保留时间 单位为天")
    private Integer recordRetentionTime;

    /**
     * 备份版本保留几个
     */
    @ApiModelProperty("备份版本保留几个")
    private Integer versionRetentionNumber;

    /**
     * 连接或备份失败的次数
     */
    @ApiModelProperty("连接或备份失败的次数")
    private String connectionFailTimes;

    /**
     * redis的键过期时间
     */
    @ApiModelProperty("redis的键过期时间")
    private String expirationTime;

    /**
     * 所属部门编号
     */
    private String authDeptId;

    /**
     * 所属部门编号
     */
    private List<String> authDeptIds;


    private String ipv4;

    private String port;

    private String user;

    private String password;

    private String testStatus;

    private String oracleUser;

    private String oracleBackupPath;

    private String mysqlBackupPath;

    private String name;

}

package com.ly.cloud.backup.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BackupManagementKeyDto implements Serializable {

    private static final long serialVersionUID = 9167572012631642592L;
    /**
     * 备份方式（枚举）（1为数据库备份、2为服务器工作目录备份、3为核心表备份）
     */
    private String backupWay;

    /**
     * 数据源类型（1为Oracle、2为mysql、6为mongo）
     */
    private String dataSourceType;

    /**
     * 全部方式（1为mysqldump，2为exp，3为expdb，4为mongodump，5为http，6为ssh，7为ftp，8为cifs，9为cdc）
     */
    private String totalMethod;
}

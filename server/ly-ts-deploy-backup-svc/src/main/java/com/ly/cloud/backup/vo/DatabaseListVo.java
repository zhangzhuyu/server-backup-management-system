package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库管理列表视图
 *
 * @author chenguoqing
 */
@Data
public class DatabaseListVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 2457766527486245526L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 数据源名称
     */
    private String dataSourceName;

    /**
     * 数据源驱动
     */
    private String dataSourceDrive;

    /**
     * 数据源类型
     */
    private String sourceType;

    /**
     * 数据库名称
     */
    private String dateBaseName;

    /**
     * 用户名
     */
    private String dataBaseUser;

    /**
     * ip
     */
    private String ip;

    /**
     * 端口
     */
    private String port;

    /**
     * url
     */
    private String url;

    /**
     * 测试状态
     */
    private String testStatus;

    /**
     * 编辑时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 是否需要核验
     */
    private String whetherCheck;

    /**
     * 是否加入运维大屏展示
     */
    private String whetherMonitoring;

    /**
     * 所属公司
     */
    private Long affiliatedCompany;

    /**
     * 序号
     */
    private int serialNumber;

    /**
     * 健康状态（枚举）
     */
    private String healthStatus;

    /**
     * 密码过期时间
     */
    private String passwordExpireTime;

    /**
     * 关联服务器（可多选）
     */
    private String[] serverId;

}

package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * <p>
 * 中间件信息表
 * </p>
 *
 * @author chenguoqing
 * @since 2022-08-24
 */
@Data
@TableName("ly_rm_middleware")
public class MiddlewarePo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 8826884259313666507L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id")
    private Long id;

    /**
     * 中间件名称
     */
    @TableField("name")
    private String name;

    /**
     * 中间件类型（ 1:redis , 2:mongodb , 3:rabbit , 4:elasticsearch）
     */
    @TableField("middleware_type")
    private String middlewareType;

    /**
     * 端口
     */
    @TableField("port")
    private String port;

    /**
     * IP地址
     */
    @TableField("ip")
    private String ip;

    /**
     * 库名称
     */
    @TableField("database_name")
    private String databaseName;

    /**
     * 账号
     */
    @TableField("user")
    private String user;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * URL
     */
    @TableField("url")
    private String url;

    /**
     * 所属公司
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("affiliated_company")
    private Long affiliatedCompany;

    /**
     * 是否加入运维大屏展示
     */
    @TableField("whether_monitoring")
    private String whetherMonitoring;

    /**
     * 测试状态
     */
    @TableField("test_status")
    private String testStatus;

    /**
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 工作目录
     */
    @TableField("work_directory")
    private String workDirectory;

    /**
     * 操作时间
     */
    @TableField("operation_time")
    private Date operationTime;

    /**
     * 操作人ID
     */
    @TableField("operator_id")
    private String operatorId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 健康状态
     */
    @TableField("health_status")
    private String healthStatus;

    /**
     * 序号
     */
    @TableField("serial_number")
    private Integer serialNumber;

}

package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 中间件信息表
 * </p>
 *
 * @author chenguoqing
 * @since 2022-08-24
 */
@Data
public class MiddlewareVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 8919161982052228187L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 中间件名称
     */
    private String name;

    /**
     * 中间件类型（ 1:redis , 2:mongodb , 3:rabbit , 4:elasticsearch）
     */
    private String middlewareType;

    /**
     * 端口
     */
    private String port;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 库名称
     */
    private String databaseName;

    /**
     * 账号
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * URL
     */
    private String url;

    /**
     * 所属公司
     */
    @ApiModelProperty(value = "所属公司")
    private Long affiliatedCompany;

    /**
     * 是否加入运维大屏展示
     */
    private String whetherMonitoring;

    /**
     * 测试状态
     */
    private String testStatus;

    /**
     * 版本号
     */
    private String version;

    /**
     * 工作目录
     */
    private String workDirectory;

    /**
     * 操作时间
     */
    private Date operationTime;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 健康状态
     */
    private String healthStatus;

    /**
     * 序号
     */
    private Integer serialNumber;


}

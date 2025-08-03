package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ly.cloud.backup.common.annotation.Sm4Field;
import io.swagger.annotations.ApiModelProperty;
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
public class MiddlewareDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3376992261520296177L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 中间件名称
     */
    @ApiModelProperty(value = "中间件名称")
    private String name;

    /**
     * 中间件类型（ 1:redis , 2:mongodb , 3:rabbit , 4:elasticsearch）
     */
    @ApiModelProperty(value = "中间件类型（ 1:redis , 2:mongodb , 3:rabbit , 4:elasticsearch）")
    private String middlewareType;

    /**
     * 端口
     */
    @ApiModelProperty(value = "端口")
    private String port;

    /**
     * IP地址
     */
    @ApiModelProperty(value = "IP地址")
    private String ip;

    /**
     * 库名称
     */
    @ApiModelProperty(value = "库名称")
    private String databaseName;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String user;

    /**
     * 密码
     */
    @Sm4Field
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * URL
     */
    @ApiModelProperty(value = "URL")
    private String url;

    /**
     * 所属公司
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "所属公司")
    private Long affiliatedCompany;

    /**
     * 是否加入运维大屏展示
     */
    @ApiModelProperty(value = "是否加入运维大屏展示")
    private String whetherMonitoring;

    /**
     * 测试状态
     */
    @ApiModelProperty(value = "测试状态")
    private String testStatus;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
     * 工作目录
     */
    @ApiModelProperty(value = "工作目录")
    private String workDirectory;

    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间")
    private Date operationTime;

    /**
     * 操作人ID
     */
    @ApiModelProperty(value = "操作人ID")
    private String operatorId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 健康状态
     */
    @ApiModelProperty(value = "健康状态")
    private String healthStatus;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private Integer serialNumber;

}

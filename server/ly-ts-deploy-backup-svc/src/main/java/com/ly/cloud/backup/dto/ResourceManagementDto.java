package com.ly.cloud.backup.dto;

import com.ly.cloud.backup.common.annotation.Sm4Field;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @author SYC
 * @Date: 2022/3/29 14:54
 * @Description 资源管理数据传输对象
 */
@Data
public class ResourceManagementDto implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 新增类型（服务器:1、数据库:2、服务:3、nginx:4、防火墙:5）
     */
    @ApiModelProperty("新增类型")
    private String createType;

    /*server start*/
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 业务领域（枚举）
     */
    @ApiModelProperty("业务领域（枚举）")
    private String businessDomain;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 业务角色（枚举）
     */
    @ApiModelProperty("业务角色（枚举）")
    private String businessWorker;

    /**
     * IP地址(ipv4)
     */
    @ApiModelProperty("IP地址(ipv4)")
    private String ipv4;

    /**
     * IP地址(ipv6)
     */
    @ApiModelProperty("IP地址(ipv6)")
    private String ipv6;

    /**
     * 服务器类型（枚举）
     */
    @ApiModelProperty("服务器类型（枚举）")
    private String serverType;

    /**
     * 端口
     */
    @ApiModelProperty("端口")
    private String port;

    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String user;

    /**
     * 密码
     */
    @Sm4Field
    @ApiModelProperty("密码")
    private String password;

    /**
     * 是否加入运维大屏展示（枚举）
     */
    @ApiModelProperty("是否加入运维大屏展示（枚举）")
    private String whetherMonitoring;

    /**
     * 所属公司
     */
    @ApiModelProperty("所属公司")
    private Long affiliatedCompany;

    /**
     * 健康状态（枚举）
     */
    @ApiModelProperty("健康状态（枚举）")
    private String healthStatus;

    /**
     * 健康检测地址
     */
    @ApiModelProperty("健康检测地址")
    private String healthMonitoringUrl;

    /**
     * 是否开通外网
     */
    @ApiModelProperty("是否开通外网")
    private String whetherOuterNet;

    /**
     * 系统类型
     */
    @ApiModelProperty("系统类型")
    private String systemType;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
    /*server end*/

    /*database start*/
    /**
     * 数据源类型（枚举）
     */
    @NotBlank
    @Size(max = 10)
    @ApiModelProperty("数据源类型（枚举）")
    private String sourceType;


    /**
     * 适配器ID（外键）
     */
    @ApiModelProperty("适配器ID（外键）")
    private String adapterId;

    /**
     * 数据库名称（实例）
     */
    @NotBlank
    @Size(max = 200)
    @ApiModelProperty("数据库名称（实例）")
    private String databaseName;

    /**
     * 数据源驱动
     */
    @NotBlank
    @Size(max = 200)
    @ApiModelProperty("数据源驱动")
    private String driver;

    /**
     * URL地址
     */
    @NotBlank
    @Size(max = 200)
    @ApiModelProperty("URL地址")
        private String url;

    /**
     * 是否需要核验（枚举）
     */
    @ApiModelProperty("是否需要核验（枚举）")
    private String whetherCheck;

    /**
     * 核验规则ID
     */
    @ApiModelProperty("核验规则ID")
    private String ruleId;

    /**
     * 关联服务器（可多选）
     */
    @ApiModelProperty("关联服务器（可多选）")
    private String serverId;

    /**
     * 测试状态（枚举）
     */
    @ApiModelProperty("测试状态（枚举）")
    private String testStatus;

    /*database end*/

    /*service start*/
    /**
     * 服务中文名称
     */
    @ApiModelProperty("服务中文名称")
    private String chineseName;

    /**
     * 应用ID（外键）
     */
    @ApiModelProperty("应用ID（外键）")
    private String applicationId;

    /**
     * 关联数据库（可多选）
     */
    @ApiModelProperty("关联数据库（可多选）")
    private String databaseId;

    /**
     * 部署方式（中间件）（枚举）
     */
    @ApiModelProperty("部署方式（中间件）（枚举）")
    private String deploymentWay;
    /*service end*/

    /*firewalld start*/
    /**
     * 应用类型(枚举)
     */
    @ApiModelProperty("应用类型(枚举)")
    private String applicationType;

    /**
     * 限制来源
     */
    @ApiModelProperty("限制来源")
    private String limitedSource;

    /**
     * 源IP地址
     */
    @ApiModelProperty("源IP地址")
    private String sourceIp;

    /**
     * 协议(枚举)
     */
    @ApiModelProperty("协议(枚举)")
    private String agreement;

    /**
     * 策略(枚举)
     */
    @ApiModelProperty("策略(枚举)")
    private String strategy;
    /*firewalld end*/



}

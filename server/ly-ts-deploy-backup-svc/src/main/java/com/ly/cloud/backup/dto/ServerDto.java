package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ly.cloud.backup.common.annotation.Sm4Field;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务器信息表
 * @author chenguoqing
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("id")
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
     * hostName
     */
    @ApiModelProperty("hostname")
    private String hostName;

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
    @ApiModelProperty("密码")
    @Sm4Field
    private String password;

    /**
     * 是否加入运维大屏展示（枚举）
     */
    @ApiModelProperty("是否加入运维大屏展示（枚举）")
    private String whetherMonitoring;

    /**
     * 是否开放外网：1：是,0：否
     */
    @ApiModelProperty("是否开放外网：1：是,0：否")
    private String whetherOuterNet;

    /**
     * 所属公司Id
     */
    @ApiModelProperty("所属公司Id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long affiliatedCompany;

    /**
     * 健康状态（枚举）
     */
    @ApiModelProperty("健康状态（枚举）")
    private String healthStatus;

    /**
     * 防火墙状态
     */
    @ApiModelProperty("防火墙状态")
    private String firewallState;

    /**
     * 系统类型
     */
    @ApiModelProperty("系统类型")
    private String systemType;

    /**
     * 测试状态
     */
    @ApiModelProperty("测试状态")
    private String testStatus;

    /**
     * 配置
     */
    @ApiModelProperty("配置")
    private String configuration;

    /**
     * Agent状态(1:未安装，2:安装中,3:安装失败,4:运行中,5:已停止)
     */
    @ApiModelProperty("Agent状态(1:未安装，2:安装中,3:安装失败,4:运行中,5:已停止)")
    private String agentStatus;

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

    /**
     * 序号
     */
    @ApiModelProperty("序号")
    private Integer serialNumber;

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

    // 自定义内容
    /**
     * 容器Id
     */
    private String containerId;

    /**
     * 健康状态筛选
     */
    @ApiModelProperty("健康状态筛选")
    private List<String> healthStatusList;

    /**
     * 测试状态筛选
     */
    @ApiModelProperty("测试状态筛选")
    private List<String> testStatusList;

    /**
     * oracle用户
     */
    @ApiModelProperty("oracle用户")
    private String oracleUser;

    /**
     * oracle密码
     */
    @ApiModelProperty("oracle密码")
    private String oraclePassword;

    /**
     * oracle备份路径
     */
    @ApiModelProperty("oracle备份路径")
    private String oracleBackupPath;

    /**
     * mongodb备份路径
     */
    @ApiModelProperty("mongodb备份路径")
    private String mongodbBackupPath;

    /**
     * mysql备份路径
     */
    @ApiModelProperty("mysql备份路径")
    private String mysqlBackupPath;

    /**
     * tidb备份路径
     */
    @ApiModelProperty("tidb备份路径")
    private String tidbBackupPath;

}

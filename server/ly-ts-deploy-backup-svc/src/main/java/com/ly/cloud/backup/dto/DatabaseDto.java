package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ly.cloud.backup.common.constant.RegexpConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 数据库信息表
 *
 * @author chenguoqing
 */
@Data
public class DatabaseDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 数据源名称
     */
    @NotBlank
    @Size(max = 500)
    @ApiModelProperty("数据源名称")
    private String name;

    /**
     * 数据源类型（枚举）
     */
    @NotBlank
    @Size(max = 10)
    @ApiModelProperty("数据源类型（枚举）")
    private String sourceType;

    /**
     * 端口
     */
    @NotBlank
    @Min(0)
    @Max(65535)
    @ApiModelProperty("端口")
    private String port;

    /**
     * IP地址(ipv4)
     */
    @NotBlank
    @Pattern(regexp = RegexpConstant.IPV4_EREGEXP)
    @ApiModelProperty("IP地址(ipv4)")
    private String ipv4;

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
     * 账号
     */
    @NotBlank
    @Size(max = 200)
    @ApiModelProperty("账号")
    private String user;

    /**
     * 密码
     */
    @NotBlank
    @Size(max = 200)
    @ApiModelProperty("密码")
    private String password;

    /**
     * 密码过期时间
     */
    @ApiModelProperty("密码过期时间")
    private String passwordExpireTime;

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
     * 是否加入运维大屏展示（枚举）
     */
    @NotBlank
    @ApiModelProperty("是否加入运维大屏展示（枚举）")
    private String whetherMonitoring;

    /**
     * 关联服务器（可多选）
     */
    @ApiModelProperty("关联服务器（可多选）")
    private List<String> serverId;

    /**
     * 测试状态（枚举）
     */
    @ApiModelProperty("测试状态（枚举）")
    private String testStatus;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private java.util.Date operationTime;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

    /**
     * 所属公司
     */
    @ApiModelProperty("所属公司")
    private Long affiliatedCompany;

    /**
     * 序号
     */
    @ApiModelProperty("序号")
    private int serialNumber;

    /**
     * 健康状态（枚举）
     */
    @ApiModelProperty("健康状态（枚举）")
    private String healthStatus;

}

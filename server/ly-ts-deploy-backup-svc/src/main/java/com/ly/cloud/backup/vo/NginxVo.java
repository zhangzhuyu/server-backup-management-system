package com.ly.cloud.backup.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ly.cloud.backup.common.annotation.Sm4Field;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * nginx信息表
 * @author chenguoqing
 *
 */
@Data
public class NginxVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
     /**
     * 应用名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

     /**
     * 访问地址
     */
    private String url;
    
     /**
     * 关联服务器（可多选）
     */
    private String serverId;
    
     /**
     * 部署方式（中间件）（枚举）
     */
    private String deploymentWay;
    
     /**
     * 部署路径
     */
    private String deploymentPath;

    /**
     * 健康状态
     */
    private String healthStatus;

     /**
     * 操作时间
     */
    private java.util.Date operationTime;
    
     /**
     * 操作人ID
     */
    private String operatorId;
    
     /**
     * 备注
     */
    private String remark;

    /**
     * 序号
     */
    private String serialNumber;

    // 自增属性
    /**
     * IP地址(ipv4)
     */
    private String ipv4;

    /**
     * 端口
     */
    private String port;

    /**
     * 账号
     */
    private String user;

    /**
     * 密码
     */
    @Sm4Field
    private String password;

    /**
     * hostname
     */
    private String hostname;

    /**
     * 应用访问地址
     */
    private String appAccessAddress;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}

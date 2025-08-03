package com.ly.cloud.backup.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 防火墙信息表
 * @author chenguoqing
 *
 */
@Data
public class FirewallVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
    private Long id;

    private String name;
    
     /**
     * 应用类型(枚举)
     */
    private String applicationType;
    
     /**
     * 限制来源
     */
    private String limitedSource;
    
     /**
     * 源IP地址
     */
    private String sourceIp;
    
     /**
     * 协议(枚举)
     */
    private String agreement;
    
     /**
     * 端口
     */
    private String port;
    
     /**
     * 策略(枚举)
     */
    private String strategy;
    
     /**
     * 操作人ID
     */
    private String operatorId;
    
     /**
     * 操作时间
     */
    private java.util.Date operationTime;
    
     /**
     * 备注
     */
    private String remark;

}

package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 流量监控列表
 * @author jiangzhongxin
 */
@Data
public class NetrafficDetailVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 2939098798574665664L;

    /**
     * 最近异常请求时间
     */
    private String requestTime;

    /**
     * 请求状态码
     */
    private String code;

    /**
     * 最近成功请求数
     */
    private Integer success;

    /**
     * 最近失败请求数
     */
    private Integer fail;

}
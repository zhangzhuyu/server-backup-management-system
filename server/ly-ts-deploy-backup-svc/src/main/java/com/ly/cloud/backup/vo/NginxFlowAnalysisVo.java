package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2022-03-14
 * @version: 1.0
 */
@Data
public class NginxFlowAnalysisVo implements Serializable {

    private static final long serialVersionUID = 4542574238193682070L;

    /**
     * 请求总数
     */
    private Integer requestCount;

    /**
     * 错误请求数
     */
    private Integer errorRequestCount;

    /**
     * 访问数
     */
    private Integer visits;

    private String limit_conn_one;

    private String limit_conn_perserver;

    private String limit_conn_addr;

    /**
     * 用户数
     */
    private Integer userCount;

    /**
     * IP数
     */
    private Integer ipCount;

    /**
     * nginxIp
     */
    private String nginxIp;



}

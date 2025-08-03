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
public class NginxProfileVo implements Serializable {

    private static final long serialVersionUID = 4542574238193682070L;

    // 端口
    private String port;

    private String limit_conn_status;

    private String limit_conn_log_level;

    private String limit_conn_one;

    private String limit_conn_perserver;

    private String limit_conn_addr;

}

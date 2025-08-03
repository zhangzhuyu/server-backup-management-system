package com.ly.cloud.license.client.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ly.cloud.backup.common.annotation.Sm4Field;
import lombok.Data;

import java.io.Serializable;

/**
 * license服务器信息表
 * @author SYC
 */
@Data
@TableName("ly_sm_system_license_server")
public class ClientLicenseServerPo implements Serializable{

    private static final long serialVersionUID = 8600137500316662317L;

    /**
     * id
     */
    @TableId("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String id;

    /**
     * 可被允许的IP地址
     */
    @TableField(value = "ip_address")
    private String ipAddress;

    /**
     * 可被允许的MAC地址
     */
    @TableField(value = "mac_address")
    private String macAddress;

    /**
     * 可被允许的CPU序列号
     */
    @TableField(value = "cpu_serial")
    private String cpuSerial;

    /**
     * 可被允许的主板序列号
     */
    @TableField(value = "main_board_serial")
    private String mainBoardSerial;

    /**
     * 描述
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * IP地址(ipv4)
     */
    @TableField(value = "ipv4")
    private String ipv4;

    /**
     * 端口
     */
    @TableField(value = "port")
    private String port;

    /**
     * 账号
     */
    @TableField(value = "user")
    private String user;

    /**
     * 密码
     */
    @Sm4Field
    @TableField(value = "password")
    private String password;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;

    /**
     * docker网桥IP
     */
    @TableField(exist = false)
    private String dockerGwbridgeIp;

    /**
     * 链接状态
     */
    @TableField(value = "test_status")
    private boolean testStatus;

}

package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志信息表：ly_sm_login_log
 *
 * @author chenguoqing
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ly_sm_login_log")
public class LoginLogPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long  id;

    /**
     * sessionID
     */
    @TableField(value = "token")
    private String token;

    /**
     * 用户账号
     */
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 登录时间
     */
    @TableField(value = "login_time")
    private java.util.Date loginTime;

    /**
     * 访问IP
     */
    @TableField(value = "visit_ip")
    private String visitIp;

    /**
     * 浏览器类型
     */
    @TableField(value = "browser_type")
    private String browserType;


}


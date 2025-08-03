package com.ly.cloud.backup.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
/**
 * 登录日志信息表
 * @author chenguoqing
 *
 */
@Data
public class LoginLogVo implements Serializable {

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
     * sessionID
     */
    private String token;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 访问IP
     */
    private String visitIp;

    /**
     * 浏览器类型
     */
    private String browserType;

    /**
     * 用户操作次数
     */
    private Integer operationNumber;

}

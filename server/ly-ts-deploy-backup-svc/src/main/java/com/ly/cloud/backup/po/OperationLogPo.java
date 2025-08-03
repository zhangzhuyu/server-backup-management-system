package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 操作日志信息表：ly_sm_operation_log
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_sm_operation_log")
public class OperationLogPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 功能模块
     */
    @TableField(value = "function_moudule")
    private String functionMoudule;

    /**
     * 子功能模块
     */
    @TableField(value = "sub_functionmdl")
    private String subFunctionmdl;

    /**
     * 访问URL
     */
    @TableField(value = "visit_url")
    private String visitUrl;

    /**
     * 请求方法
     */
    @TableField(value = "request_method")
    private String requestMethod;

    /**
     * 请求方法的参数
     */
    @TableField(value = "method_parameter")
    private String methodParameter;

    /**
     * 操作详情
     */
    @TableField(value = "operation_detail")
    private String operationDetail;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;

    /**
     * 登录日志ID
     */
    @TableField(value = "login_logid")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long loginLogid;



}


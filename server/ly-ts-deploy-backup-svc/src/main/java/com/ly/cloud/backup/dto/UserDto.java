package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author chenguoqing
 */
@Data
public class UserDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -2333609292510835997L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long userId;

    /**
     * 用户账号
     */
    @ApiModelProperty("用户账号")
    private String userName;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    private String nickName;


    /**
     * 部门
     */
    @ApiModelProperty("部门")
    private String department;

    /**
     * 手机
     */
    @ApiModelProperty("手机")
    private String phonenumber;


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


}

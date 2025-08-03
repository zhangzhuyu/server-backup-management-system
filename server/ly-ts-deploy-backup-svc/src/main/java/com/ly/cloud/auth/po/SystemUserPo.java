package com.ly.cloud.auth.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Data
@TableName("ly_sys_user")
public class SystemUserPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 4117066902062719638L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "dept_id")
    private Long deptId;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    @TableField(value = "user_name")
    private String userName;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @TableField(value = "nick_name")
    private String nickName;

    /**
     * 用户类型（00系统用户）
     */
    @ApiModelProperty(value = "用户类型（00系统用户）")
    @TableField(value = "user_type")
    private String userType;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @TableField(value = "password")
    private String password;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @TableField(value = "phonenumber")
    private String phonenumber;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @TableField(value = "email")
    private String email;

    /**
     * 用户性别（0男 1女 2未知）
     */
    @ApiModelProperty(value = "用户性别（0男 1女 2未知）")
    @TableField(value = "sex")
    private String sex;

    /**
     * 头像地址
     */
    @ApiModelProperty(value = "头像地址")
    @TableField(value = "sex")
    private String avatar;

//    /**
//     * 附件id, 用来存放用户头像附件id
//     */
//    @ApiModelProperty(value = "附件id, 用来存放用户头像附件id")
//    @TableField(value = "file_id")
//    private String fileId;
//
//    /**
//     * 头像附件名称
//     */
//    @ApiModelProperty(value = "头像附件名称")
//    @TableField(value = "file_name")
//    private String fileName;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    @TableField(value = "status")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @TableField(value = "del_flag")
    private String delFlag;

    /**
     * 密码最后修改时间
     */
    @ApiModelProperty(value = "密码最后修改时间")
    @TableField(value = "password_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date passwordTime;

    /**
     * 钉钉号
     */
    @ApiModelProperty(value = "钉钉号")
    @TableField(value = "ding_talk")
    private String dingTalk;

    /**
     * 最后登录IP
     */
    @ApiModelProperty(value = "最后登录IP")
    @TableField(value = "login_ip")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @ApiModelProperty(value = "最后登录时间")
    @TableField(value = "login_date")
    private Date loginDate;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    @TableField(value = "user_sort")
    private Integer userSort;
}

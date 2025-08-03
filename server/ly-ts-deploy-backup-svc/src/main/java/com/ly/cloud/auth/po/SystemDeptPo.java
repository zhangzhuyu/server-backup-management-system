package com.ly.cloud.auth.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Data
@TableName("ly_sys_dept")
public class SystemDeptPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3446319818349418360L;

    /**
     * 主键 部门id
     */
    @ApiModelProperty(value = "部门id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "dept_id")
    private Long deptId;

    /**
     * 父部门id
     */
    @ApiModelProperty(value = "父部门id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 祖级列表
     */
    @ApiModelProperty(value = "祖级列表")
    @TableField(value = "ancestors")
    private String ancestors;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    @TableField(value = "dept_name")
    private String deptName;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    @TableField(value = "order_num")
    private Integer orderNum;

    /**
     * 负责人
     */
    @ApiModelProperty(value = "负责人")
    @TableField(value = "leader")
    private String leader;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    @TableField(value = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @TableField(value = "email")
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    @ApiModelProperty(value = "部门状态（0正常 1停用）")
    @TableField(value = "status")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @TableField(value = "del_flag")
    private String delFlag;

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

}

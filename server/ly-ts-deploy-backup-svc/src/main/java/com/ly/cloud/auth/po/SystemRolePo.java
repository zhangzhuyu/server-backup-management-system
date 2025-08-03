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
 * 角色信息表
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Data
@TableName("ly_sys_role")
public class SystemRolePo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -65952928863717175L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "角色ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "role_id")
    private Long roleId;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    @TableField(value = "role_name")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @ApiModelProperty(value = "角色权限字符串")
    @TableField(value = "role_key")
    private String roleKey;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    @TableField(value = "role_sort")
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    @ApiModelProperty(value = "数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）")
    @TableField(value = "data_scope")
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    @ApiModelProperty(value = "菜单树选择项是否关联显示")
    @TableField(value = "menu_check_strictly")
    private Boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    @ApiModelProperty(value = "部门树选择项是否关联显示")
    @TableField(value = "dept_check_strictly")
    private Boolean deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    @ApiModelProperty(value = "角色状态（0正常 1停用）")
    @TableField(value = "status")
    private String status;

    /**
     * 角色状态（0正常 1停用）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
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

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;


}

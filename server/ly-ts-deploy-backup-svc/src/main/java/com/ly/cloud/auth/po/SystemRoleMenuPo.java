package com.ly.cloud.auth.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色和菜单关联表
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Data
@TableName("ly_sys_role_menu")
public class SystemRoleMenuPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -2249214993720385231L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "用户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id")
    private Long id;

    /**
     * 角色ID
     */
    @ApiModelProperty(value = "角色ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 菜单ID
     */
    @ApiModelProperty(value = "菜单ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "menu_id")
    private Long menuId;


}

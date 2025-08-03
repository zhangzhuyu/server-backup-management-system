package com.ly.cloud.auth.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * 菜单权限表
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Data
@TableName("ly_sys_menu")
public class SystemMenuPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 7779236995604765972L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "菜单ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "menu_id")
    private Long menuId;

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
    @TableField(value = "menu_name")
    private String menuName;

    /**
     * 父菜单ID
     */
    @ApiModelProperty(value = "父菜单ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    @TableField(value = "order_num")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @ApiModelProperty(value = "路由地址")
    @TableField(value = "path")
    private String path;

    /**
     * 组件路径
     */
    @ApiModelProperty(value = "组件路径")
    @TableField(value = "component")
    private String component;

    /**
     * 路由参数
     */
    @ApiModelProperty(value = "路由参数")
    @TableField(value = "query")
    private String query;

    /**
     * 是否为外链（0是 1否）
     */
    @ApiModelProperty(value = "是否为外链（0是 1否）")
    @TableField(value = "is_frame")
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @ApiModelProperty(value = "是否缓存（0缓存 1不缓存）")
    @TableField(value = "is_cache")
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @ApiModelProperty(value = "菜单类型（M目录 C菜单 F按钮）")
    @TableField(value = "menu_type")
    private String menuType;

    /**
     * 显示状态（0显示 1隐藏）
     */
    @ApiModelProperty(value = "显示状态（0显示 1隐藏）")
    @TableField(value = "visible")
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    @ApiModelProperty(value = "菜单状态（0正常 1停用）")
    @TableField(value = "status")
    private String status;

    /**
     * 权限标识,一个用户如果有这个标识代表他可以访问该菜单
     */
    @ApiModelProperty(value = "权限标识,一个用户如果有这个标识代表他可以访问该菜单")
    @TableField(value = "perms")
    private String perms;

    /**
     * 菜单图标
     */
    @ApiModelProperty(value = "菜单图标")
    @TableField(value = "icon")
    private String icon;

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

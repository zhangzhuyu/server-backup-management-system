package com.ly.cloud.auth.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ly.cloud.auth.common.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表 sys_menu
 * 
 *
 */
@TableName("ly_sys_menu")
public class SysMenu extends BaseEntity
{
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    @TableId
    private Long menu_id;

    /** 菜单名称 */
    private String menu_name;


    /** 父菜单ID */
    private Long parent_id;

    /** 显示顺序 */
    private Integer order_num;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;
    
    /** 显示状态（0显示 1隐藏） */
    private String status;

    /** 权限字符串 */
    private String perms;


    /** 子菜单 */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<SysMenu>();

    public Long getMenu_id()
    {
        return menu_id;
    }

    public void setMenu_id(Long menu_id)
    {
        this.menu_id = menu_id;
    }


    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    public String getMenu_name()
    {
        return menu_name;
    }

    public void setMenu_name(String menu_name)
    {
        this.menu_name = menu_name;
    }


    public Long getParent_id()
    {
        return parent_id;
    }

    public void setParent_id(Long parent_id)
    {
        this.parent_id = parent_id;
    }

    @NotNull(message = "显示顺序不能为空")
    public Integer getOrder_num()
    {
        return order_num;
    }

    public void setOrder_num(Integer order_num)
    {
        this.order_num = order_num;
    }

    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符")
    public String getComponent()
    {
        return component;
    }

    public void setComponent(String component)
    {
        this.component = component;
    }


    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    public String getPerms()
    {
        return perms;
    }

    public void setPerms(String perms)
    {
        this.perms = perms;
    }

    public List<SysMenu> getChildren()
    {
        return children;
    }

    public void setChildren(List<SysMenu> children)
    {
        this.children = children;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("menu_id", getMenu_id())
            .append("menu_name", getMenu_name())
            .append("parent_id", getParent_id())
            .append("order_num", getOrder_num())
            .append("path", getPath())
            .append("component", getComponent())
            .append("status ", getStatus())
            .append("perms", getPerms())
            .append("remark", getRemark())
            .toString();
    }
}

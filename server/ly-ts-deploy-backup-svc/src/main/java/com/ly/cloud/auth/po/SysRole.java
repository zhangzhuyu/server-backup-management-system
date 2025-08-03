package com.ly.cloud.auth.po;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ly.cloud.auth.common.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


import javax.validation.constraints.Size;

/**
 * 角色表 sys_role
 * 
 *
 */
@TableName("ly_sys_role")
public class SysRole extends BaseEntity
{
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 角色ID */
    @TableId
    private Long role_id;

    /** 角色名称 */
    private String role_name;

    /** 角色权限 */
    private String role_key;

    /** 角色排序 */
    private String role_sort;

    /** 角色状态（0正常 1停用） */
    private String status;

    /** 用户是否存在此角色标识 默认不存在 */
    @TableField(exist = false)
    private boolean flag = false;

    /** 菜单组 */
    @TableField(exist = false)
    private Long[] menu_ids;

    public SysRole()
    {

    }

    public SysRole(Long role_id)
    {
        this.role_id = role_id;
    }

    public Long getRole_id()
    {
        return role_id;
    }

    public void setRole_id(Long role_id)
    {
        this.role_id = role_id;
    }

    public boolean isAdmin()
    {
        return isAdmin(this.role_id);
    }

    public static boolean isAdmin(Long role_id)
    {
        return role_id != null && 1L == role_id;
    }

    @Size(min = 0, max = 30, message = "角色名称长度不能超过30个字符")
    public String getRole_name()
    {
        return role_name;
    }

    public void setRole_name(String role_name)
    {
        this.role_name = role_name;
    }

    @Size(min = 0, max = 100, message = "权限字符长度不能超过100个字符")
    public String getRole_key()
    {
        return role_key;
    }

    public void setRole_key(String role_key)
    {
        this.role_key = role_key;
    }

    public String getRole_sort()
    {
        return role_sort;
    }

    public void setRole_sort(String role_sort)
    {
        this.role_sort = role_sort;
    }



    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }


    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    public Long[] getmenu_ids()
    {
        return menu_ids;
    }

    public void setmenu_ids(Long[] menu_ids)
    {
        this.menu_ids = menu_ids;
    }


    
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("role_id", getRole_id())
            .append("role_name", getRole_name())
            .append("role_key", getRole_key())
            .append("role_sort", getRole_sort())
            .append("status", getStatus())
            .append("remark", getRemark())
            .toString();
    }
}

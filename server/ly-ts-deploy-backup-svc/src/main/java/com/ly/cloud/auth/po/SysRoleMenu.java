package com.ly.cloud.auth.po;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 角色和菜单关联 sys_role_menu
 * 
 *
 */
@Data
public class SysRoleMenu
{
    /** 角色ID */
    private Long role_id;
    
    /** 菜单ID */
    private Long menu_id;

}

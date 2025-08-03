package com.ly.cloud.auth.service;


import com.ly.cloud.auth.po.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户权限处理
 * 
 *
 */
@Component
public class SysPermissionService {
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取角色数据权限
     * need
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            roles.add("admin");
        }
        else
        {
            roles.addAll(roleService.selectRolePermissionByuser_id(user.getUserId()));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     * need
     * @param user 用户信息
     * @param menuType 菜单类型（M目录 C菜单 F按钮）
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user,String menuType)
    {
        Set<String> perms = new HashSet<String>();
//        // 管理员拥有所有权限
//        if (user.isAdmin())
//        {
//            perms.add("*:*:*");
//        }
//        else
//        {
//            perms.addAll(menuService.selectMenuPermsByuser_id(user.getUserId(),menuType));
//        }

        perms.addAll(menuService.selectMenuPermsByuser_id(user.getUserId(),menuType));

        return perms;
    }
}

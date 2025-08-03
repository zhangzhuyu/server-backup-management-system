package com.ly.cloud.auth.mapper;


import com.ly.cloud.auth.po.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色与菜单关联表 数据层
 * 
 *
 */
@Mapper
public interface SysRoleMenuMapper
{
    /**
     * 查询菜单使用数量
     * 
     * @param menu_id 菜单ID
     * @return 结果
     */
    public int checkMenuExistRole(Long menu_id);

    /**
     * 通过角色ID删除角色和菜单关联
     * 
     * @param role_id 角色ID
     * @return 结果
     */
    public int deleteRoleMenuByrole_id(Long role_id);

    /**
     * 批量删除角色菜单关联信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteRoleMenu(Long[] ids);

    /**
     * 批量新增角色菜单信息
     * 
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList);
}

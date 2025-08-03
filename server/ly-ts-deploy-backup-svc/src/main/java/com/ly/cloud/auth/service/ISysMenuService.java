package com.ly.cloud.auth.service;

import com.ly.cloud.auth.po.SysMenu;
import com.ly.cloud.auth.vo.TreeSelect;


import java.util.List;
import java.util.Set;

/**
 * 菜单 业务层
 * 
 *
 */
public interface ISysMenuService
{
    /**
     * 根据用户查询系统菜单列表
     * 
     * @param user_id 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(Long user_id);

    /**
     * 根据用户查询系统菜单列表
     * 
     * @param menu 菜单信息
     * @param user_id 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu, Long user_id);

    /**
     * 根据用户ID查询权限
     * need
     * @param user_id 用户ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByuser_id(Long user_id,String menuType);

    /**
     * 根据用户ID查询菜单树信息
     * 
     * @param user_id 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByuser_id(Long user_id);

    /**
     * 根据角色ID查询菜单树信息
     * 
     * @param role_id 角色ID
     * @return 选中菜单列表
     */
    public List<Long> selectMenuListByrole_id(Long role_id);


    /**
     * 构建前端所需要树结构
     * 
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     * 
     * @param menu_id 菜单ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(Long menu_id);

    /**
     * 是否存在菜单子节点
     * 
     * @param menu_id 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean hasChildBymenu_id(Long menu_id);

    /**
     * 查询菜单是否存在角色
     * 
     * @param menu_id 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkMenuExistRole(Long menu_id);

    /**
     * 新增保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     * 
     * @param menu_id 菜单ID
     * @return 结果
     */
    public int deleteMenuById(Long menu_id);

    /**
     * 校验菜单名称是否唯一
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public String checkmenu_nameUnique(SysMenu menu);
}

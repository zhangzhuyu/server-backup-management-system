package com.ly.cloud.auth.service.impl;


import com.ly.cloud.auth.constant.UserConstants;
import com.ly.cloud.auth.po.SysMenu;
import com.ly.cloud.auth.po.SysUser;
import com.ly.cloud.auth.vo.TreeSelect;
import com.ly.cloud.auth.mapper.SysMenuMapper;
import com.ly.cloud.auth.mapper.SysRoleMenuMapper;
import com.ly.cloud.auth.service.ISysMenuService;
import com.ly.cloud.auth.util.SecurityUtils;
import com.ly.cloud.auth.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单 业务层处理
 *
 *
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService {
    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 根据用户查询系统菜单列表
     *
     * @param user_id 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(Long user_id) {
        return selectMenuList(new SysMenu(), user_id);
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long user_id) {
        List<SysMenu> menuList = null;
        // 管理员显示所有菜单信息
        if (SysUser.isAdmin(user_id)) {
            menuList = menuMapper.selectMenuList(menu);
        } else {
            menu.getParams().put("user_id", user_id);
            menuList = menuMapper.selectMenuListByuser_id(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param user_id 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByuser_id(Long user_id,String menuType) {
        List<String> perms = menuMapper.selectMenuPermsByuser_id(user_id,menuType);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param user_id 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByuser_id(Long user_id) {
        List<SysMenu> menus = null;
        if (SecurityUtils.isAdmin(user_id)) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByuser_id(user_id);
        }
        return getChildPerms(menus, 0);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param role_id 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByrole_id(Long role_id) {
        return menuMapper.selectMenuListByrole_id(role_id, false);
    }


    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = new ArrayList<Long>();
        for (SysMenu dept : menus) {
            tempList.add(dept.getMenu_id());
        }
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext(); ) {
            SysMenu menu = (SysMenu) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParent_id())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menu_id 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menu_id) {
        return menuMapper.selectMenuById(menu_id);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menu_id 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildBymenu_id(Long menu_id) {
        int result = menuMapper.hasChildBymenu_id(menu_id);
        return result > 0 ? true : false;
    }

    /**
     * 查询菜单使用数量
     *
     * @param menu_id 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menu_id) {
        int result = roleMenuMapper.checkMenuExistRole(menu_id);
        return result > 0 ? true : false;
    }

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu menu) {
        return menuMapper.insert(menu);
    }

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu menu) {
        return menuMapper.updateMenu(menu);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menu_id 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menu_id) {
        return menuMapper.deleteMenuById(menu_id);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public String checkmenu_nameUnique(SysMenu menu) {
        Long menu_id = StringUtils.isNull(menu.getMenu_id()) ? -1L : menu.getMenu_id();
        SysMenu info = menuMapper.checkmenu_nameUnique(menu.getMenu_name(), menu.getParent_id());
        if (StringUtils.isNotNull(info) && info.getMenu_id().longValue() != menu_id.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parent_id 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parent_id) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext(); ) {
            SysMenu t = (SysMenu) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParent_id() == parent_id) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = (SysMenu) it.next();
            if (n.getParent_id().longValue() == t.getMenu_id().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

}

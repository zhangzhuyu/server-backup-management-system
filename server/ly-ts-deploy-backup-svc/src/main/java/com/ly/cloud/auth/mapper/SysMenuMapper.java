package com.ly.cloud.auth.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.auth.po.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单表 数据层
 *
 *
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu>
{
    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 根据用户所有权限
     *
     * @return 权限列表
     */
    public List<String> selectMenuPerms();

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuListByuser_id(SysMenu menu);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<String> selectMenuPermsByuser_id(@Param(value = "userId") Long userId,@Param(value = "menuType")String menuType);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     *
     * @param user_id 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByuser_id(Long user_id);

    /**
     * 根据角色ID查询菜单树信息
     * 
     * @param role_id 角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    public List<Long> selectMenuListByrole_id(@Param("role_id") Long role_id, @Param("menuCheckStrictly") boolean menuCheckStrictly);

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
     * @return 结果
     */
    public int hasChildBymenu_id(Long menu_id);


    /**
     * 修改菜单信息
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
     * @param menu_name 菜单名称
     * @param parent_id 父菜单ID
     * @return 结果
     */
    public SysMenu checkmenu_nameUnique(@Param("menu_name") String menu_name, @Param("parent_id") Long parent_id);
}

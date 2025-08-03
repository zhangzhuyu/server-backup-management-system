package com.ly.cloud.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.po.SysRole;
import com.ly.cloud.auth.po.SysUserRole;

import java.util.List;
import java.util.Set;

/**
 * 角色业务层
 * 
 *
 */
public interface ISysRoleService
{
    /**
     * 根据条件分页查询角色数据
     * 
     *
     * @param page
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public IPage selectRolePage(Page page, SysRole role);



    /**
     * 根据条件查询所有角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色列表
     * 
     * @param user_id 用户ID
     * @return 角色列表
     */
    public List<SysRole> selectRolesByuser_id(Long user_id);

    /**
     * 根据用户ID查询角色权限
     * 
     * @param user_id 用户ID
     * @return 权限列表
     */
    public Set<String> selectRolePermissionByuser_id(Long user_id);

    /**
     * 查询所有角色
     * 
     * @return 角色列表
     */
    public List<SysRole> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     * 
     * @param user_id 用户ID
     * @return 选中角色ID列表
     */
    public List<Long> selectRoleListByuser_id(Long user_id);

    /**
     * 通过角色ID查询角色
     * 
     * @param role_id 角色ID
     * @return 角色对象信息
     */
    public SysRole selectRoleById(Long role_id);

    /**
     * 校验角色名称是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    public String checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    public String checkRoleKeyUnique(SysRole role);

    /**
     * 校验角色是否允许操作
     * 
     * @param role 角色信息
     */
    public void checkRoleAllowed(SysRole role);

    /**
     * 校验角色是否有数据权限
     * 
     * @param role_id 角色id
     */
    public void checkRoleDataScope(Long role_id);

    /**
     * 通过角色ID查询角色使用数量
     * 
     * @param role_id 角色ID
     * @return 结果
     */
    public int countUserRoleByrole_id(Long role_id);

    /**
     * 新增保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int insertRole(SysRole role);

    /**
     * 修改保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int updateRole(SysRole role);

    /**
     * 修改角色状态
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int updateRoleStatus(SysRole role);


    /**
     * 通过角色ID删除角色
     * 
     * @param role_id 角色ID
     * @return 结果
     */
    public int deleteRoleById(Long role_id);

    /**
     * 批量删除角色信息
     * 
     * @param role_ids 需要删除的角色ID
     * @return 结果
     */
    public int deleteRoleByIds(Long[] role_ids);

    /**
     * 取消授权用户角色
     * 
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    public int deleteAuthUser(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     * 
     * @param role_id 角色ID
     * @param user_ids 需要取消授权的用户数据ID
     * @return 结果
     */
    public int deleteAuthUsers(Long role_id, Long[] user_ids);

    /**
     * 批量选择授权用户角色
     * 
     * @param role_id 角色ID
     * @param user_ids 需要删除的用户数据ID
     * @return 结果
     */
    public int insertAuthUsers(Long role_id, Long[] user_ids);
}

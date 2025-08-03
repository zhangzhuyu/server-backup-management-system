package com.ly.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.po.SysRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表 数据层
 * 
 *
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole>
{
    /**
     * 根据条件分页查询角色数据
     * 
     *
     * @param page
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public IPage<SysRole> selectRolePage(@Param("page") Page<SysRole> page, @Param("role")SysRole role);

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色
     * 
     * @param user_id 用户ID
     * @return 角色列表
     */
    public List<SysRole> selectRolePermissionByuser_id(Long user_id);

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
     * 根据用户ID查询角色
     * 
     * @param userName 用户名
     * @return 角色列表
     */
    public List<SysRole> selectRolesByUserName(String userName);

    /**
     * 校验角色名称是否唯一
     * 
     * @param roleName 角色名称
     * @return 角色信息
     */
    public SysRole checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限是否唯一
     * 
     * @param roleKey 角色权限
     * @return 角色信息
     */
    public SysRole checkRoleKeyUnique(String roleKey);

    /**
     * 修改角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int updateRole(SysRole role);



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
}

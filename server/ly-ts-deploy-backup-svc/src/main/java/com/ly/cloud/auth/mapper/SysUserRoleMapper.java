package com.ly.cloud.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.auth.po.SysUserRole;
import com.ly.cloud.auth.po.SystemUserRolePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 * 
 *
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SystemUserRolePo>
{
    /**
     * 通过用户ID删除用户和角色关联
     * 
     * @param user_id 用户ID
     * @return 结果
     */
    public int deleteUserRoleByuser_id(Long user_id);

    /**
     * 批量删除用户和角色关联
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserRole(Long[] ids);

    /**
     * 通过角色ID查询角色使用数量
     * 
     * @param role_id 角色ID
     * @return 结果
     */
    public int countUserRoleByrole_id(Long role_id);

    /**
     * 批量新增用户角色信息
     * 
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    public int batchUserRole(List<SysUserRole> userRoleList);

    /**
     * 删除用户和角色关联信息
     * 
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    public int deleteUserRoleInfo(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     * 
     * @param role_id 角色ID
     * @param user_ids 需要删除的用户数据ID
     * @return 结果
     */
    public int deleteUserRoleInfos(@Param("role_id") Long role_id, @Param("user_ids") Long[] user_ids);
}

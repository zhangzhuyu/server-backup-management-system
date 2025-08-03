package com.ly.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.po.SysUser;
import com.ly.cloud.auth.po.SystemUserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表 数据层
 * 
 *
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SystemUserPo>
{
    /**
     * 根据条件分页查询用户列表
     * 
     *
     * @param page
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    public IPage<SysUser> selectUserList(@Param("page")Page page, @Param("user")SysUser sysUser);

    /**
     * 根据条件分页查询已配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public IPage<SysUser> selectAllocatedList(@Param("page")Page page,SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public IPage<SysUser> selectUnallocatedList(@Param("page")Page page,SysUser user);

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId);

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);


    /**
     * 重置用户密码
     * 
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(@Param("userName") String userName, @Param("password") String password);

    /**
     * 通过用户ID删除用户
     * 
     * @param user_id 用户ID
     * @return 结果
     */
    public int deleteUserById(Long user_id);

    /**
     * 批量删除用户信息
     * 
     * @param user_ids 需要删除的用户ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] user_ids);

    /**
     * 校验用户名称是否唯一
     * 
     * @param userName 用户名称
     * @return 结果
     */
    public int checkUserNameUnique(String userName);

}

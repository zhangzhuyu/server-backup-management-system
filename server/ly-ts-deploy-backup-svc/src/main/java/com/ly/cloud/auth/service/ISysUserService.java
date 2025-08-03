package com.ly.cloud.auth.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.po.SysUser;

/**
 * 用户 业务层
 * 
 *
 */
public interface ISysUserService
{
    /**
     * 根据条件分页查询用户列表
     * 
     *
     * @param page
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public IPage<SysUser> selectUserList(Page page, SysUser user);

    /**
     * 根据条件分页查询已分配用户角色列表
     * 
     *
     * @param page
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public IPage<SysUser> selectAllocatedList(Page page , SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public IPage<SysUser> selectUnallocatedList(Page page,SysUser user);

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
     * @param user_id 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long user_id);

    /**
     * 根据用户ID查询用户所属角色组
     * 
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserRoleGroup(String userName);



    /**
     * 校验用户名称是否唯一
     * 
     * @param userName 用户名称
     * @return 结果
     */
    public String checkUserNameUnique(String userName);



    /**
     * 校验用户是否允许操作
     * 
     * @param user 用户信息
     */
    public void checkUserAllowed(SysUser user);

    /**
     * 校验用户是否有数据权限
     * 
     * @param user_id 用户id
     */
    public void checkUserDataScope(Long user_id);

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 注册用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public boolean registerUser(SysUser user);

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 用户授权角色
     * 
     * @param user_id 用户ID
     * @param role_ids 角色组
     */
    public void insertUserAuth(Long user_id, Long[] role_ids);

    /**
     * 修改用户状态
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserStatus(SysUser user);

    /**
     * 修改用户基本信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserProfile(SysUser user);


    /**
     * 重置用户密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int resetPwd(SysUser user);

    /**
     * 重置用户密码
     * 
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(String userName, String password);

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

//    /**
//     * 导入用户数据
//     *
//     * @param userList 用户数据列表
//     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
//     * @param operName 操作用户
//     * @return 结果
//     */
//    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);
}

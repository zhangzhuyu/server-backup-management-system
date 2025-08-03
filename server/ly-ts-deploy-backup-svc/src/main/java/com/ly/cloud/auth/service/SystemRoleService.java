package com.ly.cloud.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.auth.dto.SystemRoleDto;
import com.ly.cloud.auth.dto.SystemUserRoleDto;
import com.ly.cloud.auth.po.SystemRolePo;
import com.ly.cloud.auth.vo.SystemMenuVo;
import com.ly.cloud.auth.vo.SystemRoleVo;
import com.ly.cloud.auth.vo.SystemUserVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 角色信息表 服务类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemRoleService extends IService<SystemRolePo> {
    /**
     * 添加
     * @param systemRoleDto 角色信息表
     */
    void create(SystemRoleDto systemRoleDto);

    /**
     * 删除
     * @param ids 主键ID数组
     */
    void delete(Long[] ids);

    /**
     * 更新
     * @param systemRoleDto 角色信息表
     */
    void update(SystemRoleDto systemRoleDto);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 角色信息表
     */
    SystemRoleVo find(Long id);

    /**
     * 分配用户
     * @param systemUserRoleDto
     * @return
     */
    void assigningUsers(SystemUserRoleDto systemUserRoleDto);

    /**
     * 查询
     * @param page
     * @param pageSize
     * @param roleName 搜索框搜索内容(用户名)
     * @param status 状态
     * @return 角色信息表分页数据
     */
    IPage<SystemRoleVo> query(Integer page, Integer pageSize, String roleName, String status);

    /**
     * 批量导出
     *
     * @param roleIds
     * @param response
     */
    void export(List<Long> roleIds, HttpServletResponse response);

    /**
     * 根据主键查询角色用户
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    IPage<SystemUserVo> queryUsersByRoleId(Integer page, Integer pageSize, String id);

    /**
     * 根据主键查询角色权限
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    List<SystemMenuVo> queryMenusByRoleId(String id);
}

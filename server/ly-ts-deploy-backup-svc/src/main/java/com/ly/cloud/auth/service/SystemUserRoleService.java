package com.ly.cloud.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.auth.dto.SystemRoleDto;
import com.ly.cloud.auth.dto.SystemUserRoleDto;
import com.ly.cloud.auth.po.SystemUserRolePo;
import com.ly.cloud.auth.vo.SystemUserRoleVo;

/**
 * <p>
 * 用户和角色关联表 服务类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemUserRoleService extends IService<SystemUserRolePo> {
    /**
     * 添加
     * @param systemUserRoleDto 用户和角色关联表
     */
    void create(SystemUserRoleDto systemUserRoleDto);

    /**
     * 删除
     * @param ids 主键ID数组
     */
    void delete(Long[] ids);

    /**
     * 更新
     * @param systemUserRoleDto 用户和角色关联表
     */
    void update(SystemUserRoleDto systemUserRoleDto);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 用户和角色关联表
     */
    SystemUserRoleVo find(Long id);

    /**
     * 查询
     * @param pageNum,pageSize,systemUserRoleDto 分页查询参数
     * @return 用户和角色关联表分页数据
     */
    IPage<SystemUserRoleVo> query(int pageNum, int pageSize, SystemUserRoleDto systemUserRoleDto);
}

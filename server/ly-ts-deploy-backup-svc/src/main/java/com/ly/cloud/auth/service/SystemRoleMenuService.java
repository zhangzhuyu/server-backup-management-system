package com.ly.cloud.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.auth.dto.SystemRoleMenuDto;
import com.ly.cloud.auth.po.SystemRoleMenuPo;
import com.ly.cloud.auth.vo.SystemRoleMenuVo;

/**
 * <p>
 * 角色和菜单关联表 服务类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemRoleMenuService extends IService<SystemRoleMenuPo> {
    /**
     * 添加
     * @param systemRoleMenuDto 角色和菜单关联表
     */
    void create(SystemRoleMenuDto systemRoleMenuDto);

    /**
     * 删除
     * @param ids 主键ID数组
     */
    void delete(Long[] ids);

    /**
     * 更新
     * @param systemRoleMenuDto 角色和菜单关联表
     */
    void update(SystemRoleMenuDto systemRoleMenuDto);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 角色和菜单关联表
     */
    SystemRoleMenuVo find(Long id);

    /**
     * 查询
     * @param pageNum,pageSize,systemRoleMenuDto 分页查询参数
     * @return 角色和菜单关联表分页数据
     */
    IPage<SystemRoleMenuVo> query(int pageNum, int pageSize, SystemRoleMenuDto systemRoleMenuDto);
}

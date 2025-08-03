package com.ly.cloud.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.auth.dto.SystemMenuDto;
import com.ly.cloud.auth.po.SystemMenuPo;
import com.ly.cloud.auth.vo.SystemMenuVo;

import java.util.List;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemMenuService extends IService<SystemMenuPo> {
    /**
     * 添加
     * @param systemMenuDto 菜单权限表
     */
    void create(SystemMenuDto systemMenuDto);

    /**
     * 删除
     * @param ids 主键ID数组
     */
    void delete(Long[] ids);

    /**
     * 更新
     * @param systemMenuDto 菜单权限表
     */
    void update(SystemMenuDto systemMenuDto);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 菜单权限表
     */
    SystemMenuVo find(Long id);

    /**
     * 查询
     * @param pageNum,pageSize,systemMenuDto 分页查询参数
     * @return 菜单权限表分页数据
     */
    IPage<SystemMenuVo> query(int pageNum, int pageSize, SystemMenuDto systemMenuDto);

    /**
     * 查询
     * @param page
     * @param pageSize
     * @param id
     * @return 菜单权限表分页数据
     */
    IPage<SystemMenuVo> querySubmenus(Integer page, Integer pageSize, String id, String status, String content);

    /**
     * 查询所有
     * @return 菜单权限表分页数据
     */
    List<SystemMenuVo> queryAll();
}

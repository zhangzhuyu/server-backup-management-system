package com.ly.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.dto.SystemRoleMenuDto;
import com.ly.cloud.auth.po.SystemRoleMenuPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.auth.vo.SystemMenuVo;
import com.ly.cloud.auth.vo.SystemRoleMenuVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色和菜单关联表 Mapper 接口
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemRoleMenuMapper extends BaseMapper<SystemRoleMenuPo> {
    /**
     * 按主键查询
     * @param id 主键ID
     * @return 角色和菜单关联表
     */
    SystemRoleMenuVo selectById(Long id);

    /**
     * 根据角色id 查询 菜单id
     */
    List<String> selectMenuIdById( @Param("id") Long id);

    /**
     * 查询
     * @param page 分页参数
     * @param systemRoleMenuDto 角色和菜单关联表
     * @return 角色和菜单关联表集合
     */
    IPage<SystemRoleMenuVo> select(@Param("page") Page<SystemRoleMenuVo> page, @Param("dto") SystemRoleMenuDto systemRoleMenuDto);

    /**
     * 根据角色Id查询菜单
     * @param page 分页参数
     * @param id
     * @return
     */
    List<SystemMenuVo> queryMenusByRoleId(@Param("id") Long id);
}

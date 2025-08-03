package com.ly.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.dto.SystemMenuDto;
import com.ly.cloud.auth.po.SystemMenuPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.auth.vo.SystemMenuVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemMenuMapper extends BaseMapper<SystemMenuPo> {
    /**
     * 按主键查询
     * @param menu_id 主键ID
     * @return 菜单权限表
     */
    SystemMenuVo selectById(Long menu_id);

    /**
     * 查询
     * @param page 分页参数
     * @param systemMenuDto 菜单权限表
     * @return 菜单权限表集合
     */
    IPage<SystemMenuVo> select(@Param("page") Page<SystemMenuVo> page, @Param("dto") SystemMenuDto systemMenuDto);

    /**
     * 查询 树形结构 
     * @param page 分页参数
     * @param systemMenuDto 菜单权限表
     * @return 菜单权限表集合
     */
    IPage<SystemMenuVo> queryTreeSystemMenuAll(@Param("page") Page<SystemMenuVo> page, @Param("dto") SystemMenuDto systemMenuDto);

    /**
     * 根据主键查询所有子菜单Id(包含当前菜单)
     * @param id 当前菜单Id
     * @return 菜单表集合
     */
    List<SystemMenuVo> querySubmenusById(@Param("id") Long id);

    /**
     * 根据主键集合查询
     * @param page
     * @param ids 菜单Ids
     * @return 菜单权限表集合
     */
    IPage<SystemMenuVo> queryByIds(@Param("page") Page<SystemMenuVo> page, @Param("list") List<Long> ids, @Param("statusList") List<String> statusList, @Param("content") String content);

    /**
     * 查询（不分页）
     * @return 菜单表集合
     */
    List<SystemMenuVo> queryAll();

    /**
     * 查询
     * @param id 角色Id
     * @return 菜单权限表集合
     */
    List<SystemMenuVo> selectByRoleId(@Param("id") Long id);
}

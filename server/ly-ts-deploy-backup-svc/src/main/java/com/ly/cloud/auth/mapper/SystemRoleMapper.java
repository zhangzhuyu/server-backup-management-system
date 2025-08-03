package com.ly.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.dto.SystemRoleDto;
import com.ly.cloud.auth.po.SystemRolePo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.auth.vo.SystemRoleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色信息表 Mapper 接口
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemRoleMapper extends BaseMapper<SystemRolePo> {
    /**
     * 按主键查询
     * @param roleId 主键ID
     * @return 角色信息表
     */
    SystemRoleVo selectById(Long roleId);

    /**
     * 查询
     * @param page 分页参数
     * @param systemRoleDto 角色信息表
     * @param statusList 角色状态集合
     * @return 角色信息表集合
     */
    IPage<SystemRoleVo> select(@Param("page") Page<SystemRoleVo> page, @Param("dto") SystemRoleDto systemRoleDto, @Param("list") List<String> statusList);

    /**
     * 根据角色名称查询角色Id
     * @param roleName 角色名称
     * @return 角色Id
     */
    Long selectRoleIdByRoleName(@Param("roleName") String roleName);

    /**
     * 查询所有角色名称
     * @return 角色名称
     */
    List<String> selectRoleNameAll();
}

package com.ly.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.dto.SystemUserRoleDto;
import com.ly.cloud.auth.po.SystemUserRolePo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.auth.vo.SystemUserRoleVo;
import com.ly.cloud.auth.vo.SystemUserVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户和角色关联表 Mapper 接口
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemUserRoleMapper extends BaseMapper<SystemUserRolePo> {
    /**
     * 按主键查询
     * @param id 主键ID
     * @return 用户和角色关联表
     */
    SystemUserRoleVo selectById(Long id);

    /**
     * 查询
     * @param page 分页参数
     * @param systemUserRoleDto 用户和角色关联表
     * @return 用户和角色关联表集合
     */
    IPage<SystemUserRoleVo> select(@Param("page") Page<SystemUserRoleVo> page, @Param("dto") SystemUserRoleDto systemUserRoleDto);
}

package com.ly.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.dto.SystemUserDto;
import com.ly.cloud.auth.po.SystemUserPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.auth.vo.SystemUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemUserMapper extends BaseMapper<SystemUserPo> {
    /**
     * 按主键查询
     * @param userId 主键ID
     * @return 用户信息表
     */
    SystemUserVo selectById(Long userId);

    /**
     * 按Dto查询
     * @param systemUserDto 用户信息dto
     * @return 用户信息表
     */
    SystemUserVo selectBydto(@Param("dto") SystemUserDto systemUserDto);


    /**
     * 查询
     * @param page 分页参数
     * @param systemUserDto 用户信息表
     * @return 用户信息表集合
     */
    IPage<SystemUserVo> select(@Param("page") Page<SystemUserVo> page, @Param("dto") SystemUserDto systemUserDto, @Param("childDeptIdList")List<Long> childDeptIdList);

    /**
     * 按部门ID查询
     * @param deptId 部门ID
     * @return 用户信息表
     */
    List<SystemUserVo> selectByDeptIds(@Param("list")List<Long> deptIds);

    /**
     * 根据角色ID查询该角色下用户数据
     * @param page 分页参数
     * @param id 角色Id
     * @return
     */
    IPage<SystemUserVo> selectUsersByRoleId(@Param("page") Page<SystemUserVo> page, @Param("roleId") String roleId);

    /**
     * 按主键集合查询
     * @param userIds 主键ID集合
     * @return 用户信息表
     */
    List<SystemUserVo> queryByIds(@Param("list")List<Long> userIds);
}

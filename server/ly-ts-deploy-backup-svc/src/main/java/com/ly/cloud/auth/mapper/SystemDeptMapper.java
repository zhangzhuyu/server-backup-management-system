package com.ly.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.dto.SystemDeptDto;
import com.ly.cloud.auth.po.SystemDeptPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.auth.vo.DeptChildParentIdPairVO;
import com.ly.cloud.auth.vo.SystemDeptVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemDeptMapper extends BaseMapper<SystemDeptPo> {

    /**
     * 按主键查询
     * @param deptId 主键ID
     * @return 部门表
     */
    SystemDeptVo selectById(Long deptId);

    /**
     * 查询
     * @param page 分页参数
     * @param systemDeptDto 部门表
     * @return 部门表集合
     */
    IPage<SystemDeptVo> select(@Param("page") Page<SystemDeptVo> page, @Param("dto") SystemDeptDto systemDeptDto);

    /**
     * 查询 树形结构
     * @param page 分页参数
     * @param systemDeptDto 部门表
     * @return 部门表集合
     */
    IPage<SystemDeptVo> queryTreeSystemDeptAll(@Param("page") Page<SystemDeptVo> page, @Param("dto") SystemDeptDto systemDeptDto);

    /**
     * 查询所有部门树形结构
     * @return 部门表集合
     */
    List<SystemDeptVo> queryAll();

    /**
     * 查询部门与上级部门的集合
     * @return 部门表集合
     */
    List<DeptChildParentIdPairVO> queryChildAndParent();

    /**
     * 根据主键查询所有子部门Id(包含当前部门)
     * @param id 当前菜单Id
     * @return 菜单表集合
     */
    List<SystemDeptVo> querySubDeptById(@Param("id") Long id);

    /**
     * 根据主键集合查询
     * @param page
     * @param ids 菜单Ids
     * @return 菜单权限表集合
     */
    IPage<SystemDeptVo> queryByIds(@Param("page") Page<SystemDeptVo> page, @Param("list") List<Long> ids, @Param("content") String content);

    /**
     * 查询所有部门名称
     * @return 部门名称
     */
    List<String> queryDeptNameAll();
}

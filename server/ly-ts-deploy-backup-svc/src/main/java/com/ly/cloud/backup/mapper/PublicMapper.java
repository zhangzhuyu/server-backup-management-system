package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.UserDto;
import com.ly.cloud.backup.vo.PublicBaseVo;
import com.ly.cloud.backup.vo.SysUserVo;
import com.ly.cloud.backup.vo.TreeSelectVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2022-04-27
 * @version: 1.0
 */
@Mapper
public interface PublicMapper {

    String findRobotUrl();

    /**
     * 应用下拉框
     */
    List<PublicBaseVo> findApplicationList(@Param("content") String content,@Param("whetherMonitoring")String whetherMonitoring);

    /**
     * 服务下拉框
     */
    List<PublicBaseVo> findServiceList(@Param("content") String content);

    /**
     * 根据应用id查询全部服务信息
     * @param id 应用id
     * @return 应用服务关系表
     */
    List<PublicBaseVo> selectAllServiceByAppId(@Param("id") Long id);

    /**
     * Nginx下拉框
     */
    List<PublicBaseVo> findNginxList(@Param("content") String content);

    /**
     * 服务器下拉框
     */
    List<PublicBaseVo> findServerList(@Param("content") String content);

    /**
     * 服务器下拉框
     */
    List<PublicBaseVo> findDatabaseList(@Param("content") String content);

    /**
     * 角色下拉框
     */
    List<PublicBaseVo> findRoleList(@Param("content") String content);

    /**
     * 部门树
     */
    List<TreeSelectVo> getDepartment(@Param("content") String content);

    /**
     * 查询 菜单权限树形结构下拉框
     * @param content 查询参数
     * @return 菜单权限表集合
     */
    List<TreeSelectVo> selectTreeSystemMenu(@Param("content") String content);

    /**
     * 查询 部门树形结构下拉框
     * @param content 查询参数
     * @return 部门表集合
     */
    List<TreeSelectVo> selectTreeSystemDept(@Param("content") String content);

    /**
     * 根据部门查询 用户树形结构下拉框
     * @param deptId 部门Id，content 查询参数
     * @return 用户表集合
     */
    List<TreeSelectVo> selectTreeSystemUserByDeptId(@Param("deptId") String deptId,@Param("content") String content);

    /**
     * 用户下拉框
     */
    List<SysUserVo> findUserList(@Param("content") String content);

    IPage<SysUserVo> listUser(@Param("page") Page<SysUserVo> page, @Param("dto") UserDto dto);

    SysUserVo findUserById(@Param("id") String id);

    String getServerName(@Param("id") String id);

    String getDatabaseName(@Param("id") String id);

    String getServiceName(@Param("id") String id);

    String getApplicationName(@Param("id") String id);

    String getMiddlewareName(@Param("id") String id);

    List<PublicBaseVo> findMiddlewareList(@Param("content") String content);
}

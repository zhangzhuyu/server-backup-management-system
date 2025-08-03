package com.ly.cloud.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.auth.dto.SystemDeptDto;
import com.ly.cloud.auth.po.SystemDeptPo;
import com.ly.cloud.auth.vo.SystemDeptVo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemDeptService extends IService<SystemDeptPo> {
    /**
     * 添加
     * @param systemDeptDto 部门表
     */
    void create(SystemDeptDto systemDeptDto);

    /**
     * 删除
     * @param ids 主键ID数组
     */
    void delete(Long[] ids);

    /**
     * 更新
     * @param systemDeptDto 部门表
     */
    void update(SystemDeptDto systemDeptDto);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 部门表
     */
    SystemDeptVo find(Long id);

    /**
     * 查询
     * @param page, pageSize, deptId, content 分页查询参数
     * @return 部门表分页数据
     */
    IPage<SystemDeptVo> query(Integer page, Integer pageSize, String deptId, String content);

    /**
     * 所有部门查询
     * @return 部门表分页数据
     */
    List<SystemDeptVo> queryAll();
}

package com.ly.cloud.backup.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.ServiceDto;
import com.ly.cloud.backup.po.ServicePo;
import com.ly.cloud.backup.vo.ServiceVo;

/**
 * 服务信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface ServiceMapper extends BaseMapper<ServicePo> {

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 服务信息表
     */
    ServiceVo selectById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param serviceDto 服务信息表
     * @return 服务信息表集合
     */
    IPage<ServiceVo> select(@Param("page") Page<ServiceVo> page, @Param("dto") ServiceDto serviceDto);

    /**
     * 按条件查询
     * @param serviceDto 主键ID
     * @return 服务信息表
     */
    List<ServiceVo> selectAllList(@Param("dto")ServiceDto serviceDto);

    /**
     *  更新状态 异常
     * @param list
     * @return
     */
    Integer updateStatusErr(@Param("list") List<String> list);

    /**
     *  更新状态 健康
     * @param list
     * @return
     */
    Integer updateStatusSucc(@Param("list") List<String> list);

    /**
     * 根据app_id和healthStatus统计的服务数量
     * @param applicationId
     * @return
     */
    List<ServiceVo> selectServiceByAppId(@Param("applicationId") String applicationId, @Param("healthStatus") String healthStatus);

    /**
     * healthStatus统计的服务数量
     * @param healthStatus
     * @return
     */
    List<ServiceVo> selectService(@Param("healthStatus") String healthStatus);


    /**
     * 查询还没归集的服务（没有归属到应用里面的服务）
     */
    List<ServiceVo> selectServicesByNoApp();

}
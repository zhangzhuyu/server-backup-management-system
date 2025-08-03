package com.ly.cloud.backup.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.ApplicationServiceDto;
import com.ly.cloud.backup.po.ApplicationServicePo;
import com.ly.cloud.backup.vo.ApplicationServiceVo;

/**
 * 应用服务关系表
 * @author chenguoqing
 *
 */
@Mapper
public interface ApplicationServiceMapper extends BaseMapper<ApplicationServicePo> {

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 应用服务关系表
     */
    ApplicationServiceVo selectById(@Param("id") Long id);

    /**
     * 根据应用id删除所有的服务id
     */
    void deleteByApplicationId(@Param("id") Long id);

    /**
     * 根据条件查询全部
     * @param applicationServiceDto 应用服务关系表
     * @return 应用服务关系表
     */
    List<ApplicationServiceVo> selectAll(ApplicationServiceDto applicationServiceDto);

    /**
     * 根据应用id查询全部服务id
     * @param id 应用id
     * @return 应用服务关系表
     */
    List<String> selectAllService(@Param("id") Long id);

    /**
     * 根据服务id查询全部应用id
     * @param id 服务id
     * @return 应用服务关系表
     */
    List<String> selectAllApplication(@Param("id") Long id);

    /**
     * 根据应用id查询全部服务信息
     * @param id 应用id
     * @return 应用服务关系表
     */
    List<ApplicationServiceVo> selectServiceInfo(@Param("id") Long id);

    /**
     * 查询
     * @param page 分页参数
     * @param applicationServiceDto 应用服务关系表
     * @return 应用服务关系表集合
     */
    IPage<ApplicationServiceVo> select(@Param("page") Page<ApplicationServiceVo> page,ApplicationServiceDto applicationServiceDto);

    /**
     * 根据app_id统计异常的服务数量
     */
    Integer countErrorServiceByAppId(@Param("applicationId") String applicationId);

    /**
     * 根据app_id和healthStatus统计的服务数量
     * @param applicationId
     * @return
     */
    Integer countServiceByAppId(@Param("applicationId") String applicationId,@Param("healthStatus") String healthStatus);

    /**
     * 根据app_id和healthStatus分组统计的服务数量
     * @return
     */
    List<Map<String,Object>> countServiceGroupByAppId(@Param("healthStatus") String healthStatus);

    /**
     * 根据应用id查询应用下全部服务信息
     * @param id 应用id
     * @return 应用服务关系表
     */
    List<ApplicationServiceVo> selectServices(@Param("id") Long id);

}
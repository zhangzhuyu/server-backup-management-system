package com.ly.cloud.backup.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.ApplicationDto;
import com.ly.cloud.backup.po.ApplicationPo;
import com.ly.cloud.backup.vo.ApplicationVo;

/**
 * 应用信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<ApplicationPo> {


    /**
     * 查询最大的序号值
     */
    Integer selectMaxSerialNumber();

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 应用信息表
     */
    ApplicationVo selectById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param applicationDto 应用信息表
     * @return 应用信息表集合
     */
    IPage<ApplicationVo> select(@Param("page") Page<ApplicationVo> page, @Param("dto")ApplicationDto applicationDto);

    /**
     *  运维监控大屏_重点服务监控查询
     */
    List<ApplicationVo> selectKeyServiceMonitoring();

    /**
     * 根据服务id去修改应用的健康状态
     */
    void updateHealthStatusByServerId(@Param("serverId") Long serverId,@Param("healthStatus")String healthStatus);

    /**
     * 根据应用id去查询所属服务下服务的健康状态并修改应用的健康状态
     */
    void updateHealthStatusByapplicationId(@Param("applicationId") Long applicationId);

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
     *  查询应用信息和服务信息
     */
    List<ApplicationVo> select(@Param("dto")ApplicationDto applicationDto);



}
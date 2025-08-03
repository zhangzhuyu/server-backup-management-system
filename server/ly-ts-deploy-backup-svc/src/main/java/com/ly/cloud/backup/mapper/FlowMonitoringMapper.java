package com.ly.cloud.backup.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import com.ly.cloud.backup.dto.FlowMonitoringDto;
import com.ly.cloud.backup.po.FlowMonitoringPo;
import com.ly.cloud.backup.vo.FlowMonitoringVo;
import org.apache.ibatis.annotations.Param;

/**
 * 流量监控表
 * @author chenguoqing
 *
 */
@Mapper
public interface FlowMonitoringMapper extends BaseMapper<FlowMonitoringPo> {

    /**
     * 根据nginxId查询 最大的序号值
     */
    Integer selectMaxSerialNumberByNginx(@Param("id")Long id);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 流量监控表
     */
    FlowMonitoringVo selectById(Long id);
    
    /**
     * 查询
     * @param flowMonitoringDto 流量监控表
     * @return 流量监控表集合
     */
    IPage<FlowMonitoringVo> select(@Param("page") Page<FlowMonitoringVo> page, @Param("dto")FlowMonitoringDto flowMonitoringDto);

    /**
     * 查询
     * @param flowMonitoringDto 流量监控表
     * @return 流量监控表集合
     */
    List<FlowMonitoringVo> selectAllById(@Param("dto")FlowMonitoringDto flowMonitoringDto);
}
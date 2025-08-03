package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.common.handler.batchandle.CustomMapper;
import com.ly.cloud.backup.dto.PerformanceMonitorDto;
import com.ly.cloud.backup.po.PerformanceMonitorPo;
import com.ly.cloud.backup.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PerformanceMonitorMapper extends CustomMapper<PerformanceMonitorPo> {

    public List<PerformanceMonitorNapeVo> getPerformanceHardwareList();

    public List<PerformanceMonitorNapeVo> findCategoriesByParentNapeType();

    List<PerformanceMonitorTargetSourceServerVo> getTargetSourceServerList();

    List<PerformanceMonitorTargetSourceDatabaseVo> getTargetSourceDataBaseList();

    List<PerformanceMonitorTargetSourceMiddlewareVo> getTargetSourceMiddlewareList();

    List<PerformanceMonitorTargetSourceApplicationVo> getTargetSourceApplicationList();

    IPage<PerformanceMonitorVo> list(@Param("page") Page<PerformanceMonitorVo> list, @Param("dto") PerformanceMonitorDto dto);

    List<PerformanceMonitorNapeVo> getNapeTypeList();

    List<PerformanceMonitorTargetSourceServerVo> findTargetSourceList();

    void deleteTargetSourceByIdBatch(@Param("list") List<String> targetSourceIdList);

    List<StrategicModeVo> findStrategicModeList();

    void updatStart(@Param("id") Long id);

    List<PerformanceMonitorPo> selectByName(@Param("name") String name);
}

package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.InspectionStrategyDto;
import com.ly.cloud.backup.po.InspectionStrategyPo;
import com.ly.cloud.backup.vo.InspectionStrategyVo;

/**
 * 巡检策略信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface InspectionStrategyMapper extends BaseMapper<InspectionStrategyPo> {


    /**
     * 按主键查询
     * @param id 主键ID
     * @return 巡检策略信息表
     */
    InspectionStrategyVo selectById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param inspectionStrategyDto 巡检策略信息表
     * @return 巡检策略信息表集合
     */
    IPage<InspectionStrategyVo> select(@Param("page") Page<InspectionStrategyVo> page, @Param("dto")InspectionStrategyDto inspectionStrategyDto);
}
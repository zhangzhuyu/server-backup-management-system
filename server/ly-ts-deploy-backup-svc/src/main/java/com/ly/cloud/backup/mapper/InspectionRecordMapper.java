package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.InspectionRecordDto;
import com.ly.cloud.backup.po.InspectionRecordPo;
import com.ly.cloud.backup.vo.InspectionRecordVo;

/**
 * 巡检记录信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface InspectionRecordMapper extends BaseMapper<InspectionRecordPo> {
    

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 巡检记录信息表
     */
    InspectionRecordVo selectById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param inspectionRecordDto 巡检记录信息表
     * @return 巡检记录信息表集合
     */
    IPage<InspectionRecordVo> select(@Param("page") Page<InspectionRecordVo> page, @Param("dto")InspectionRecordDto inspectionRecordDto);
}
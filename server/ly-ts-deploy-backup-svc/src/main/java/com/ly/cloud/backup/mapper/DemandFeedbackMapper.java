package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.DemandFeedbackDto;
import com.ly.cloud.backup.po.DemandFeedbackPo;
import com.ly.cloud.backup.vo.DemandFeedbackVo;

/**
 * 需求反馈信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface DemandFeedbackMapper extends BaseMapper<DemandFeedbackPo> {

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 需求反馈信息表
     */
    DemandFeedbackVo selectById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param demandFeedbackDto 需求反馈信息表
     * @return 需求反馈信息表集合
     */
    IPage<DemandFeedbackVo> select(@Param("page") Page<DemandFeedbackVo> page, @Param("dto")DemandFeedbackDto demandFeedbackDto);
}
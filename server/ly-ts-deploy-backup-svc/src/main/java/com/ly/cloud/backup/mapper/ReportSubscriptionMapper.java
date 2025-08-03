package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.ReportSubscriptionDto;
import com.ly.cloud.backup.po.ReportSubscriptionPo;
import com.ly.cloud.backup.vo.ReportSubscriptionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportSubscriptionMapper extends BaseMapper<ReportSubscriptionPo> {

    /**
     * 查询
     * @param page 分页参数
     * @param ReportSubscriptionDto 订阅报告表
     * @return 订阅报告表集合
     */
    IPage<ReportSubscriptionVo> select(@Param("page") Page<ReportSubscriptionVo> page, @Param("dto") ReportSubscriptionDto ReportSubscriptionDto);

}
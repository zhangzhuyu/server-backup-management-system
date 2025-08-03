package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.ReportSetDto;
import com.ly.cloud.backup.po.ReportSetPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.vo.ReportSetVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author admin
* @description 针对表【ly_or_report_set】的数据库操作Mapper
* @createDate 2023-03-30 12:03:10
* @Entity com.ly.cloud.tc.po.ReportSet
*/
@Mapper
public interface ReportSetMapper extends BaseMapper<ReportSetPo> {

    /**
     * 查询
     * @param page 分页参数
     * @param reportTemplateVo 运维报告设置表
     * @return 运维报告设置集合
     */
    IPage<ReportSetVo> select(@Param("page") Page<ReportSetVo> page, @Param("dto") ReportSetDto reportTemplateVo);

}





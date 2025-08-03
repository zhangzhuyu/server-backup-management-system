package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.ReportTemplateDto;
import com.ly.cloud.backup.po.ReportTemplatePo;
import com.ly.cloud.backup.vo.ReportTemplateVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author admin
* @description 针对表【ly_or_report_template】的数据库操作Mapper
* @createDate 2023-03-30 11:10:50
*/
@Mapper
public interface ReportTemplateMapper extends BaseMapper<ReportTemplatePo> {

    /**
     * 查询
     * @param page 分页参数
     * @param reportTemplateVo 运维报告模板表
     * @return 运维报告模板集合
     */
    IPage<ReportTemplateVo> select(@Param("page") Page<ReportTemplateVo> page, @Param("dto") ReportTemplateDto reportTemplateVo);


}





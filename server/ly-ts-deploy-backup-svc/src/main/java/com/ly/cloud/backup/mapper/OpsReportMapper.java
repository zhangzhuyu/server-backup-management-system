package com.ly.cloud.backup.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.OpsReportDto;
import com.ly.cloud.backup.po.OpsReportPo;
import com.ly.cloud.backup.vo.OpsReportVo;

/**
 * 运维报告信息表
 *
 * @author chenguoqing
 */
@Mapper
public interface OpsReportMapper extends BaseMapper<OpsReportPo> {


    /**
     * 按主键查询
     * @param id 主键ID
     * @return 运维报告信息表
     */
    OpsReportVo selectById(Long id);

    /**
     * 按条件查询
     *
     * @param opsReportDto 运维报告信息表
     * @return 运维报告信息表
     */
    List<OpsReportVo> selectByDto(@Param("dto") OpsReportDto opsReportDto);

    /**
     * 查询
     *
     * @param page         分页参数
     * @param opsReportDto 运维报告信息表
     * @return 运维报告信息表集合
     */
    IPage<OpsReportVo> select(@Param("page") Page<OpsReportVo> page, @Param("dto") OpsReportDto opsReportDto);
}
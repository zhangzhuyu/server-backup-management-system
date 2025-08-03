package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.ReportSubscriberDto;
import com.ly.cloud.backup.dto.ReportSubscriptionDto;
import com.ly.cloud.backup.po.ReportSubscriberPo;
import com.ly.cloud.backup.vo.ReportSubscriberVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author admin
 * @description 针对表【ly_or_report_subscriber】的数据库操作Mapper
 * @createDate 2023-02-23 10:48:15
 */
@Mapper
public interface ReportSubscriberMapper extends BaseMapper<ReportSubscriberPo> {

    /**
     * 查询
     * @param page 分页参数
     * @param reportSubscriberDto 报告订阅人表
     * @return 报告订阅人表集合
     */
    IPage<ReportSubscriberVo> select(@Param("page") Page<ReportSubscriberVo> page, @Param("dto") ReportSubscriberDto reportSubscriberDto);

    /**
     * 查询集合
     */
    List<ReportSubscriberVo> selectListAll(@Param("dto") ReportSubscriberDto reportSubscriberDto);

    /**
     * 根据订阅类型查询订阅人Id集合
     */
    List<ReportSubscriberVo> selectUserIdListAll(@Param("dto") ReportSubscriptionDto reportSubscriptionDto);

}

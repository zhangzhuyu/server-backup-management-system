package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.dto.OpsReportDto;
import com.ly.cloud.backup.po.WarningRecordObjectPo;
import com.ly.cloud.backup.vo.WarningRecordObjectVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 告警记录对象一对一存储类
 * @author SYC
 * @since 2022-08-17 17:44:00
 */
@Mapper
public interface WarningRecordObjectMapper extends BaseMapper<WarningRecordObjectPo> {
    /**
     * 获取按告警目标（等级）统计数据
     */
    public List<WarningRecordObjectVo> alarmTargetStatistics(@Param("dto") OpsReportDto opsReportDto);

}
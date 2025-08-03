package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.WarningPushRecordDto;
import com.ly.cloud.backup.po.WarningMessageRecordPo;
import com.ly.cloud.backup.vo.WarningPushRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息发送记录
 * @author SYC
 */
@Mapper
public interface WarningMessageRecordMapper extends BaseMapper<WarningMessageRecordPo> {


    IPage<WarningPushRecordVo> list(@Param("page") Page<WarningPushRecordVo> page, @Param("dto") WarningPushRecordDto warningPushRecordDto);

    List<WarningPushRecordVo> ruleList(@Param("searchKey") String searchKey, @Param("ruleId") String ruleId);

    void deleteRecordByRuleId(@Param("ruleIds") List<String> ruleId);
}
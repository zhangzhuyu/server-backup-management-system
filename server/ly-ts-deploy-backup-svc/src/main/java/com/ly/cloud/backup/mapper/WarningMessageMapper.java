package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.po.WarningMessagePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息发送记录预警记录
 * @author SYC
 */
@Mapper
public interface WarningMessageMapper extends BaseMapper<WarningMessagePo> {


    void deleteMessageByRuleId(@Param("ruleIds") List<String> ruleId);
}
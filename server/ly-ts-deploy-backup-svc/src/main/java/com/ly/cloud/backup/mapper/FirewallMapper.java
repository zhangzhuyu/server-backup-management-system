package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.FirewallDto;
import com.ly.cloud.backup.po.FirewallPo;
import com.ly.cloud.backup.vo.FirewallVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @Entity generator.domain.FirewallPo
*/
@Mapper
public interface FirewallMapper extends BaseMapper<FirewallPo> {


    IPage<FirewallVo> select(@Param("page") Page<FirewallVo> page, @Param("dto") FirewallDto firewallDto);

}

package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.RecoveryStrategyDto;
import com.ly.cloud.backup.po.RecoveryStrategyPo;
import com.ly.cloud.backup.vo.RecoveryStrategyVo;

/**
 * 自愈策略信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface RecoveryStrategyMapper extends BaseMapper<RecoveryStrategyPo> {


    /**
     * 按主键查询
     * @param id 主键ID
     * @return 自愈策略信息表
     */
    RecoveryStrategyVo selectById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param recoveryStrategyDto 自愈策略信息表
     * @return 自愈策略信息表集合
     */
    IPage<RecoveryStrategyVo> select(@Param("page") Page<RecoveryStrategyVo> page, @Param("dto")RecoveryStrategyDto recoveryStrategyDto);
}
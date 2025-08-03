package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.MessageManagementDto;
import com.ly.cloud.backup.po.MessageManagementPo;
import com.ly.cloud.backup.vo.MessageManagementVo;

/**
 * 消息管理信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface MessageManagementMapper extends BaseMapper<MessageManagementPo> {


    /**
     * 按主键查询
     * @param id 主键ID
     * @return 消息管理信息表
     */
    MessageManagementVo selectById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param messageManagementDto 消息管理信息表
     * @return 消息管理信息表集合
     */
    IPage<MessageManagementVo> select(@Param("page") Page<MessageManagementVo> page, @Param("dto")MessageManagementDto messageManagementDto);
}
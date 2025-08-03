package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.po.DatabaseServerPo;
import org.apache.ibatis.annotations.Mapper;


/**
 * 数据库服务器关系表
 * @author SYC
 *
 */
@Mapper
public interface DatabaseServerMapper extends BaseMapper<DatabaseServerPo> {

}
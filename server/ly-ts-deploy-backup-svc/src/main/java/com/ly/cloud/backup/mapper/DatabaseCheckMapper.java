package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.DatabaseCheckDto;
import com.ly.cloud.backup.po.DatabaseCheckPo;
import com.ly.cloud.backup.vo.DatabaseCheckVo;

/**
 * 数据库核验信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface DatabaseCheckMapper extends BaseMapper<DatabaseCheckPo> {

    /**
     * 查询
     * @param page 分页参数
     * @param databaseCheckDto 数据库核验信息表
     * @return 数据库核验信息表集合
     */
    IPage<DatabaseCheckVo> select(@Param("page") Page<DatabaseCheckVo> page, @Param("dto")DatabaseCheckDto databaseCheckDto);

}
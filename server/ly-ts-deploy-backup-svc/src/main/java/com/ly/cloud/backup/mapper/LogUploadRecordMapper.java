package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.po.LogUploadRecordPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: wzz
 * @date: 2023-03-31
 */
@Mapper
public interface LogUploadRecordMapper extends BaseMapper<LogUploadRecordPo> {

    /**
     * 根据条件分页查询日志上传记录
     *
     * @param page
     * @param content 角色信息
     * @return 日志上传数据集合信息
     */
    IPage<LogUploadRecordPo> select(@Param("page") Page<LogUploadRecordPo> page, @Param("content")String content);
}

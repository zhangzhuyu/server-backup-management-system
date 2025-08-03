package com.ly.cloud.backup.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.po.WarningRecordObjectPo;
import com.ly.cloud.backup.vo.WarningRecordListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.WarningRecordDto;
import com.ly.cloud.backup.po.WarningRecordPo;

/**
 * 告警记录信息表
 *
 * @author chenguoqing
 */
@Mapper
public interface WarningRecordMapper extends BaseMapper<WarningRecordPo> {

    public Integer insertObjects(@Param("list") List<WarningRecordObjectPo> list);
    /**
     * 批量插入po的方法
     *
     * @param list : po数据集
     * @author: jiangzhongxin
     * @date: 2022/3/4 15:36
     */
    public void insertPoBatch(@Param("list") List<WarningRecordPo> list);

    public List<String> findUserByServer(@Param("warningObject") String warningObject);

    public List<String> findUserByDatabase(@Param("ip") String ip,@Param("port") String port);

    public List<String> findUserByService(@Param("warningObject") String warningObject);

    public List<String> findUserByApplication(@Param("warningObject") String warningObject);

    /**
     * 批量删除数据
     *
     * @param ids : 主键集合
     * @return Integer : 删除数量
     * @author: jiangzhongxin
     * @date: 2022/3/4 15:38
     */
    public Integer deleteByMulti(@Param("list") List<String> ids);
    
    public Integer deleteRecordObject(@Param("list") List<String> ids);

    /**
     *  删除与记录相关的工单数据
     * @param ids
     * @return
     */
    public Integer deleteWorkOrderByRecordId(@Param("list") List<String> ids);

    /**
     * 条件查询告警记录信息
     *
     * @param page             : 分页辅助页
     * @param warningRecordDto : 条件
     * @author: jiangzhongxin
     * @date: 2022/3/17 10:40
     * @return: List<WarningRecordVo> ：告警记录信息
     */
    public IPage<WarningRecordListVo> list(@Param("page") Page<WarningRecordListVo> page, @Param("dto") WarningRecordDto warningRecordDto);


    String findGapTime();

    public List<WarningRecordListVo> selectWarningRecordByIds(@Param("ids") List<String> ids);

    List<String> selectAllRecordId();
}
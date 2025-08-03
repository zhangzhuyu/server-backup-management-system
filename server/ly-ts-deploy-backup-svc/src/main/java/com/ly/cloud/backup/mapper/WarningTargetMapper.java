package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.WarningFieldDto;
import com.ly.cloud.backup.dto.WarningTargetDto;
import com.ly.cloud.backup.po.WarningTargetPo;
import com.ly.cloud.backup.vo.WarningFieldVo;
import com.ly.cloud.backup.vo.WarningTargetVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 告警记录信息表
 *
 * @author chenguoqing
 */
@Mapper
public interface WarningTargetMapper extends BaseMapper<WarningTargetPo> {

    /**
     * 批量插入po的方法
     *
     * @param list : po数据集
     * @author: jiangzhongxin
     * @date: 2022/3/4 15:36
     */
    public void insertPoBatch(@Param("list") List<WarningTargetPo> list);

    /**
     * 批量删除数据
     *
     * @param ids : 主键集合
     * @return Integer : 删除数量
     * @author: jiangzhongxin
     * @date: 2022/3/4 15:38
     */
    public Integer deleteByMulti(@Param("list") List<String> ids);

    public Integer enable(@Param("id") String id, @Param("status") String status);

    /**
     * 条件查询告警记录信息
     *
     * @param page             : 分页辅助页
     * @param warningTargetDto : 条件
     * @author: jiangzhongxin
     * @date: 2022/3/17 10:40
     * @return: List<WarningTargetVo> ：告警记录信息
     */
    public IPage<WarningTargetVo> list(@Param("page") Page<WarningTargetVo> page, @Param("dto") WarningTargetDto warningTargetDto);

    /**
     *  条件查询告警字段
     * @param page
     * @return
     */
    public IPage<WarningFieldVo> listField(@Param("page") Page<WarningFieldVo> page, @Param("dto") WarningFieldDto warningFieldDto);

    /**
     * 修改所有的指标状态
     * @param statusCondition
     * @param status
     * @return
     */
    Integer updateAllStatus(@Param("statusCondition") String statusCondition, @Param("status") String status);
    /**
     * 查找所有system下的指标的中英文对照表
     * @return
     */
    @Select("select chinese_name chineseName , english_name englishName from ly_mw_warning_field ${ew.customSqlSegment}")
    List<WarningFieldVo> selectEnZhList(@Param(Constants.WRAPPER) QueryWrapper<WarningFieldVo> wrapper);
}
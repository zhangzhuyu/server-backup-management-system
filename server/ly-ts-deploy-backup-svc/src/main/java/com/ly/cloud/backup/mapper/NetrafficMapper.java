package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.NetrafficDto;
import com.ly.cloud.backup.vo.NetrafficDetailVo;
import com.ly.cloud.backup.vo.NetrafficVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 网络与域名-流量监控Mapper
 *
 * @author jiangzhongxin
 * @Entity com.ly.cloud.tc.po.Exception
 */
@Mapper
public interface NetrafficMapper {

    /*
     * 以下写查询Mapper
     */

    /**
     * 条件查询流量监控列表数据
     *
     * @param page          : 实现分页辅助
     * @param netrafficDto  : 服务名称/告警等级/搜索内容相关参数
     * @author: jiangzhongxin
     * @date: 2022/2/24 17:06
     * @return: List<NetrafficVo> : 流量监控列表数据
     */
    public List<NetrafficVo> selectPageLike(@Param("page") Page<NetrafficVo> page, @Param("dto") NetrafficDto netrafficDto);

    /**
     * 查看详情
     *
     * @param param : param
     * @author: jiangzhongxin
     * @date: 2022/3/4 14:04
     * @return: NetrafficDetailVo
     */
    public NetrafficDetailVo detail(@Param("param") String param);


    /*
     * 以下写插入Mapper
     */

    /*
     * 以下写更新Mapper
     */

    /*
     * 以下写删除Mapper
     */

}





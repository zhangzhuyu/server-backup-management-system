package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.common.handler.batchandle.CustomMapper;
import com.ly.cloud.backup.dto.WarningSubMethodDto;
import com.ly.cloud.backup.dto.WarningSubscriptionDto;
import com.ly.cloud.backup.po.WarningSubscriptionContentPo;
import com.ly.cloud.backup.po.WarningSubscriptionObjectPo;
import com.ly.cloud.backup.po.WarningSubscriptionPo;
import com.ly.cloud.backup.vo.UserVo;
import com.ly.cloud.backup.vo.WarningSubMethodVo;
import com.ly.cloud.backup.vo.WarningSubscriptionListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: jiangzhongxin
 * @Entity com.ly.cloud.tc.po.WarningSubscriptionPo
 */
@Mapper
public interface WarningSubscriptionMapper extends CustomMapper<WarningSubscriptionPo> {


    /**
     * 批量插入po的方法
     *
     * @param list po数据集
     * @author: jiangzhongxin
     * @date: 2022/3/22 13:38
     */
    public void insertPoBatch(@Param("list") List<WarningSubscriptionPo> list);

    public void insertPoBatchContent(@Param("list") List<WarningSubscriptionContentPo> list);

    public void insertPoBatchObject(@Param("list") List<WarningSubscriptionObjectPo> list);


    public List<WarningSubscriptionContentPo> getContentData(@Param("subId") String subId, @Param("subType") String subType);


    public List<WarningSubscriptionObjectPo> getObjectData(@Param("subId") String subId);

    public List<String> getUserList(@Param("subId") String subId);

    public void delContentById(@Param("subId") String subId);

    public void delUserById(@Param("subId") String subId);

    public Integer enable(@Param("id") String id,  @Param("type") String type, @Param("status") String status);

    public Integer updataSubMethod(@Param("dto") WarningSubMethodDto dto);

    public List<WarningSubMethodVo> listSubMethod();

    public String getSubMethodById(@Param("levelId") String levelId);

    public IPage<WarningSubscriptionListVo> list(@Param("page") Page<WarningSubscriptionListVo> page, @Param("dto") WarningSubscriptionDto dto);

    List<WarningSubscriptionContentPo> selectByIdAndType(@Param("id") String id, @Param("type") String type);

    List<UserVo> selectUserBySubscriptionId(@Param("id") String id);

    List<String> selectAlllevelId();

}





package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.po.BusinessDomainPo;
import com.ly.cloud.backup.vo.SelectVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业务领域代码mapper dto
 * @author SYC
  */
@Mapper
public interface BusinessDomainMapper extends BaseMapper<BusinessDomainPo> {

    /**
     * 按照不同的业务分类，分组统计应用的数量
     */
   List<SelectVo> businessDomainCount(@Param("searchParam") String searchParam);
}
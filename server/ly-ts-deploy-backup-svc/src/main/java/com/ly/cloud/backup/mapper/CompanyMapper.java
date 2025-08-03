package com.ly.cloud.backup.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.CompanyDto;
import com.ly.cloud.backup.po.CompanyPo;
import com.ly.cloud.backup.vo.CompanyVo;

/**
 * 公司信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface CompanyMapper extends BaseMapper<CompanyPo> {

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 公司信息表
     */
    CompanyVo selectById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param companyDto 公司信息表
     * @return 公司信息表集合
     */
    IPage<CompanyVo> select(@Param("page") Page<CompanyVo> page,@Param("dto")CompanyDto companyDto);

    /**
     * 查询树形部门信息_ALL
     * @param
     * @return 树形部门信息
     */
    List<CompanyVo> queryTreeCompanyAll(@Param("content") String content);

    /**
     * 查询树形部门信息_服务器信息
     * @param
     * @return 树形部门信息
     */
    List<CompanyVo> queryTreeCompanyToServer(@Param("affiliatedCompany") Long affiliatedCompany,@Param("buttonType")Integer buttonType,@Param("content") String content);

    /**
     * 查询树形部门信息_Nginx信息
     * @param
     * @return 树形部门信息
     */
    List<CompanyVo> queryTreeCompanyToNginx(@Param("affiliatedCompany")Long affiliatedCompany,@Param("buttonType")Integer buttonType,@Param("content") String content);

    /**
     * 查询树形部门信息_服务器的防火墙信息
     * @param
     * @return 树形部门信息
     */
    List<CompanyVo> queryTreeCompanyToFirewall(@Param("affiliatedCompany")Long affiliatedCompany,@Param("buttonType")Integer buttonType,@Param("content") String content);

}
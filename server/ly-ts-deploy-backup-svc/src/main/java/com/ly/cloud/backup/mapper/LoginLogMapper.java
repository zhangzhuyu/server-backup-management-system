package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.po.LoginLogPo;
import com.ly.cloud.backup.vo.LoginLogVo;

import java.util.HashMap;
import java.util.List;

/**
 * 登录日志信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLogPo> {


    public LoginLogPo selectOne(@Param("token") String token, @Param("userAccount") String userAccount);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 登录日志信息表
     */
    public LoginLogVo getDataById(String id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param columnKey 排序列
     * @param order 排序方向
     * @param searchParam 搜索框内容
     * @return 登录日志信息表集合
     */
    public IPage<LoginLogVo> select(@Param("page") Page<LoginLogVo> page, @Param("columnKey")String columnKey, @Param("order")String order, @Param("searchParam")String searchParam);

    /**
     * 获取用户ID和名称
     */
    public List<HashMap<String,String>> getUserInfo();
}
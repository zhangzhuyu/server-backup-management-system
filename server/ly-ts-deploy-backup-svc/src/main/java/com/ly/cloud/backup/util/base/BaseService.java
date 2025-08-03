package com.ly.cloud.backup.util.base;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Author: gyc
 * @Date: 2019/11/8 10:26
 */


public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M,T> implements IBaseService<T> {

    @Autowired
    protected M baseMapper;

    public IPage<T> page(Integer current, Integer pageSize,HttpServletRequest request){
        IPage<T>  page = new Page<>(current,pageSize);

        QueryWrapper<T> queryWrapper = getQueryWrapper(request);
        IPage<T> resultPage = baseMapper.selectPage(page, queryWrapper);
        return resultPage;
    }


    public IPage<T> page(Integer current, Integer pageSize,QueryWrapper<T> queryWrapper){
        IPage<T>  page = new Page<>(current,pageSize);
        IPage<T> resultPage = baseMapper.selectPage(page, queryWrapper);
        return resultPage;
    }



    public QueryWrapper<T>  getQueryWrapper(HttpServletRequest request){

        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();


        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {
            if(key.endsWith("_eq")){
                String paramsKey = key.substring(0, key.lastIndexOf("_eq"));
                tQueryWrapper.eq(paramsKey,parameterMap.get(key)[0]);
            }else if(key.endsWith("_like")){
                String paramsKey = key.substring(0, key.lastIndexOf("_like"));
                tQueryWrapper.like(paramsKey,parameterMap.get(key)[0]);
            } else if(key.endsWith("_isNotNull")){
                String paramsKey = key.substring(0, key.lastIndexOf("_isNotNull"));
                String[] strings = parameterMap.get(key);
                if("1".equals(strings[0])){
                    tQueryWrapper.isNotNull(paramsKey);
                }else if("0".equals(strings[0])){
                    tQueryWrapper.isNull(paramsKey);
                }
            }else if(key.endsWith("_sort")){
                String paramsKey = key.substring(0, key.lastIndexOf("_sort"));
                String[] strings = parameterMap.get(key);
                if(StringUtils.equalsIgnoreCase("DESC",strings[0])){
                    tQueryWrapper.orderByDesc(paramsKey);
                }else if(StringUtils.equalsIgnoreCase("ASC",strings[0])){
                    tQueryWrapper.orderByAsc(paramsKey);
                }
            }

        }
        return tQueryWrapper;

    }

}

package com.ly.cloud.backup.util.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: gyc
 */
public interface IBaseService<T> extends IService<T> {

    public IPage<T> page(Integer current, Integer pageSize, HttpServletRequest request);


    public IPage<T> page(Integer current, Integer pageSize, QueryWrapper<T> queryWrapper);

}
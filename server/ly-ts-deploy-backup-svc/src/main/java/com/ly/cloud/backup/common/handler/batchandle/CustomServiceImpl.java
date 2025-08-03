package com.ly.cloud.backup.common.handler.batchandle;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
public class CustomServiceImpl<M extends CustomMapper<T>, T> extends ServiceImpl<M, T> {

    public static final Integer LENGTH = 1000;

    @Override
    public boolean saveBatch(Collection<T> entityList) {
        return saveBatch(entityList, LENGTH);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        List<T> objs = Lists.newArrayList(entityList);
        List<List<T>> partitions = Lists.partition(objs, batchSize);
        for (List<T> list : partitions) {
            this.baseMapper.insertBatch(list);
        }
        return true;
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, LENGTH);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        List<T> objs = Lists.newArrayList(entityList);
        List<List<T>> partitions = Lists.partition(objs, batchSize);
        for (List<T> list : partitions) {
            this.baseMapper.updateBatch(list);
        }
        return true;
    }

}

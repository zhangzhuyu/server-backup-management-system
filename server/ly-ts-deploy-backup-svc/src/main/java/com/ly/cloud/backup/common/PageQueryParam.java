package com.ly.cloud.backup.common;

/**
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2022-03-29
 * @version: 1.0
 */
public class PageQueryParam<T> {
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private T param;

    public PageQueryParam() {
    }

    public Integer getPageNum() {
        return this.pageNum;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public T getParam() {
        return this.param;
    }

    public void setPageNum(Integer PageNum) {
        this.pageNum = PageNum;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setParam(T param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "PageQueryParam(PageNum=" + this.getPageNum() + ", pageSize=" + this.getPageSize() + ", param=" + this.getParam() + ")";
    }
}

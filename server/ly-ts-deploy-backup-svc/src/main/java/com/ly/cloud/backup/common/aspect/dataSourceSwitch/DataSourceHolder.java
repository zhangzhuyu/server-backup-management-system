package com.ly.cloud.backup.common.aspect.dataSourceSwitch;

public final class DataSourceHolder {
    private static ThreadLocal<DataSourceBeanBuilder> threadLocal=new ThreadLocal<DataSourceBeanBuilder>(){
        @Override
        protected DataSourceBeanBuilder initialValue() {
            return null;
        }
    };

    public static DataSourceBeanBuilder getDataSource(){
        return threadLocal.get();
    }

    public static void setDataSource(DataSourceBeanBuilder dataSourceBeanBuilder){
        threadLocal.set(dataSourceBeanBuilder);
    }


    public static void clearDataSource(){
        threadLocal.remove();
    }
}

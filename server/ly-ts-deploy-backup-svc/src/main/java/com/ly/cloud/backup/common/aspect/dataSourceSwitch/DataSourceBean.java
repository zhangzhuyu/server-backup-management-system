package com.ly.cloud.backup.common.aspect.dataSourceSwitch;

import lombok.Getter;


@Getter
final class DataSourceBean {
    private final String beanName;
    private final String driverClassName;
    private final String url;
    private final String username;
    private final String password;
    private final String validationQuery;
    private final boolean testOnBorrow;

    private final String dataSourceType;


    public DataSourceBean(DataSourceBeanBuilder beanBuilder){
        this.beanName=beanBuilder.getBeanName();
        this.driverClassName=beanBuilder.getDriverClassName();
        this.url=beanBuilder.getUrl();
        this.password=beanBuilder.getPassword();
        this.testOnBorrow=beanBuilder.isTestOnBorrow();
        this.username=beanBuilder.getUsername();
        this.validationQuery=beanBuilder.getValidationQuery();

        this.dataSourceType=beanBuilder.getDataSourceType();
    }




}

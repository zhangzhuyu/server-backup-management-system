package com.ly.cloud.backup.common.aspect.dataSourceSwitch;


import com.google.common.base.Objects;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class DataSourceBeanBuilder {

    private String url;
    private String driverClassName;
    private final String beanName;
    private String username;
    private String password;
    private String validationQuery;
    private boolean testOnBorrow = true;
    private String dataSourceType;


    public DataSourceBeanBuilder(String beanName) {
        this.beanName = beanName;
    }

    public static DataSourceBeanBuilder BuildDataSourceBeanBuilder(MetaDataSource metaDataSource, String desSaltUtil) {

   /*     if(desSaltUtil != null){
            metaDataSource.setSjkmm(desSaltUtil.decrypt( metaDataSource.getSjkmm()));

        }

        return new DataSourceBeanBuilder( metaDataSource.getSjylx(),
                metaDataSource.getSjyurl(),
                metaDataSource.getSjyqd(),
                metaDataSource.getSjybh(),
                metaDataSource.getSjkyh(),
                metaDataSource.getSjkmm());*/
        return null;

    }

    public static DataSourceBeanBuilder BuildDataSourceBeanBuilder(MetaDataSource metaDataSource) {
        return BuildDataSourceBeanBuilder(metaDataSource,null);
    }





    public DataSourceBeanBuilder(String sjklx, String url, String driverClassName, String beanName,
                                 String username, String password) {
        if (sjklx.toLowerCase().contains("hsqldb")) {
            this.validationQuery = "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
        } else if (sjklx.toLowerCase().contains("oracle")) {
            this.validationQuery = "select 1 from dual";
        } else if (sjklx.toLowerCase().contains("db2")) {
            this.validationQuery = "select 1 from sysibm.sysdummy1";
        } else if (sjklx.toLowerCase().contains("mysql")) {
            this.validationQuery = "select 1";
        } else if (sjklx.toLowerCase().contains("sqlServer")) {
            this.validationQuery = "select 1";
        } else if (sjklx.toLowerCase().contains("postgresql")) {
            this.validationQuery = "select version()";
        } else if (sjklx.toLowerCase().contains("ingres")) {
            this.validationQuery = "select 1";
        } else if (sjklx.toLowerCase().contains("derby")) {
            this.validationQuery = "values 1";
        } else if (sjklx.toLowerCase().contains("h2")) {
            this.validationQuery = "select 1";
        } else {
            this.validationQuery = "";
        }
        this.url = url;
        this.driverClassName = driverClassName;
        this.beanName = beanName;
        this.username = username;
        this.password = password;

        this.dataSourceType = sjklx;
    }

    /**
     * 自动生成beanName的构造方法
     * （业务动态数据源通过该方法生成beanName，其他系统固定的数据源手动维护beanName）
     *
     * @param sjklx
     * @param url
     * @param driverClassName
     * @param username
     * @param password
     */
    public DataSourceBeanBuilder(String sjklx, String url, String driverClassName, String username, String password) {
        this.url = url;
        this.driverClassName = driverClassName;
        this.username = username;
        this.password = password;
        this.beanName = generateBeanName();
        if (sjklx.toLowerCase().contains("hsqldb")) {
            this.validationQuery = "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
        } else if (sjklx.toLowerCase().contains("oracle")) {
            this.validationQuery = "select 1 from dual";
        } else if (sjklx.toLowerCase().contains("db2")) {
            this.validationQuery = "select 1 from sysibm.sysdummy1";
        } else if (sjklx.toLowerCase().contains("mysql")) {
            this.validationQuery = "select 1";
        } else if (sjklx.toLowerCase().contains("sqlserver")) {
            this.validationQuery = "select 1";
        } else if (sjklx.toLowerCase().contains("postgresql")) {
            this.validationQuery = "select version()";
        } else if (sjklx.toLowerCase().contains("ingres")) {
            this.validationQuery = "select 1";
        } else if (sjklx.toLowerCase().contains("derby")) {
            this.validationQuery = "values 1";
        } else if (sjklx.toLowerCase().contains("h2")) {
            this.validationQuery = "select 1";
        } else {
            this.validationQuery = "";
        }

        this.dataSourceType = sjklx;
    }

    /**
     * 生成beanName（业务动态数据源通过该方法生成beanName，其他系统固定的数据源手动维护beanName）
     * （根据URL, driverClassName, username, password生成，该4个属性都一致，在切换数据源是判断为同一数据源）
     *
     * @return
     */
    private String generateBeanName() {
        return String.valueOf(Objects.hashCode(url, driverClassName, username, password));
    }

    public DataSourceBeanBuilder testOnBorrow(boolean value) {
        this.testOnBorrow = value;
        return this;
    }


}

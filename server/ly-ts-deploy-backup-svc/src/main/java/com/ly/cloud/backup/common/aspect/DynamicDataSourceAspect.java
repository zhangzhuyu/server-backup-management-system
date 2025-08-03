package com.ly.cloud.backup.common.aspect;


import com.ly.cloud.backup.common.annotation.DataSourceAnno;
import com.ly.cloud.backup.common.aspect.dataSourceSwitch.CustomerException;
import com.ly.cloud.backup.common.aspect.dataSourceSwitch.DataSourceBeanBuilder;
import com.ly.cloud.backup.common.aspect.dataSourceSwitch.DataSourceHolder;
import com.ly.cloud.backup.common.aspect.dataSourceSwitch.DynamicDataSource;
import com.ly.cloud.backup.service.DatabaseService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author SYC
 * @Date: 2022/3/8 10:54
 * @Description 数据源切换
 */
@Aspect
@Component
public class DynamicDataSourceAspect {

    @Autowired
    private DatabaseService databaseService;

    @Pointcut("@within(com.ly.cloud.backup.common.annotation.DataSourceAnno) ")
    public void aspect() {

    }


    @Autowired
    private DynamicDataSource dynamicDataSource;

    public static Map<String, DataSourceBeanBuilder> dataSourceBeanBuilderMap = new ConcurrentHashMap<>();

    public  void putDataSourceBeanBuilderMap(Map<String, DataSourceBeanBuilder> map) {
        dataSourceBeanBuilderMap.clear();
        dynamicDataSource.deleteDataSource(new ArrayList<>(map.keySet()));
        dataSourceBeanBuilderMap.putAll(map);
    }

/*    static {
        DataSourceBeanBuilder standardBeanBuilder = new DataSourceBeanBuilder("oracle", "jdbc:oracle:thin:@//192.168.35.151:1521/ORCL",
                "oracle.jdbc.driver.OracleDriver", "BZK", "LY_ISMP1", "LY_ISMP1");
        dataSourceBeanBuilderMap.put(standardBeanBuilder.getBeanName(), standardBeanBuilder);
    }*/


    @Around("aspect()")
    public Object interceptor(ProceedingJoinPoint invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Object target = invocation.getTarget();
        Class<?> aClass = target.getClass();
        Class<?>[] interfaces = aClass.getInterfaces();


        for (Class<?> cls : interfaces) {
            DataSourceAnno annotation = cls.getAnnotation(DataSourceAnno.class);
            if (annotation != null) {
                String beanName = annotation.beanName();
                DataSourceBeanBuilder dataSourceBeanBuilder = null;

                if (dynamicDataSource.isContainDataSouce(beanName)) {
                    dataSourceBeanBuilder = new DataSourceBeanBuilder(beanName);
                } else {
                    dataSourceBeanBuilder = dataSourceBeanBuilderMap.get(beanName);
                }

                if (dataSourceBeanBuilder == null) {
                    throw new CustomerException(beanName + "数据源不存在，请设置好重启系统");
                }
                //给当前线程设置数据源配置
                DataSourceHolder.setDataSource(dataSourceBeanBuilder);
                DataSourceBeanBuilder dataSource = DataSourceHolder.getDataSource();
                System.out.println("给当前线程设置数据源配置-dataSource:"+dataSource);
                break;
            }
        }

        try {
            return invocation.proceed(args);//环绕执行注解方法

        } finally {
            DataSourceHolder.clearDataSource();
        }

    }


}


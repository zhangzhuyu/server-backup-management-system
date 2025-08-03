package com.ly.cloud;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication()
//@SpringBootApplication(scanBasePackages = {"com.ly.cloud"})
@MapperScan({"com.ly.cloud.backup.mapper","com.ly.cloud.auth.mapper","com.ly.cloud.quartz.mapper","com.ly.cloud.license.*.mapper"})
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableAsync
public class BackupClientApplication {

    //@Autowired
    //private static TaierService taierService;

    public static void main(String[] args) {

        SpringApplication.run(BackupClientApplication.class, args);
        //ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        //taierService = applicationContext.getBean(TaierService.class);
    }
}


































package com.ly.cloud.backup.common.handler.batchandle;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author admin
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public CustomSqlInjector mySqlInjector() {
        return new CustomSqlInjector();
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
 
package com.ly.cloud.backup.config;

import com.ly.cloud.quartz.util.JSchConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author SYC
 * @Date: 2022/3/8 16:03
 * @Description 异步调用线程池
 */
//@EnableAsync
@Configuration
public class ThreadPoolConfig {
    private static Logger logger = LoggerFactory.getLogger(ThreadPoolConfig.class);
    @Bean(name = "threadPoolConf")
    public Executor threadPoolExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        核心线程数
        executor.setCorePoolSize(5);
//        最大线程数
        executor.setMaxPoolSize(10);
//        等待队列大小
        executor.setQueueCapacity(1024);
//        允许空闲时间
        executor.setKeepAliveSeconds(10);
//        线程名称前缀
        executor.setThreadNamePrefix("threadPool-");
//        调用线程处理任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        初始化
        executor.initialize();
        return executor;
    }

    @Bean
    public JSchConnectionPool connectionPool(){
        return new JSchConnectionPool(3 * 60 * 1000,2,10);
    }

}

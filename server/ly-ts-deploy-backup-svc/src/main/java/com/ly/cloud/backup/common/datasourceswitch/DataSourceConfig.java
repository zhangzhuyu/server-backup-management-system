package com.ly.cloud.backup.common.datasourceswitch;

import com.alibaba.druid.pool.DruidDataSource;
import com.ly.cloud.backup.common.aspect.dataSourceSwitch.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Class Name: DataSourceConfig Description: 数据源配置类
 *
 * @author: lizhenwei
 * @version: 1.0
 */
@Configuration
public class DataSourceConfig {



	@Autowired
	private Environment env;


	public static final String DEFAULT_DATASOURCE_NAME = "defaultDataSource";



	@Bean(name = "dynamicDataSource")
	public DynamicDataSource dynamicDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(env.getProperty("spring.datasource.url"));
		dataSource.setUsername(env.getProperty("spring.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.datasource.password"));
		dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));


		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put(DEFAULT_DATASOURCE_NAME, dataSource);
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		dynamicDataSource.setTargetDataSources(targetDataSources);
		dynamicDataSource.setDefaultTargetDataSource(dataSource);
		return dynamicDataSource;
	}


	@Bean
	public PlatformTransactionManager txManager(DynamicDataSource dynamicDataSource) {
		return new DataSourceTransactionManager(dynamicDataSource);
	}




}
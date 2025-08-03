package com.ly.cloud.backup.common.aspect.dataSourceSwitch;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DynamicDataSource extends AbstractRoutingDataSource implements ApplicationContextAware {

	private static final String DATA_SOURCES_NAME = "targetDataSources";
	private ApplicationContext applicationContext;

	@Override
	protected Object determineCurrentLookupKey() {
		DataSourceBeanBuilder dataSourceBeanBuilder = DataSourceHolder.getDataSource();
		if (dataSourceBeanBuilder == null) {
			return null;
		}
		DataSourceBean dataSourceBean = new DataSourceBean(dataSourceBeanBuilder);
		try {
			Map<Object, Object> map = getTargetDataSources();
			synchronized (map) {
				if (!map.containsKey(dataSourceBean.getBeanName())) {
					map.put(dataSourceBean.getBeanName(), createDataSource(dataSourceBean));
					super.afterPropertiesSet();// 通知spring有bean更新
				}
			}
			return dataSourceBean.getBeanName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSourceBean;
	}

	private Object createDataSource(DataSourceBean dataSourceBean) throws IllegalAccessException {
		// 在spring容器中创建并且声明bean
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(DruidDataSource.class);
		// 将dataSourceBean中的属性值赋给目标bean
		Map<String, Object> properties = getPropertyKeyValues(DataSourceBean.class, dataSourceBean);
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			beanDefinitionBuilder.addPropertyValue((String) entry.getKey(), entry.getValue());
		}
		beanFactory.registerBeanDefinition(dataSourceBean.getBeanName(), beanDefinitionBuilder.getBeanDefinition());
		DruidDataSource druidDataSource = (DruidDataSource) applicationContext.getBean(dataSourceBean.getBeanName());
		//重连数据源，60秒尝试一次
		druidDataSource.setTimeBetweenConnectErrorMillis(60000);
		//最多尝试5次
		druidDataSource.setConnectionErrorRetryAttempts(5);
		//尝试失败之后终止
		druidDataSource.setBreakAfterAcquireFailure(true);
		//数据源初始化出错的时候要抛异常
//		druidDataSource.setInitExceptionThrow(true);

		//获取链接之前先校验链接 mysql 默认300 断开链接，而连接池还维护此链接,造成打开一个已被mysql拒绝的链接而报错
		druidDataSource.setTestOnBorrow(true);

		return druidDataSource;
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> getTargetDataSources() throws NoSuchFieldException, IllegalAccessException {
//		 final String DATA_SOURCES_NAME = "targetDataSources";
		Field field = AbstractRoutingDataSource.class.getDeclaredField(DATA_SOURCES_NAME);
		field.setAccessible(true);
		return (Map<Object, Object>) field.get(this);
	}


	/**
	 * 判断动态数据源中是否包含了这个beanName
	 * @param beanName
	 * @return
	 * @throws Exception
	 */
	public Boolean isContainDataSouce(String beanName) throws Exception {
		Map<Object, Object> targetDataSources = getTargetDataSources();
		if(targetDataSources != null && targetDataSources.get(beanName)!=null){
			return true;
		}else{
			return false;
		}
	}



	private <T> Map<String, Object> getPropertyKeyValues(Class<T> clazz, Object object) throws IllegalAccessException {
		Field[] fields = clazz.getDeclaredFields();
		Map<String, Object> result = new HashMap<>();
		for (Field field : fields) {
			if ("dataSourceType".equals(field.getName())){
				continue;
			}
			field.setAccessible(true);
			result.put(field.getName(), field.get(object));
		}
		result.remove("beanName");
		return result;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;


	}


	/**
	 * 动态清空dataSource
	 */
	public void deleteDataSource(List<String> beanNamelist){
		try {
			ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
			DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
			//先删除dynamicDataSource的dataSource
			Map<Object, Object> targetDataSources = getTargetDataSources();
			for (String key : beanNamelist) {
				if(targetDataSources.containsKey(key)){
					targetDataSources.remove(key);

				}
				if(beanFactory.containsBeanDefinition(key)){
					beanFactory.removeBeanDefinition(key);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}









}
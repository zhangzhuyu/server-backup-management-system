package com.ly.cloud.backup.common.annotation;


import com.ly.cloud.backup.common.constant.DynamicVersion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author SYC
 * @Date: 2022/3/8 10:54
 * @Description 数据源切换
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface DataSourceAnno {

     String beanName() default "";

    DynamicVersion version() default DynamicVersion.VERSION_1;

}

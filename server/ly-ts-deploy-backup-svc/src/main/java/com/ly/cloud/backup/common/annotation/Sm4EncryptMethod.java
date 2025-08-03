package com.ly.cloud.backup.common.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * @author SYC
 * @Date: 2022/11/2 14:27
 * @Description 需要使用sm4加密的方法
 */
@Documented
@Target({ElementType.METHOD}) //打在方法上
@Retention(RetentionPolicy.RUNTIME)
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface Sm4EncryptMethod {
}

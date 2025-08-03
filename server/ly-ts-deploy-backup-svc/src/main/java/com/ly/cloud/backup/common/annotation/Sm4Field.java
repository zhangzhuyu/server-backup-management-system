package com.ly.cloud.backup.common.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * @author SYC
 * @Date: 2022/11/2 14:25
 * @Description 标记需要sm4解密的字段
 */
@Documented
@Target({ElementType.FIELD}) //打在实体(po,dto,vo)上面
@Retention(RetentionPolicy.RUNTIME)
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface Sm4Field {
}

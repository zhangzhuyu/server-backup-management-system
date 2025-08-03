package com.ly.cloud.backup.common.annotation;

import java.lang.annotation.*;

/**
 * 钉钉消息自定义注解，作用于方法（静态、非静态）上，添加该注解时必须加上【ReceivedMessage receivedMessage】参数，不然无效
 * @author jiangzhongxin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DingSendMessage {

    /**
     * 消息关键字，默认为空字符
     * @return 消息关键字
     */
    public String keyword() default "";

}

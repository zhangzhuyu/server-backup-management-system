package com.ly.cloud.backup.common.aspect;

import com.ly.cloud.backup.util.Sm4Util;
import com.ly.cloud.backup.common.annotation.Sm4Field;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author SYC
 * @Date: 2022/11/2 14:29
 * @Description sm4解密的切面
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Aspect
@Component
public class Sm4DecryptAspect {
    /**
     * 选择指定的解密注解切入点
     */
    @Pointcut("@annotation(com.ly.cloud.backup.common.annotation.Sm4DecryptMethod)")
    public void sm4DecryptPointCut(){}

    /**
     * 选择指定的加密注解切入点
     */
    @Pointcut("@annotation(com.ly.cloud.backup.common.annotation.Sm4EncryptMethod)")
    public void sm4EncryptPointCut(){}

    /**
     *  环绕的解密数据处理
     * @param joinPoint
     * @return
     */
    @Around("sm4DecryptPointCut()")
    public Object sm4DecryptAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object requestObj = args[0];
        sm4DecryptMethod(requestObj);
        Object proceed = joinPoint.proceed();
        return proceed;
    }

    /**
     *  环绕的加密数据处理
     * @param joinPoint
     * @return
     */
    @Around("sm4EncryptPointCut()")
    public Object sm4EncryptAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object requestObj = args[0];
        sm4EncryptMethod(requestObj);
        Object proceed = joinPoint.proceed();
        return proceed;
    }

    /**
     * sm4解密的处理方法
     * @param responseObj
     * @return
     * @throws Exception
     */
    private Object sm4DecryptMethod(Object responseObj) throws Exception {
        if (Objects.isNull(responseObj)){
            return null;
        }
        Field[] fields = responseObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            //获取被@Sm4Field标记的字段属性
            boolean annotationPresent = field.isAnnotationPresent(Sm4Field.class);
            if (annotationPresent){
                field.setAccessible(true);
                //获取需要解密的字段
                String encryptValue = field.get(responseObj).toString();
                String decryptValue = Sm4Util.sm4EcbDecrypt(encryptValue);
                field.set(responseObj,decryptValue);
            }
        }
     return responseObj;
    }

    /**
     * sm4加密的处理方法
     * @param responseObj
     * @return
     * @throws Exception
     */
    private Object sm4EncryptMethod(Object responseObj) throws Exception {
        if (Objects.isNull(responseObj)){
            return null;
        }
        Field[] fields = responseObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            //获取被@Sm4Field标记的字段属性
            boolean annotationPresent = field.isAnnotationPresent(Sm4Field.class);
            if (annotationPresent){
                field.setAccessible(true);
                //获取需要加密的字段
                String encryptValue = field.get(responseObj).toString();
                String decryptValue = Sm4Util.sm4EcbEncrypt(encryptValue);
                field.set(responseObj,decryptValue);
            }
        }
        return responseObj;
    }


}

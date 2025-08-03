package com.ly.cloud.backup.common.handler;

import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.common.enums.CommonEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class Name: SysExceptionHandler Description:
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年03月10日 13:58
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@ControllerAdvice
@Component
public class MyExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    /**
     * 入参校验
     *
     * @param exception : 异常
     * @return WebResponse<String>
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WebResponse<String> handle(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        Map<String, String> map = new HashMap<>(fieldErrors.size());
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String defaultMessage = fieldError.getDefaultMessage();
            if (map.containsKey(field)) {
                defaultMessage = map.get(field) + "、" + defaultMessage;
            }
            map.put(field, defaultMessage);
        }
        Set<Map.Entry<String, String>> entries = map.entrySet();
        String message = entries.stream()
                .map(r -> "'" + r.getKey() + "':" + r.getValue())
                .collect(Collectors.joining(" $ "));
        logger.error(message, exception);
        return new WebResponse<String>().failure(CommonEnums.PARAM_ERROR.getCode(), message);
    }

//    /**
//     * 全局异常捕捉处理
//     * @param ex
//     * @return
//     */
//    @ResponseBody
//    @ExceptionHandler(value = Exception.class)
//    public WebResponse<String> errorHandler(Exception ex) {
//        logger.error(ex.getMessage(),ex);
//        return new WebResponse<String>().failure(CommonEnums.SYSTEM_ERROR.getCode(), CommonEnums.SYSTEM_ERROR.getValue());
//    }

}

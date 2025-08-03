package com.ly.cloud.auth.commonException;

/**
 * 自定义的 验证码错误类
 *
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2022-10-08
 * @version: 1.0
 */
public class KaptchaException extends RuntimeException {

    private int code;

    public KaptchaException() {
    }

    public KaptchaException(String message) {
        super(message);
    }

    public KaptchaException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}

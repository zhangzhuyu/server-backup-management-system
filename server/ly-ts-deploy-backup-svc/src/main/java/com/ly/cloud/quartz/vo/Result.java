package com.ly.cloud.quartz.vo;


import com.ly.cloud.quartz.constants.CommonConstant;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {

    private String returnCode;

    private String returnMsg;

    private Object data;

    public static Result status(String code, String message) {
        Result Result = new Result();
        Result.setReturnCode(code);
        Result.setReturnMsg(message);
        return Result;
    }

    public static Result OK(Object obj) {
        Result Result = new Result();
        Result.setReturnCode(CommonConstant.SUCC);
        Result.setData(obj);
        return Result;
    }

    public static Result OK() {
        Result Result = new Result();
        Result.setReturnCode(CommonConstant.SUCC);
        Result.setData("");
        return Result;
    }

    public static Result FAIL(String msg) {
        Result Result = new Result();
        Result.setReturnCode(CommonConstant.FAIL);
        Result.setReturnMsg(msg);
        return Result;
    }


}

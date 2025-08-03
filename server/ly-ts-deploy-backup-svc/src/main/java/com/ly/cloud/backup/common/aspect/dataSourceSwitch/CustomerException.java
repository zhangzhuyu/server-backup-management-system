package com.ly.cloud.backup.common.aspect.dataSourceSwitch;

import lombok.Data;

/**
 * @author SYC
 * @Date: 2022/3/8 10:54
 * @Description 数据源切换
 */
@Data
public class CustomerException extends RuntimeException {

    private ResultCode resultCode;


   public CustomerException(ResultCode resultCode){
       super(resultCode.getText());
       this.resultCode = resultCode;
   }

    public CustomerException(String message){
        super(message);
        resultCode = ResultCode.INTERNAL_SERVER_ERROR;
    }



}

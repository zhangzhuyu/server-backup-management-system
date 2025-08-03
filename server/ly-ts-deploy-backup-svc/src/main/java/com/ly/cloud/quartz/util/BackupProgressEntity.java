package com.ly.cloud.quartz.util;

import lombok.Data;

/**
 * @author SYC
 */
@Data
public class BackupProgressEntity {


    private StringBuilder currState=new StringBuilder();

    public  void update(String c){
        currState.append(c);
        currState.append("<br />");
    }

}

package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/*
字段都取自于 ly_sm_system_setup
*/
@Data
public class SystemInfoVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    //页脚
    private String footer;

    //平台名称
    private String platformName;

    //logo图标
    private String logo;

//    // 项目地名称
//    private String schoolName;

}

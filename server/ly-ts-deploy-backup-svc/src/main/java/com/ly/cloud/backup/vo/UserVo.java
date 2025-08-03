package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {
    private static final long serialVersionUID = -5157236904709021782L;

    /**
     * 用户ID
     */
    private String id = "";

    /**
     * 用户昵称
     */
    private String name = "";
}

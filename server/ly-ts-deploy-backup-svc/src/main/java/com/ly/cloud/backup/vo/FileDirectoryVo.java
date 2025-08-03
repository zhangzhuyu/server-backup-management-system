package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2022-03-09
 * @version: 1.0
 */
@Data
public class FileDirectoryVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -8462738643889643715L;

    /**
     * 自动增加id
     */
    private int id;

    /**
     * 文件名/文件夹名
     */
    private String fileName;

    /**
     * 权限
     */
    private String permission;

    /**
     * 所有者
     */
    private String possessor;

    /**
     * 大小
     */
    private String fileSize;

    /**
     * 修改时间
     */
    private String modifyTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 文件类型 （ 1:文件，2：文件夹,3: 链接文件,4: 管理文件,5: 块设备文件,6: 字符设备文件,7: 套接字文件）
     */
    private String fileType;

}

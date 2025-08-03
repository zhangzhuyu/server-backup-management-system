package com.ly.cloud.backup.vo;

import lombok.Data;

import java.util.List;


/**
 * 上传初始化参数
 */
@Data
public class UploadInitVo {
    /**
     * uploadId
     */
    private String uploadId;
    /**
     * 上传地址
     */
    private List<String> uploadUrl;
    /**
     * 是否完成
     */
    private Boolean finished;
}

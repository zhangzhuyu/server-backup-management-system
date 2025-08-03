package com.ly.cloud.backup.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OssFileVo {
    /**
     * OSS 存储时文件路径
     */
    private String ossFilePath;
    /**
     * 原始文件名
     */
    private String originalFileName;
}

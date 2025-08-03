package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class HttpResponseDto implements Serializable {

    /**
     * 名称
     */
    @ApiModelProperty("字节数组")
    private byte[] bytes;

    /**
     * 父节点
     */
    @ApiModelProperty("http下载的文件名字")
    private String decodedUrl;
}

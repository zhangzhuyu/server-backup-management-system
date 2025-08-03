package com.ly.cloud.backup.config;


import io.minio.MinioClient;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2023-02-10
 * @version: 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    @ApiModelProperty(name = "url",value = "域名和端口",example = "http://101.35.199.23:9000")
    private String url;

    @ApiModelProperty(name = "access-key",value = "accessKey",example = "admin1")
    private String accessKey;

    @ApiModelProperty(name = "secret-key",value = "accessKey密码",example = "admin123")
    private String secretKey;

    @ApiModelProperty(name = "bucket-name",value = "默认存储桶",example = "maintenance-report")
    private String bucketName;

    @ApiModelProperty(name = "file-folder",value = "文件文件夹",example = "file")
    private String fileFolder;

    @ApiModelProperty(name = "preview-url",value = "外网访问服务地址",example = "")
    private String previewUrl;

    @ApiModelProperty(name = "img-size",value = "图片大小限制，单位：M",example = "1024")
    private Integer imgSize;

    @ApiModelProperty(name = "file-size",value = "文件大小限制，单位：M",example = "1024")
    private Integer fileSize;

    @Bean
    public MinioClient getMinioClient() {
        MinioClient minioClient = MinioClient.builder().endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        return minioClient;
    }

}

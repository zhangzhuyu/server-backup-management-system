package com.ly.cloud.backup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfoDto {

    private Long id; // 使用字符串ID，更通用
    private String name;
    private String ipv4;
}

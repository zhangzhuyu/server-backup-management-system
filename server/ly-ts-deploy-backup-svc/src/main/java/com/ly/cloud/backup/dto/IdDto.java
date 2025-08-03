package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class IdDto implements Serializable {
    /**
     * id
     */
    @ApiModelProperty("id")
    private String id;
}

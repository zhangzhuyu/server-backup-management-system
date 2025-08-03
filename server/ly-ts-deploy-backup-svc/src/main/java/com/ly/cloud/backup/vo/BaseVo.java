package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 分页的参数
 */
@Data
public class BaseVo implements Serializable {

    private static final long serialVersionUID = 8763700094891693074L;

    /**
     * 第几页
     */
    @ApiModelProperty("第几页")
    private long current;

    /**
     * 每页多少条
     */
    @ApiModelProperty("每页多少条")
    private long size;

    /**
     * 总数
     */
    @ApiModelProperty("总数")
    private long total;

    /**
     * 结果集
     */
    @ApiModelProperty("结果集")
    List<Map<String, Object>> records;


}

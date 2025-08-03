package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * taier新增实时采集任务
 *
 * @author zhangzhuyu
 */
@Data
public class AddOrUpdateTaskDto implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 任务名称
     */
    @ApiModelProperty("任务名称")
    private String name;

    /**
     * 任务类型
     */
    @ApiModelProperty("任务类型")
    private Integer taskType;

    /**
     * nodePid
     */
    @ApiModelProperty("nodePid")
    private Integer nodePid;

    /**
     * createModel
     */
    @ApiModelProperty("createModel")
    private Integer createModel;

    /**
     * 数据库用户
     */
    @ApiModelProperty("数据库用户")
    private String componentVersion;

    /**
     * 计算类型
     */
    @ApiModelProperty("计算类型")
    private Integer computerType;

    //以下为卡夫卡新增字段

    /**
     * 父类id
     */
    @ApiModelProperty("父类id")
    private Integer parentId;


    @ApiModelProperty("任务描述")
    private String taskDesc;

}












































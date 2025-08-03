package com.ly.cloud.backup.dto;

import com.ly.cloud.backup.vo.UserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Class Name: AlarmSubscriptionDto Description: 告警订阅dto
 *
 * @author: ljb
 * @date: 2022年03月21日 17:00
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Data
public class WarningSubscriptionDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -523353202186321959L;

    @ApiModelProperty("订阅关系id")
    private String id;

    @ApiModelProperty("订阅是否启用")
    private String enable;

//    @ApiModelProperty("订阅主机")
//    private List<String> hosts;
//
//    @ApiModelProperty("订阅数据库")
//    private  List<String> database;
//
//    @ApiModelProperty("订阅应用")
//    private List<String> application;
//
//    @ApiModelProperty("订阅服务")
//    private List<String> service;

    /**
     * 用户集合，即订阅人
     */
    @ApiModelProperty("用户集合，即订阅人")
    private List<UserVo> userList;

    @ApiModelProperty("订阅标题")
    private String subscriptionTitle;

    @ApiModelProperty("订阅资源")
    private List<String> subscribeList;

    @ApiModelProperty("指标对象（枚举，1-主机，2-数据库，3-服务，4-应用，5-中间件）")
    private String target;

    @ApiModelProperty("指标对象列表(用于多选)")
    private List<String> targetList;

    @ApiModelProperty("搜索内容")
    private String content;

    @ApiModelProperty("指标对象（枚举，1-主机，2-数据库，3-服务，4-应用，5-中间件）")
    private String subTypes;


}

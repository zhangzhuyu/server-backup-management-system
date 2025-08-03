package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 告警订阅关系信息视图类
 *
 * @author jiangzhongxin
 */
@Data
public class WarningSubscriptionListVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1741523968592784624L;

    /**
     * 告警订阅关系信息主键
     */
    private String id;

    /**
     * 开关
     */
    private String enable;

    /**
     * 订阅标题
     */
    private String subscriptionTitle;

    /**
     *主机集
     */
    private List<String> hostList;

    private String hostName;

    /**
     * 数据库集
     */
    private List<String> databaseList;

    private String databaseName;

    /**
     * 服务集
     */
    private String serviceName;

    private List<String> serviceList;

    /**
     * 应用集
     */
    private String applicationName;

    private List<String> applicationList;

    /**
     * 中间件集
     */
    private String middlewareName;

    private List<String> middlewareList;

//    /**
//     *  用户集
//     */
//    private List<String> userIds;
//
//    private String userName;

    private List<UserVo> userList = new ArrayList<>();
    /**
     * 告警描述
     */
    private String description;

    /**
     * 最近告警时间
     */
    private String latestAlarmTime;

    /**
     * 类型id集合
     */
    private String subContents;

    /**
     * 类型分类集合
     */
    private String subTypes;


    /**
     * 订阅内容
     */
    private String subscribeName;

    /**
     * 订阅内容Id集合
     */
    private List<String> subscribeList;

}

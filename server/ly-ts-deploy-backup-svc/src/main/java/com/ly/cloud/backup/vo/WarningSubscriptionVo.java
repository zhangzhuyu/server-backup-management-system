package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Class Name: AlarmSubscriptionDto Description: 告警订阅视图类
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年03月21日 17:00
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Data
public class WarningSubscriptionVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1771723594253913011L;

    /**
     * 订阅内容
     */
    private String content;

    /**
     * 用户集合，即订阅人
     */
    private List<String> userIds;

}

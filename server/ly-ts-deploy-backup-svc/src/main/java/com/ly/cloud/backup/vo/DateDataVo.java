package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Class Name: DateDataVo Description:
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年03月28日 14:36
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Data
public class DateDataVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9076883756966781025L;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * count
     */
    private BigDecimal count;

}

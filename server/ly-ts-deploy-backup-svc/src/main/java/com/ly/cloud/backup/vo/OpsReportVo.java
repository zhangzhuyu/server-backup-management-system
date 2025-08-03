package com.ly.cloud.backup.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 运维报告信息表
 * @author chenguoqing
 *
 */
@Data
public class OpsReportVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
    private Long id;
    
     /**
     * 时间周期(枚举)(0：周、1：月，2：年，3：自定义时间段)
     */
    private String timePeriod;
    
     /**
     * 报告名称
     */
    private String name;
    
     /**
     * 报告类型(枚举)【word pdf】
     */
    private String reportType;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件保存地址
     */
    private String fileAddress;

    /**
     * 报告下载次数
     */
    private String downloadCount;

    /**
     * 开始时间
     */
    private java.util.Date startTime;

    /**
     * 结束时间
     */
    private java.util.Date endTime;
    
     /**
     * 操作人ID
     */
    private String operatorId;
    
     /**
     * 操作时间
     */
    private java.util.Date operationTime;
    
     /**
     * 备注
     */
    private String remark;


    // 自增属性
    /**
     * 所属模板文件名称
     */
    private String templateName;

    /**
     * 文件所在服务器Id
     */
    private String fileServerId;

}

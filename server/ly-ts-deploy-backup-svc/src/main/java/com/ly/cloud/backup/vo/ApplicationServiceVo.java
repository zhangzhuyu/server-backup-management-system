package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 应用服务关系表
 * @author chenguoqing
 *
 */
@Data
public class ApplicationServiceVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
     /**
     * 应用ID
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long applicationId;
    
     /**
     * 服务ID
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long serviceId;
    
     /**
     * 部署方式（中间件）（枚举）
     */
    private String deploymentWay;
    
     /**
     * 所属公司
     */
    private String affiliatedCompany;
    
     /**
     * 操作时间
     */
    private java.util.Date operationTime;
    
     /**
     * 操作人ID
     */
    private String operatorId;
    
     /**
     * 备注
     */
    private String remark;

    //自增属性

    /**
     * 服务英文名称
     */
    private String englishName;

    /**
     * 服务中文名称
     */
    private String chineseName;

    /**
     * 服务健康状态（枚举）
     */
    private String healthStatus;

    /**
     * 容器ID数
     */
    private int containerSum;

    /**
     * 日志发生次数
     */
    private int logSum;

    /**
     * 日志chart图数据
     */
    private List<Map<String,String>> logsEChartList;

    /**
     * 错误日志发生次数
     */
    private int errorLogSum;

    /**
     * 错误日志chart图数据
     */
    private List<Map<String,String>> errorLogsEChartList;
}

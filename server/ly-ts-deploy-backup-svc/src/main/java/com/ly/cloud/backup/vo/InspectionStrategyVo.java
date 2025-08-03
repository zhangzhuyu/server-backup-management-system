package com.ly.cloud.backup.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 巡检策略信息表
 * @author chenguoqing
 *
 */
@Data
public class InspectionStrategyVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
    private Long id;
    
     /**
     * 任务名称
     */
    private String name;
    
     /**
     * 巡检类型（枚举）
     */
    private String inspection_type;
    
     /**
     * 巡检调度方式（枚举）
     */
    private String scheduling_mode;
    
     /**
     * 巡检调度周期（枚举）【每月、每周、每天】
     */
    private String scheduling_period;
    
     /**
     * 巡检调度执行时间
     */
    private java.util.Date execution_time;
    
     /**
     * 巡检调度间隔时间
     */
    private java.util.Date interval_time;
    
     /**
     * 首次巡检是否立即执行（枚举）
     */
    private String whether_immediate;
    
     /**
     * 推送邮箱
     */
    private String push_email;
    
     /**
     * 推送钉钉
     */
    private String push_ding_talk;
    
     /**
     * 操作人ID
     */
    private String operator_id;
    
     /**
     * 操作时间
     */
    private java.util.Date operation_time;
    
     /**
     * 备注
     */
    private String remark;
    

}

package com.ly.cloud.backup.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 自愈策略信息表
 * @author chenguoqing
 *
 */
@Data
public class RecoveryStrategyVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
    private Long id;
    
     /**
     * 指标名称
     */
    private String name;
    
     /**
     * 自愈类型（枚举）
     */
    private String recovery_type;
    
     /**
     * 自愈对象（服务）
     */
    private String recovery_object;
    
     /**
     * 自愈条件（枚举）
     */
    private String recovery_condition;
    
     /**
     * 是否启用故障自愈（枚举）
     */
    private String whether_enable;
    
     /**
     * 自愈尝试次数
     */
    private String recovery_count;
    
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

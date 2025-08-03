package com.ly.cloud.backup.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 操作日志信息表
 * @author chenguoqing
 *
 */
@Data
public class OperationLogVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
    private Long id;
    
     /**
     * 标题
     */
    private String title;
    
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

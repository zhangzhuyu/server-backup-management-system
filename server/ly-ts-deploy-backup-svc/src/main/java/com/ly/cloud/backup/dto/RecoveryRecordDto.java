package com.ly.cloud.backup.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自愈记录信息表
 * @author chenguoqing
 *
 */
@Data
public class RecoveryRecordDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
     @ApiModelProperty("主键")
    private Long id;
    
     /**
     * 自愈指标ID（外键）
     */
     @ApiModelProperty("自愈指标ID（外键）")
    private String recovery_id;
    
     /**
     * 服务名称ID（外键）
     */
     @ApiModelProperty("服务名称ID（外键）")
    private String service_id;
    
     /**
     * 自愈开始时间
     */
     @ApiModelProperty("自愈开始时间")
    private java.util.Date start_time;
    
     /**
     * 自愈结束时间
     */
     @ApiModelProperty("自愈结束时间")
    private java.util.Date end_time;
    
     /**
     * 自愈尝试次数
     */
     @ApiModelProperty("自愈尝试次数")
    private Integer recovery_count;
    
     /**
     * 操作人ID
     */
     @ApiModelProperty("操作人ID")
    private String operator_id;
    
     /**
     * 操作时间
     */
     @ApiModelProperty("操作时间")
    private java.util.Date operation_time;
    
     /**
     * 备注
     */
     @ApiModelProperty("备注")
    private String remark;
    

}

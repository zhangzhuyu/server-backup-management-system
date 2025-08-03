package com.ly.cloud.backup.po;



import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 公司信息表：ly_sm_company
 * @author chenguoqing
 *
 */
@TableName("ly_sm_company")
@Data
public class CompanyPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
    @TableId("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
     /**
     * 名称
     */
    @TableField(value = "name")
    private String name;
    
     /**
     * 父节点
     */
     @TableField(value = "parent_node")
    private Long parentNode;
    
     /**
     * 序号
     */
    @TableField(value = "serial_number")
    private String serialNumber;
    
     /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;
    
     /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;
    
     /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;
    

}

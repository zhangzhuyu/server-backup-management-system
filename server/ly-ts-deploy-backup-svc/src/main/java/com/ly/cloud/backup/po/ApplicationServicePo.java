package com.ly.cloud.backup.po;



import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 应用服务关系表：ly_rm_application_service
 * @author chenguoqing
 *
 */
@TableName("ly_rm_application_service")
@Data
public class ApplicationServicePo implements Serializable {

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
     * 应用ID
     */
    @TableField("application_id")
    private Long applicationId;
    
     /**
     * 服务ID
     */
    @TableField("service_id")
    private Long serviceId;
    
     /**
     * 部署方式（中间件）（枚举）
     */
    @TableField("deployment_way")
    private String deploymentWay;

     /**
     * 所属公司
     */
    @TableField("affiliated_company")
    private String affiliatedCompany;
    
     /**
     * 操作时间
     */
    @TableField("operation_time")
    private java.util.Date operationTime;
    
     /**
     * 操作人ID
     */
    @TableField("operator_id")
    private String operatorId;
    
     /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    

}

package com.ly.cloud.backup.po;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 流量监控表：ly_rm_flow_monitoring
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_rm_flow_monitoring")
public class FlowMonitoringPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id")
    private Long id;

    /**
     * 应用id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "application_id")
    private Long applicationId;

    /**
     * NginxId
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "nginx_id")
    private Long nginxId;

    /**
     * Nginx反向代理地址
     */
    @TableField(value = "agent_address")
    private String agentAddress;

    /**
     * Nginx反向代理地址(location)
     */
    @TableField(value = "agent_address_name")
    private String agentAddressName;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 序号
     */
    @TableField(value = "serial_number")
    private String serialNumber;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}


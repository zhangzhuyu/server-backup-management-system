package com.ly.cloud.backup.po;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据库服务器关系表：ly_rm_database_server
 * @author SYC
 *
 */
@TableName("ly_rm_database_server")
@Data
public class DatabaseServerPo implements Serializable {

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
     * 数据库ID
     */
    @TableField("database_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long databaseId;
    
     /**
     * 服务器ID
     */
    @TableField("server_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long serverId;
    
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

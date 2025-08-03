package com.ly.cloud.backup.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公司信息表
 * @author chenguoqing
 *
 */
@Data
public class CompanyDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
     @ApiModelProperty("主键")
     @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
     /**
     * 名称
     */
     @ApiModelProperty("名称")
    private String name;
    
     /**
     * 父节点
     */
     @ApiModelProperty("父节点")
    private Long parentNode;
    
     /**
     * 序号
     */
     @ApiModelProperty("序号")
    private String serialNumber;
    
     /**
     * 操作人ID
     */
     @ApiModelProperty("操作人ID")
    private String operatorId;
    
     /**
     * 操作时间
     */
     @ApiModelProperty("操作时间")
    private java.util.Date operationTime;
    
     /**
     * 备注
     */
     @ApiModelProperty("备注")
    private String remark;

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;
}

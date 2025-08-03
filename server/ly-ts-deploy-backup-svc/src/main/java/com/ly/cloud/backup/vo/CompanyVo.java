package com.ly.cloud.backup.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 公司信息表
 * @author chenguoqing
 *
 */
@Data
public class CompanyVo implements Serializable {

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
     * 名称
     */
    private String name;
    
     /**
     * 父节点
     */
    private Long parentNode;
    
     /**
     * 序号
     */
    private String serialNumber;
    
     /**
     * 操作人ID
     */
    private String operatorId;
    
     /**
     * 操作时间
     */
    private java.util.Date operationTime;
    
     /**
     * 备注
     */
    private String remark;

    //自增属性
    private Integer buttonType;

    /**
     * 下一级 集合
     */
    List<CompanyVo> children;

    /**
     * 服务器信息
     */
    List<ServerVo> serverVos;


    /**
     * 防火墙状态
     */
    private String firewallState;

    /**
     * 前端展示控制
     */
    private Integer companyShow;
    private Integer switchChecked;
    private Integer ngnixShow;
    private Integer serverShow;

    /**
     * 搜索内容
     */
    private String content;
}

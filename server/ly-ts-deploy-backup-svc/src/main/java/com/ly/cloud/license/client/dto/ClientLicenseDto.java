package com.ly.cloud.license.client.dto;

import com.ly.cloud.license.client.po.ClientLicenseServerPo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * license
 * @author SYC
 * @date 20220720
 */
@Data
public class ClientLicenseDto implements Serializable{

    private static final long serialVersionUID = 8600137500316662317L;

    /**
     * 项目地名称
     */
    private String schoolName;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 有效期：1:1周、2:1个月 3:3个月 4:半年 5:1年 -1:永久（选择）
     */
    private int expiryDate;

    /**
     * 可被允许的IP地址
     */
    private List<String> ipAddress;

    /**
     * 可被允许的MAC地址
     */
    private List<String> macAddress;

    /**
     * 可被允许的CPU序列号
     */
    private List<String> cpuSerial;

    /**
     * 可被允许的主板序列号
     */
    private List<String> mainBoardSerial;

    /**
     * 描述
     */
    private String remark;

    /**
     * 审批状态(1:申请，2：已处理)
     */
    private int status;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private java.util.Date operationTime;

    /**
     * 服务器信息
     */
    private List<ClientLicenseServerPo> clientLicenseServerPos;

    /**
     * 平台版本号
     */
    private String systemVersion;

}

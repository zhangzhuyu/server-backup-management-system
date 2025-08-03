package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * system_license
 * @author zhangzhuyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("ly_sm_system_license_server")
@TableName("ly_sm_system_license_server")
public class LySmSystemLicenseServerPo implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835112L;
    /**
     * 主键
     */
    @ApiModelProperty("主键")
//    @TableId(value = "id", type = IdType.AUTO)
    @TableId("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String id;

    /**
     * 可被允许的IP地址
     */
    @ApiModelProperty(value = "可被允许的IP地址")
    @TableField(value = "ip_address",strategy = FieldStrategy.IGNORED)
    private String ipAddress;

    /**
     * 可被允许的MAC地址
     */
    /*//@TableField(value = "data_source_type")
    private String dataSourceType;*/

    @ApiModelProperty("可被允许的MAC地址")
    @TableField(value = "mac_address",strategy = FieldStrategy.IGNORED)
    private String macAddress;

    /**
     * 可被允许的CPU序列号
     */
    //@TableField(value = "backup_way")
    //private String backupWay;

    @ApiModelProperty("可被允许的CPU序列号")
    @TableField(value = "cpu_serial",strategy = FieldStrategy.IGNORED)
    private String cpuSerial;

    /**
     * 可被允许的主板序列号
     */
    @ApiModelProperty("可被允许的主板序列号")
    @TableField(value = "main_board_serial",strategy = FieldStrategy.IGNORED)
    private String mainBoardSerial;

    /**
     * 备份目标
     */
    @ApiModelProperty("操作人ID")
    @TableField(value = "operator_id",strategy = FieldStrategy.IGNORED)
    private String operatorId;

    /**
     * 数据库连接
     */
    @ApiModelProperty("操作时间")
    @TableField(value = "operation_time",strategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operationTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField(value = "remark",strategy = FieldStrategy.IGNORED)
    private String remark;

    /**
     * IP地址(ipv4)
     */
    @ApiModelProperty("IP地址(ipv4)")
    @TableField(value = "ipv4",strategy = FieldStrategy.IGNORED)
    private String ipv4;

    /**
     * 备份时间
     */
    @ApiModelProperty("端口")
    @TableField(value = "port",strategy = FieldStrategy.IGNORED)
    private String port;

    /**
     * 操作人ID
     */
    @ApiModelProperty("账号")
    @TableField(value = "user",strategy = FieldStrategy.IGNORED)
    private String user;


    @ApiModelProperty("密码")
    @TableField(value = "password",strategy = FieldStrategy.IGNORED)
    private String password;

    /**
     * cron表达式
     */
    @ApiModelProperty("测试状态")
    @TableField(value = "test_status",strategy = FieldStrategy.IGNORED)
    private String testStatus;

    /**
     * 所属部门编号
     */
    @TableField(value = "auth_dept_id")
    private String authDeptId;

    @TableField(exist = false)
    private List<String> authDeptIds;
}


































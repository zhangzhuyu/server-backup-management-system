package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * oracle仪表板概览
 * */
@Data
@ApiModel("oracle仪表板概览")
public class OraclePanelSummaryVo {
    @ApiModelProperty("IP地址")
    private String ip;
    @ApiModelProperty("状况--0正常，1-异常")
    private Integer okOrNot;
    @ApiModelProperty("当前连接数")
    private Integer currentConn;
    @ApiModelProperty("最大连接数")
    private Integer maxConn;
    @ApiModelProperty("数据库版本")
    private String version;
    @ApiModelProperty("端口")
    private Integer port;
    @ApiModelProperty("实例名称")
    private String instance;
    @ApiModelProperty("数据库状态")
    private String dbStatus;
    @ApiModelProperty("实例状态")
    private String instanceStatus;
    @ApiModelProperty("会话数")
    private Integer sessions;
    @ApiModelProperty("预警规则数")
    private Integer alerts;
    @ApiModelProperty("总预警数")
    private Integer totalAlerts;

    public OraclePanelSummaryVo convert(Map<String, Object> m, List<Map<String, Object>> res1, List<Map<String, Object>> res2){
        if (m!=null){
            String[] ipPort=((String) m.get("ip")).split(":");
            if(ipPort.length==2){
                this.setIp(ipPort[0]); this.setPort(Integer.valueOf(ipPort[1]));
            }

            String dbStatus0= (String) m.get("dbStatus");
            String instanceStatus0=(String) m.get("instanceStatus");
            if(dbStatus0.equals("ACTIVE")&&!instanceStatus0.equals("INACTIVE")){
                this.setOkOrNot(0);
            }else{
                this.setOkOrNot(1);
            }
            this.setDbStatus(dbStatus0);
            this.setInstanceStatus(instanceStatus0);
        }

        for(Map<String,Object> connMap:res1){
            if(connMap.get("connections")!=null&&!"null".equals(connMap.get("connections"))){
                this.setCurrentConn(Integer.parseInt((String) connMap.get("connections")));
                break;
            }
        }
        for(Map<String,Object> connMap:res1){
            if(connMap.get("maxConnections")!=null&&!"null".equals(connMap.get("maxConnections"))){
                this.setMaxConn(Integer.parseInt((String) connMap.get("maxConnections")));
                break;
            }
        }

        this.setVersion((String) m.get("version"));
        this.setInstance((String) m.get("instance"));

        for(Map<String,Object> sidMap:res2){
            if(sidMap.get("sessions")!=null&&!"null".equals(sidMap.get("sessions"))){
                this.setSessions(Integer.parseInt((String) sidMap.get("sessions")));
                break;
            }
        }
        return this;
    }
}

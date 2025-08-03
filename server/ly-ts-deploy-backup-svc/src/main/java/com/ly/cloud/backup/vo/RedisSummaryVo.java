package com.ly.cloud.backup.vo;

import com.ly.cloud.backup.util.base.ChartDataUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@ApiModel("redis监控面板概览")
@Data
public class RedisSummaryVo { //version,memory,clients,commandsProcessed,upTime
    @ApiModelProperty("版本")
    private String version;
    @ApiModelProperty("已用内存")
    private String memory;
    @ApiModelProperty("客户端")
    private String clients;
    @ApiModelProperty("已执行命令")
    private Object commandsProcessed;
    @ApiModelProperty("正常运行时间(天)")
    private String upTime;

    public RedisSummaryVo convert(List<Map<String, Object>> res) {
        RedisSummaryVo vo=new RedisSummaryVo();
        res.forEach(u->{
            for(Map.Entry<String,Object> entry:u.entrySet()){
                if(entry.getValue()==null||entry.getValue().equals("null"))continue;
                switch (entry.getKey()){
                    case "version":
                        if(vo.getVersion()==null){
                            vo.setVersion((String) entry.getValue());
                        }
                        break;
                    case "memory":
                        if(vo.getMemory()==null){
                            Double d=Double.parseDouble((String) entry.getValue())/1024/1024;
                            vo.setMemory((String) ChartDataUtil.getRound(String.valueOf(d)));
                        }
                        break;
                    case "clients":
                        if(vo.getClients()==null){
                            vo.setClients((String) entry.getValue());
                        }
                        break;
                    case "commandsProcessed":
                        if(vo.getCommandsProcessed()==null){
                            vo.setCommandsProcessed(entry.getValue()); //Integer.parseInt((String)
                        }
                        break;
                    case "upTime":
                        if(vo.getUpTime()==null){
                            try{
                                Long l=Long.parseLong((String) entry.getValue());
                                if(l!=null){
                                    Long day=l/(24*60*60);
                                    vo.setUpTime(String.valueOf(day));
                                }
                            }catch (Exception e){

                            }
                        }
                }
            }
        });
        return vo;
    }
}

package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiModel("redis监控页面--客户端  列表项")
@Data
public class RedisClientVo {
    @ApiModelProperty("")
    private String clientIP;
    @ApiModelProperty("")
    private String connectionTime;
    @ApiModelProperty("空闲")
    private String idle;
    @ApiModelProperty("标记")
    private String mark;
    @ApiModelProperty("当前库")
    private String currentDatabase;
    @ApiModelProperty("内存用量")
    private String memory;
    @ApiModelProperty("系统使用cpu")
    private String sysCPU;
    @ApiModelProperty("用户用cpu")
    private String userCPU;
    @ApiModelProperty("pid")
    private String pid;

    //service.address ip, redis.info.server.uptime uptime, process.pid pid,redis.info.memory.used.value memory,redis.info.cpu.used.sys sysCPU, redis.info.cpu.used.user userCPU

    public List<RedisClientVo> convert(List<Map<String, Object>> res) {
        List<RedisClientVo> list=new ArrayList<>();
        res.stream().filter(u->u.get("ip")!=null||!u.get("ip").equals(""));
        //去重
        Map<String, Integer> uniqueMap=new HashMap<>();
        for(Map<String, Object> u:res){
            if(uniqueMap.get(u.get("ip"))!=null) continue;
            uniqueMap.put((String) u.get("ip"),1);
            RedisClientVo vo=new RedisClientVo();
            for(Map.Entry<String,Object> entry:u.entrySet()){
                try{
                    switch (entry.getKey()){
                        case "ip":
                            vo.setClientIP((String) entry.getValue());
                            break;
                        case "uptime":
                            vo.setConnectionTime((String) entry.getValue());
                            break;
                        case "idle":
                            vo.setIdle((String) entry.getValue());
                            break;
                        case "mark":
                            vo.setMark((String) entry.getValue());
                            break;
                        case "memory":
                            vo.setMemory((String) entry.getValue());
                            break;
                        case "sysCPU":
                            vo.setSysCPU((String) entry.getValue());
                            break;
                        case "userCPU":
                            vo.setUserCPU((String) entry.getValue());
                            break;
                        case "pid":
                            vo.setPid((String) entry.getValue());
                            break;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            list.add(vo);
        }
        return list;
    }
}

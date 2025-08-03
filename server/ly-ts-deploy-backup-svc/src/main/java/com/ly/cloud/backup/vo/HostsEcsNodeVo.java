package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiModel("Mongodb Docker ECS 节点列表项")
@Data
public class HostsEcsNodeVo {
    @ApiModelProperty("服务ID/连接url")
    private String address;
    @ApiModelProperty("连接数")
    private Integer connections;
    @ApiModelProperty("架构")
    private String arch;
    @ApiModelProperty("驻留内存")
    private Double residentMemory;
    @ApiModelProperty("虚拟内存")
    private Double virtualMemory;

    public List<HostsEcsNodeVo> convert(List<Map<String, Object>> res) {
        List<HostsEcsNodeVo> list=new ArrayList<>();
        //把同一serviceIP的搞在一起
        Map<String,Integer> ips=new HashMap<>();
        res.forEach(u->{
            if(u.get("ip")!=null&&!u.get("ip").equals("")){
                if(ips.get(u.get("ip"))==null) {
                    ips.put((String) u.get("ip"),1);
                }
            }
        });
        for(Map.Entry<String, Integer> ipEntry:ips.entrySet()){
            HostsEcsNodeVo vo=new HostsEcsNodeVo();
            String ip=ipEntry.getKey();
            vo.setAddress(ip);
            for(Map<String, Object> u:res){
                if(!ip.equals(u.get("ip"))) continue;
                for(Map.Entry<String,Object> entry:u.entrySet()){
                    try{
                        switch (entry.getKey()){
                            case "conn":
                                if(vo.getConnections()==null &&entry.getValue()!=null&&!entry.getValue().equals("null"))
                                vo.setConnections(Integer.parseInt((String) entry.getValue()));
                                break;
                            case "arch":
                                if(vo.getArch()==null &&entry.getValue()!=null&&!entry.getValue().equals("null"))
                                vo.setArch((String) entry.getValue());
                                break;
                            case "resident":
                                if(vo.getResidentMemory()==null &&entry.getValue()!=null&&!entry.getValue().equals("null"))
                                vo.setResidentMemory(Double.parseDouble((String) entry.getValue()));
                                break;
                            case "virtual":
                                if(vo.getVirtualMemory()==null &&entry.getValue()!=null&&!entry.getValue().equals("null"))
                                vo.setVirtualMemory(Double.parseDouble((String) entry.getValue()));
                                break;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            list.add(vo);
        }

        if(list.size()==0){list.add(new HostsEcsNodeVo());}
        return list;
    }
}

package com.ly.cloud.backup.vo;

import com.ly.cloud.backup.util.base.ChartDataUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.cglib.beans.BeanMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiModel("DOCKER ECS节点")
@Data
public class DockerContainerVo {
    @ApiModelProperty("容器id")
    private String containerId;
    @ApiModelProperty("容器名称")
    private String containerName;
    @ApiModelProperty("cpu使用率")
    private Double cpuPct;
    @ApiModelProperty("内存使用率")
    private String memoryPct;
    @ApiModelProperty("内存使用量(MB)")
    private String memoryUsedMB;
    @ApiModelProperty("容器数量")
    private Integer containerNum;
    @ApiModelProperty("磁盘io")
    private Double diskio;

    public List<DockerContainerVo> convert(List<Map<String, Object>> res) {
        List<DockerContainerVo> list=new ArrayList<>();
        double totalCpu=0;double totalMemory=0; int totalNum=0;double totalDiskio=0;

        Map<String,Integer> uniqueMap=new HashMap<>();
        for(Map<String ,Object> m:res){
            if(uniqueMap.get((String)m.get("name"))!=null)continue;
            uniqueMap.put((String) m.get("name"),1);
            DockerContainerVo vo=new DockerContainerVo();
            for (Map.Entry<String, Object> entry : m.entrySet()) {
                if (entry.getValue() != null && !"null".equals(entry.getValue())) {
                    try{
                        switch (entry.getKey()) {
                            case "id":
                                vo.setContainerId((String) entry.getValue());
                                break;
                            case "name":
                                if(vo.getContainerName()==null)
                                vo.setContainerName((String) entry.getValue());
                                break;
                            case "cpuPct":
                                if(vo.getCpuPct()==null)
                                vo.setCpuPct( Double.parseDouble((String) ChartDataUtil.getRound(Double.parseDouble((String) entry.getValue()))));
                                if(entry.getValue()!=null&&!"null".equals(entry.getValue())){
                                    totalCpu +=Double.parseDouble((String) entry.getValue());
                                }
                                break;
                            case "memoryPct":
                                if(vo.getMemoryPct()==null){
                                    String val=(String) ChartDataUtil.getRound(Double.parseDouble((String) entry.getValue()));
                                    vo.setMemoryPct(val);
                                }
                                if(entry.getValue()!=null&&!"null".equals(entry.getValue())){
                                    totalMemory+=Double.parseDouble((String) entry.getValue());
                                }
                                break;
                            case "num":
                                if(vo.getContainerNum()==null)
                                vo.setContainerNum(Integer.parseInt((String) entry.getValue()));
                                if(entry.getValue()!=null&&!"null".equals(entry.getValue())){
                                    totalNum+=Integer.parseInt((String) entry.getValue());
                                }
                                break;
                            case "diskio":
                                if(vo.getDiskio()==null)
                                vo.setDiskio(Double.parseDouble((String) entry.getValue()));
                                if(entry.getValue()!=null&&!"null".equals(entry.getValue())){
                                    totalDiskio+=Double.parseDouble((String) entry.getValue());
                                }
                                break;
                            case "memoryTotal":
                                if(vo.getMemoryUsedMB()==null){
                                    String mb= ChartDataUtil.getRound(Double.parseDouble((String)entry.getValue())/1024/1024) +"MB";
                                    vo.setMemoryUsedMB(mb);
                                }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            list.add(vo);
        }

//        list.add(new DockerContainerVo());
        DockerContainerVo summary=new DockerContainerVo();
        summary.setContainerId("总量");
        summary.setCpuPct(totalCpu);
        summary.setMemoryPct(String.valueOf(totalMemory));
        summary.setContainerNum( totalNum);
        list.add(summary);
        return list;
    }

    public DockerContainerVo getSummary(List<DockerContainerVo> list) {
        if(list.size()==0) return this;
        this.setCpuPct(0d);
        this.setMemoryPct("");
        this.setDiskio(0d);
        this.setMemoryUsedMB("");
        this.setContainerNum(0);
        double memoryPct=0d; double memoryMB=0d;
        for(int i=0;i<list.size();i++){
            DockerContainerVo u=list.get(i);
            Double cpuPct=this.getCpuPct()!=null?this.getCpuPct():0d;
            cpuPct=u.getCpuPct()!=null?cpuPct+u.getCpuPct():cpuPct;
            this.setCpuPct(cpuPct);

            Double diskio=this.getDiskio()!=null?this.getDiskio():0d;
            diskio=u.getDiskio()!=null?u.getDiskio()+diskio:diskio;
            this.setDiskio(diskio);

            Integer containerNum=this.getContainerNum()!=null?this.getContainerNum():0;
            containerNum=u.getContainerNum()!=null?containerNum+u.getContainerNum():containerNum;
            this.setContainerNum(containerNum);

            memoryPct+=u.getMemoryPct()!=null?Double.parseDouble(u.getMemoryPct()):0d;
            if(u.getMemoryUsedMB()!=null){
                memoryMB+=Double.parseDouble(u.getMemoryUsedMB().replace("MB",""));
            }
        }
        this.setMemoryPct((String) ChartDataUtil.getRound(memoryPct/list.size()));
        this.setMemoryUsedMB(ChartDataUtil.getRound(memoryMB/list.size())+"MB");
        this.setCpuPct(Double.parseDouble((String) ChartDataUtil.getRound(this.getCpuPct()/list.size())));
        this.setDiskio(Double.parseDouble((String)ChartDataUtil.getRound(this.getDiskio()/ list.size())));
        return this;
    }

    public void copyFieldValue(DockerContainerVo vo, String u) {
        switch (u){
            case "diskio":
                this.setDiskio(vo.getDiskio());
                break;
            case "memoryPct":
                this.setMemoryPct(vo.getMemoryPct());
                break;
            case "memoryTotal":
                this.setMemoryUsedMB(vo.getMemoryUsedMB());
                break;
            case "num":
                this.setContainerNum(vo.getContainerNum());
                break;
        }
    }

    public Double get(String columnKey) {
        Map<String,Object>m=BeanMap.create(this);
        if(m.get(columnKey) instanceof String){
           return Double.parseDouble (((String)m.get(columnKey)).replaceAll("[GMKB]+",""));
        }
        return (Double) m.get(columnKey);
    }
}

package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@ApiModel("监控面板oracle sqlIO--字段列表\"sqlId\",\"sql_text\",\"child_number\",\"elapsed_time\",\"cpu_time\",\"disk_reads\",\"elapsed_rank\"")
@Data
public class PanelSqlIoVo {
    @ApiModelProperty("监控面板oracle sqlIO")
    private Integer sqlId;
    @ApiModelProperty("监控面板oracle sqlIO")
    private String sql_text;
    @ApiModelProperty("监控面板oracle sqlIO")
    private Integer child_number;
    @ApiModelProperty("监控面板oracle sqlIO")
    private Object elapsed_time;
    @ApiModelProperty("监控面板oracle sqlIO")
    private Object cpu_time;
    @ApiModelProperty("监控面板oracle sqlIO")
    private Object disk_reads;
    @ApiModelProperty("监控面板oracle sqlIO")
    private Integer elapsed_rank;

    public PanelSqlIoVo convert(Map<String, Object> u) {
        PanelSqlIoVo vo=new PanelSqlIoVo();
        for(Map.Entry<String,Object> entry:u.entrySet()){
            switch (entry.getKey()){
                case "sqlId" :
                    vo.setSqlId((Integer) entry.getValue());
                    break;
                case "sql_text" :
                    vo.setSql_text((String) entry.getValue());
                    break;
                case "child_number" :
                    vo.setChild_number((Integer) entry.getValue());
                    break;
                case "elapsed_time" :
                    vo.setElapsed_time(entry.getValue());
                    break;
                case "cpu_time" :
                    vo.setCpu_time(entry.getValue());
                    break;
                case "disk_reads" :
                    vo.setDisk_reads(entry.getValue());
                    break;
                case "elapsed_rank" :
                    vo.setElapsed_rank((Integer) entry.getValue());
                    break;
            }
        }
        return vo;
    }
}

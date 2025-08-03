package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@ApiModel("nginx监控面板概览指标")
@Data
public class SummaryNginxVo {
    @ApiModelProperty("异常日志")
    private Integer abnormalLog;
    @ApiModelProperty("日志总数")
    private Integer totalLog;
    @ApiModelProperty("当前连接数")
    private Integer currentConn;
    @ApiModelProperty("最大连接数")
    private Integer maxConn;

    public SummaryNginxVo convert(List<Map<String, Object>> res) {
        res.forEach(u->{
            for(Map.Entry<String,Object> entry:u.entrySet()){
                if(entry.getValue()==null||entry.getValue().equals("null"))continue;
                switch (entry.getKey()){
                    case "currentConn":
                        this.setCurrentConn(Integer.parseInt((String) entry.getValue()));
                        break;
                    case "maxConn":
                        this.setMaxConn(Integer.parseInt((String) entry.getValue()));
                        break;
                    case "abnormalLog":
                        this.setAbnormalLog(Integer.parseInt((String) entry.getValue()));
                        break;
                    case "totalLog":
                        this.setTotalLog(Integer.parseInt((String) entry.getValue()));
                        break;
                }
            }
        });
        return this;
    }
}

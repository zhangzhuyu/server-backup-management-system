package com.ly.cloud.backup.vo;


import com.ly.cloud.backup.util.base.ChartDataUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@ApiModel("oracle缓存缓冲区命中率/游标命中率/IO重新加载/锁定请求/固定请求")
@Data
public class OracleClockChartVo {
    @ApiModelProperty("oracle缓存缓冲区命中率")
    private String bufferedCacheHitRatio;
    @ApiModelProperty("游标命中率")
    private String cursorHitRation;
    @ApiModelProperty("IO重新加载")
    private String ioReload;
    @ApiModelProperty("锁定请求")
    private String lockedRequest;
    @ApiModelProperty("锁定请求")
    private String pinRequest;

    public OracleClockChartVo convert(List<Map<String, Object>> res) {
        for (Map<String, Object> m : res) {
            for (Map.Entry<String, Object> entry : m.entrySet()) {
                String key = entry.getKey();
                Double val=null;
                String roundVal="";
                try{
                    val=Double.parseDouble((String) entry.getValue());
                    roundVal= (String) ChartDataUtil.getRound(entry.getValue());
                    if(val==null){
                        val=(Double) entry.getValue();
                    }
                }catch (Exception e){

                }
                switch (key) {
                    case "bufferHit":
                        this.setBufferedCacheHitRatio(roundVal);
                        break;
                    case "cursorHit":
                        this.setCursorHitRation(roundVal);
                        break;
                    case "reload":
                        this.setIoReload(roundVal);
                        break;
                    case "lock":
                        this.setLockedRequest(roundVal);
                        break;
                    case "pin":
                        this.setPinRequest(roundVal);
                        break;
                }
            }
        }
        return this;
    }
}

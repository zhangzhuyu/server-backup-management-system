package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApiModel("Nginx 错误日志")
@Data
public class ErrorLogNginxVo {
    @ApiModelProperty("日志产生时间")
    private String timestamp;
    @ApiModelProperty("日志类型")
    private String errorType;
    @ApiModelProperty("日志内容")
    private String log;

    public List<ErrorLogNginxVo> convert(List<Map<String, Object>> res) {
        List<ErrorLogNginxVo> list=new ArrayList<>();
        res.forEach(u->{
            ErrorLogNginxVo vo=new ErrorLogNginxVo();
            for(Map.Entry<String,Object> entry:u.entrySet()){
                try{
                    switch (entry.getKey()){
                        case "@timestamp":
                            vo.setTimestamp((String) entry.getValue());
                            break;
                        case "errorType":
                            vo.setErrorType((String) entry.getValue());
                            break;
                        case "message":
                            if(entry.getValue()==null)continue;
                            vo.setLog((String) entry.getValue());
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            list.add(vo);
        });
        if(list.size()==0){
            list.add(new ErrorLogNginxVo());
        }
        return list;
    }
}

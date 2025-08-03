package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApiModel("Nginx 访问日志")
@Data
public class RequestLogNginxVo {
    @ApiModelProperty("日志时间")
    private String timestamp;
    @ApiModelProperty("url")
    private String urlOriginal;
    @ApiModelProperty("日志请求方式")
    private String httpRequestMethod;
    @ApiModelProperty("请求状态码")
    private Integer httpResponseStatusCode;
    @ApiModelProperty("请求响应body字节数")
    private Integer httpResponseBodyBytes;

    public List<RequestLogNginxVo> convert(List<Map<String, Object>> res) {
        List<RequestLogNginxVo> list=new ArrayList<>();

        res.forEach(u->{
            RequestLogNginxVo vo=new RequestLogNginxVo();
            for(Map.Entry<String,Object> entry:u.entrySet()){
                if(entry.getValue().equals("null")){
                    continue;
                }
                switch (entry.getKey()){
                    case "timestamp":
                        vo.setTimestamp((String) entry.getValue());
                        break;
                    case "urlOriginal":
                        vo.setUrlOriginal((String) entry.getValue());
                        break;
                    case "httpRequestMethod":
                        vo.setHttpRequestMethod((String) entry.getValue());
                        break;
                    case "httpResponseStatusCode":
                        vo.setHttpResponseStatusCode(Integer.parseInt((String) entry.getValue()));
                        break;
                    case "httpResponseBodyBytes":
                        vo.setHttpResponseBodyBytes(Integer.parseInt((String) entry.getValue()));
                        break;
                }
            }
            list.add(vo);
        });

        return list;
    }
}

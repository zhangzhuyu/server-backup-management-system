package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApiModel("慢查询")
@Data
public class SlowLogVo {
    @ApiModelProperty("SQL语句")
    private Object sqlStr;
    @ApiModelProperty("执行用户")
    private Object user;
    @ApiModelProperty("执行时间")
    private Object execTime;
    @ApiModelProperty("执行次数")
    private Object executes;
    @ApiModelProperty("执行耗时(秒)")
    private Object execDuration;
    @ApiModelProperty("平均耗时(秒)")
    private Object avgExecDuration;
    @ApiModelProperty("连接地址")
    private Object connIP;
    @ApiModelProperty("最后活跃时间")
    private Object latestActiveTime;

    public List<SlowLogVo> convertToList(List<Map<String, Object>> res, Integer pageNum, Integer pageSize) {
        List<SlowLogVo> list=new ArrayList<>();
//        res.forEach(u->{
//        });
        for(int i=(pageNum-1)*pageSize;i<pageNum*pageSize && i<res.size();i++){ //分页
            list.add(toSlowLogVo(res.get(i)));
        }
        return list;
    }

    private SlowLogVo toSlowLogVo(Map<String, Object> u) {
        SlowLogVo vo= new SlowLogVo();
        //转化成vo
        try{
            vo.setConnIP( u.get("connIP"));
            vo.setAvgExecDuration( u.get("avgExecDuration"));
            vo.setExecDuration( u.get("execDuration"));
            vo.setExecutes( u.get("executes"));
            vo.setExecTime( u.get("execTime"));
            vo.setSqlStr( u.get("sqlStr"));
            vo.setLatestActiveTime( u.get("latestActiveTime"));
            vo.setUser( u.get("user"));
        }catch (Exception e){

        }
        return vo;
    }
}

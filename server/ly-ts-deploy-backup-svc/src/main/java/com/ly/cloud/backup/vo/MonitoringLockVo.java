package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApiModel("监控锁")
@Data
public class MonitoringLockVo {
    @ApiModelProperty("持有锁数据库用户")
    private String lockerHolder;
    @ApiModelProperty("持有锁数据库用户schema")
    private String schema;
    @ApiModelProperty("被锁对像")
    private String lockedObject;
    @ApiModelProperty("SID")
    private Integer sid;
    @ApiModelProperty("serial#")
    private Long serial;
    @ApiModelProperty("持有锁应用")
    private String app;
    @ApiModelProperty("登陆时间")
    private String loginTime;
    @ApiModelProperty("活动状态")
    private String activeStatus;
    @ApiModelProperty("解锁SQL")
    private String unlockSql;

    public List<MonitoringLockVo> convertToList(List<Map<String, Object>> res, Integer pageNum, Integer pageSize) {
        List<MonitoringLockVo> list=new ArrayList<>();
        for(int i=(pageNum-1)*pageSize;i<pageNum*pageSize&&i<res.size();i++){
            list.add(toMonitoringLockVo(res.get(i)));
        }
        return list;
    }

    private MonitoringLockVo toMonitoringLockVo(Map<String, Object> u) {
        MonitoringLockVo vo=new MonitoringLockVo();
        try{
            vo.setLockerHolder((String) u.get("username"));
            vo.setSchema((String) u.get("schema"));
            vo.setLockedObject((String) u.get(""));//?
            vo.setSid(Integer.parseInt((String) u.get("SID")));
            vo.setSerial((Long) u.get("SERIAL"));
            vo.setApp((String) u.get("machine"));
            vo.setLoginTime((String) u.get("loginTime"));
            vo.setActiveStatus((String) u.get("status"));
            vo.setUnlockSql("alter system kill session '"+u.get("SID")+","+u.get("SERIAL")+"'"); //拼接解锁命令
        }catch (Exception e){
            e.printStackTrace();
        }
        return vo;
    }
}

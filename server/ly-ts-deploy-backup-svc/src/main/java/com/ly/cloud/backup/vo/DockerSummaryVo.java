package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@ApiModel("docker监控页面概览")
@Data
public class DockerSummaryVo {
    @ApiModelProperty("容器数量运行")
    private Integer running;
    @ApiModelProperty("容器数量暂停")
    private Integer paused;
    @ApiModelProperty("容器数量停止")
    private Integer stopped;
    @ApiModelProperty("容器总数量")
    private Integer total;
    @ApiModelProperty("docker cpu使用率")
    private Double pct;
    @ApiModelProperty("cpu告警数")
    private Integer alertCpu;
    @ApiModelProperty("内存告警数")
    private Integer alertMemory;
    @ApiModelProperty("网络IO告警数")
    private Integer alertNetwork;

    public static DockerSummaryVo convert(List<Map<String, Object>> res) {
        DockerSummaryVo vo = new DockerSummaryVo();
        int total = 0;
        res.forEach(u -> {
            for (Map.Entry<String, Object> entry : u.entrySet()) {
                if (entry.getValue() != null && !"null".equals(entry.getValue())) {
                    try {
                        switch (entry.getKey()) {
                            case "running":
                                vo.setRunning(Integer.parseInt((String) entry.getValue()));
                                break;
                            case "paused":
                                vo.setPaused(Integer.parseInt((String) entry.getValue()));
                                break;
                            case "stopped":
                                vo.setStopped(Integer.parseInt((String) entry.getValue()));
                                break;
                            case "total":
                                if (vo.getTotal() == null)
                                    vo.setTotal(Integer.parseInt((String) entry.getValue()));
                                break;
                            case "pct":
                                if (vo.getPct() == null)
                                    vo.setPct(Double.parseDouble((String) entry.getValue()));
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return vo;
    }
}

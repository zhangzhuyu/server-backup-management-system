package com.ly.cloud.backup.scheduled;


import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author SYC
 * @Date: 2022/3/7 17:22
 * @Description 报告模板
 */
@Data
public class ReportTemplate implements Serializable {

    private String titleNumber;
    private String templateName;
    private String titleGroupName;
    private String titleName;
    private String titleType;
    private Set<Map<String,Object>> systems;
    private Set<Map<String,Object>> departments;
    private String systemNumber;
    private String systemName;
    private String departmentNumber;
    private String departmentName;
    private Set<String> titleGroupNames;
    private String configurationValue;
    private String result;
    private String month;
    private List<Map<String, Object>> warningRecordObjectVos;
    private List<Map<String,Object>> others;
    private Map<String,Object> ucInfos;





}

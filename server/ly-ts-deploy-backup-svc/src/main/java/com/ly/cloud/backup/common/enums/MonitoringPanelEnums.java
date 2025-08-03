package com.ly.cloud.backup.common.enums;


/**
 * 监控面板主页
 */
public enum MonitoringPanelEnums {


    HOST(1001,"1","host",0L,0L,"Filebeat AwS S3 ServerAccess Log OverviewDashboard"),
    ORACLE(2001,"2-1","oracle",0L,0L,"An overview of key metrics from all Metricsets in the Oracle database Metricbe..."),
    DOCKER(5005,"5-5","docker",0L,0L,"overview of docker containers"),
    NGINX(6001,"6","nginx",0L,0L,"Dashboard for the FilebeatNginx Ingress Controller"),
    MYSQL(2002, "2-2", "mysql",0L,0L,"overview dashboard for the Filebeat MysQL module"),
//    SQLSERVER(2003, "2-3","sqlserver",0L,0L,""),
//    POSTGRESQL(2004, "2-4","postgresql",0L,0L,""),
//    KINGBASE(2005, "2-5","kingbase",0L,0L,""),
    REDIS(5001,"5-1","redis",0L,0L,"overview dashboard for the Filebeat Redis module"),
    MONGODB(5002,"5-2","mongodb",0L,0L,"Filebeat MongoDB module overview"),
    RABBIT(5003,"5-3","rabbit",0L,0L,"Metricbeat RabbitMQ overview EC");
//    ELASTICSEARCH(5004,"5-4","elasticsearch",0L,0L,""),
    ;

    private Integer code;
    private String resourcesType;
    private String name;
    private Long ruleTotal;
    private Long recordTotal;
    private String description;

    MonitoringPanelEnums(Integer code, String resourcesType, String name, Long ruleTotal, Long recordTotal,String description) {
        this.code = code;
        this.resourcesType = resourcesType;
        this.name = name;
        this.ruleTotal = ruleTotal;
        this.recordTotal = recordTotal;
        this.description= description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getResourcesType() {
        return resourcesType;
    }

    public void setResourcesType(String resourcesType) {
        this.resourcesType = resourcesType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRuleTotal() {
        return ruleTotal;
    }

    public void setRuleTotal(Long ruleTotal) {
        this.ruleTotal = ruleTotal;
    }

    public Long getRecordTotal() {
        return recordTotal;
    }

    public void setRecordTotal(Long recordTotal) {
        this.recordTotal = recordTotal;
    }

    public String getDescription(){ return  description;}

    public void setDescription(String description){this.description=description;}

    public static MonitoringPanelEnums getMonitoringPanelEnumsByResourcesType(String resourcesType) {
        MonitoringPanelEnums[] values = MonitoringPanelEnums.values();
        for (MonitoringPanelEnums value : values) {
            if(value.resourcesType.equals(resourcesType)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "{ 'code':'"+code+"','resourcesType':'"+resourcesType+"','name':'"+name+"','ruleTotal':'"+ruleTotal+"','recordTotal':'"+recordTotal+"'}";
    }
}

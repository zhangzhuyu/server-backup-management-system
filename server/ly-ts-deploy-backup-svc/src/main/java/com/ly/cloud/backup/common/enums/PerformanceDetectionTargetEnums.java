package com.ly.cloud.backup.common.enums;

public enum PerformanceDetectionTargetEnums {

    HARDWARE("1001", "硬件",""),
    MIDDLEWARE("1002", "中间件",""),
    RELATIONAl_DATABASE("1003", "关系型数据库",""),
    CPU("1004", "CPU","cpu.performance"),
    INTERNAL_STORAGE("1005", "内存","memory.performance"),
    DISK("1006", "磁盘","disk.speed"),
    NETWORK_CARD("1007", "网卡","speedtest.net.network.speed"),
    DOMAIN_NAME_TEST("1008", "域名测试",""),
    PARAMETER_DETECTION("1009", "参数检测",""),
    REDIS("1010","Redis",""),
    RABBITMQ("1011","Rabbitmq",""),
    ELASTIC("1012","Elastic",""),
    MONGDB("1013","Mongdb",""),
    FASTDFS("1014","Fastdfs",""),
    ORACLE("1015","Oracle",""),
    MYSQL("1016","Mysql",""),
    PGSQL("1017","Pgsql",""),
    CALCULATION_SPED("1018","计算速度(分数)","cpu.performance"),
    READ_AND_WRITE_SPEED("1019","读写速度","memory.performance"),
    DISK_IOPS("1020","磁盘IOPS(每秒读写值)","disk.speed"),
    UPLOAD_SPEED("1021","上传速度","Upload Speed"),
    DOWNLOAD_SPEED("1022","下载速度","Download Speed"),
    TCP("1023","TCP",""),
    TAPE_WIDTH("1024","带宽",""),
    LINUX_PARAMETER_OPTIMIZATION("1025","Linux参数优化",""),
    NETWORK_CARD_INFORMATION_COLLCTION("1026","网卡信息采集",""),
    HOSTS_CONTENT_COLLECTION("1027","hosts内容采集",""),
    DNS_COLLECTION("1028","Dns的采集",""),
    IPV6_COLLECTION("1029","Ipv6的采集",""),
    PORT_STATUS("1030","端口状态",""),
    SOFTWARE_STATUS_COLLECTION("1031","软件状态采集",""),
    AUDIT_EXCEPTION_LOG("1032","审计日常日志",""),
    PROCESS_STATUS("1033","进程状态的采集",""),
    NETWORK_ACCESSIBILITY_TEST("1034","网络通达性测试",""),
    DOCKER_ENHANCED_ACQUISITION("1035","Docker增强采集",""),
    ADAPT_TO_LINUX_VERSION("1036","适配Linux版本",""),
    REDIS_QPS("1037","qps",""),
    REDIS_TPS("1038","tps",""),
    REDIS_RM("1039","rm",""),
    RABBITMQ_QPS("1040","qps",""),
    RABBITMQ_TPS("1041","tps",""),
    RABBITMQ_RM("1042","rm",""),
    ELASTIC_QPS("1043","qps",""),
    ELASTIC_TPS("1044","tps",""),
    ELASTIC_RM("1045","rm",""),
    MONGDB_QPS("1046","qps",""),
    MONGDB_TPS("1047","tps",""),
    MONGDB_RM("1048","rm",""),
    FASTDFS_QPS("1049","qps",""),
    FASTDFS_TPS("1050","tps",""),
    FASTDFS_RM("1051","rm",""),
    ORACLE_QPS("1052","qps",""),
    ORACLE_TPS("1053","tps",""),
    ORACLE_RM("1054","rm",""),
    MYSQL_QPS("1055","qps",""),
    MYSQL_TPS("1056","tps",""),
    MYSQL_RM("1057","rm",""),
    PGSQL_QPS("1058","qps",""),
    PGSQL_TPS("1059","tps",""),
    PGSQL_RM("1060","rm","");

    private String code;
    private String name;
    private String esKey;

    PerformanceDetectionTargetEnums(String code, String name, String esKey) {
        this.code = code;
        this.name = name;
        this.esKey = esKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEsKey() {
        return esKey;
    }

    public void setEsKey(String esKey) {
        this.esKey = esKey;
    }
}

package com.ly.cloud.backup.common.constant;


/**
 * @author SYC
 * @Date: 20230316 15:50
 * @Description  kibana的URL链接常量
 */
public class KibanaUrlConstant {



    /**
     * apm uri前缀(7.17.5之前的版本)
     */
    public static final String APM_URI_PREFIX = "/api/apm";

    /**
     * apm uri前缀(7.17.5之后的版本)
     */
    public static final String INTERNAL_APM_URI_PREFIX = "/internal/apm";


    /**
     * 登录链接
     */
    public static final String INTERNAL_SECURITY_LOGIN = "/internal/security/login";


    /**
     * 获取链路ID
     */
    public static final String TRACES = "/traces";

    /**
     * 取kibana的延迟分布chart图表左边信息
     */
    public static final String TRANSACTIONS_CHARTS_THROUGHPUT = "/services/{0}/transactions/charts/throughput?environment={1}&start={2}&end={3}&transactionType={4}&transactionName={5}";


    /**
     * 取kibana的延迟分布chart图表信息
     */
    public static final String TRANSACTIONS_CHARTS_DISTRIBUTION = "/services/{0}/transactions/charts/distribution?environment={1}&start={2}&end={3}&transactionType={4}&transactionName={5}";


    /**
     * 获取kibana的事务列表信息
     */
    public static final String TRANSACTIONS_GROUPS_DETAILED_STATISTICS = "/services/{0}/transactions/groups/detailed_statistics?environment={1}&start={2}&end={3}&numBuckets={4}&transactionType={5}&latencyAggregationType={6}&transactionNames={7}&comparisonStart={8}&comparisonEnd={9}";

    /**
     * 获取kibana的全部事务列表信息
     */
    public static final String TRANSACTIONS_GROUPS_MAIN_STATISTICS = "/services/{0}/transactions/groups/main_statistics?environment={1}&start={2}&end={3}&transactionType={4}&latencyAggregationType={5}";



//-------------------------7.17.5之后的版本------------------------------------
    /**
     * 获取kibana的jvm图表信息
     */
    public static final String METRICS_CHARTS = "/services/{0}/metrics/charts?environment={1}&start={2}&end={3}&kuery={4}&agentName={5}&serviceNodeName={6}";

    public static final String LATENCY_OVERALL_DISTRIBUTION = "/latency/overall_distribution";

}

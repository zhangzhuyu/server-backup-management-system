package com.ly.cloud.backup.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ly.cloud.backup.common.constant.CommonConstant.NULL;
import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.HOST_NAME;
import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.IP;

/**
 * @author SYC
 * @Date: 2022/5/26 19:34
 * @Description 通过metricbeat虚拟IP，获取真实ipv4
 */
public  class RealIpUtil {

    private static final Logger logger = LoggerFactory.getLogger(RealIpUtil.class);

    @Autowired
    private static RestHighLevelClient restHighLevelClient;

    /**
     *  从metricBeat中获取真实IP
     * @param excludedIpSegment:排除的IP段（用于过滤docker网络的IP端）172.200,172.199,172.198
     * @param excludedFixedIp:排除的固定IP（用于过滤虚拟机本身的IP）192.168.122.1
     * @return
     * @throws IOException
     */
    public static String getRealIp(String hostIpStr,String excludedIpSegment,String excludedFixedIp) throws IOException {
        //定义最终返回的真实IP
        String resultRealIp ="";
        try {
            List<String> ipList = Arrays.asList(hostIpStr.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
            List<String> tempIpList = Lists.newArrayList();
           //排除的IP段
            List<String> excludedIpSegmentLists = Arrays.asList(excludedIpSegment.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
           //排除的固定IP
            List<String> excludedFixedIpList = Arrays.asList(excludedFixedIp.split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList());
            //被过滤的IP地址
            ipList.forEach(tempIp -> {
                if (!excludedFixedIpList.contains(tempIp)) {
                    tempIpList.add(tempIp);
                }
            });
            //被过滤的IP段
            String finalResultRealIp = resultRealIp;
            List<String> filterIps = tempIpList.stream().map((filterIp) -> {
                for (String excludedIpSegmentList : excludedIpSegmentLists) {
                    if (filterIp.startsWith(excludedIpSegmentList)) {
                        return filterIp;
                    }
                }
                return finalResultRealIp;
            }).collect(Collectors.toList());
            //取差集
            List<String> resultIps = tempIpList.stream().filter(item -> !filterIps.contains(item)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(resultIps)) {
                String realIp = resultIps.get(0);
                logger.info("hostIpStr->realIp真实IP："+realIp);
                resultRealIp=realIp;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return resultRealIp;
    }

    /**
     *  通过虚拟IP从metricBeat中获取真实IP
     * @param virtualIpSegment:虚拟IP段（用于验证当前获取到的IP是否已经为真实IP）
     * @param excludedIpSegment:排除的IP段（用于过滤docker网络的IP端）
     * @param excludedFixedIp:排除的固定IP（用于过滤虚拟机本身的IP）
     * @return
     * @throws IOException
     */
    public static String getRealIp(String virtualIp,RestHighLevelClient restHighLevelClient,String virtualIpSegment,String excludedIpSegment,String excludedFixedIp) throws IOException {
        //定义最终返回的真实IP
        String resultRealIp = "";
        try {
            //验证当前获取到的IP是否已经为真实IP，是则直接返回结果。
            if (!virtualIp.startsWith(virtualIpSegment)){
                if (NULL.equalsIgnoreCase(virtualIp)){
                    return "";
                }
                return virtualIp;
            }
            // 1、SearchRequest
            SearchRequest searchRequest = new SearchRequest("metricbeat-*");
            // 2、指定查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //2.1、查询字段过滤
            String[] excludes = {};
            String[] includes = {"host.ip"};
            searchSourceBuilder.fetchSource(includes, excludes);
            // 2.2、查询条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            /**此处使用模糊匹配查询 类比数据库中 like    name代表的是字段名，‘动态’代表的是匹配的关键字*/
            //QueryBuilder 适用于单个字段查询（matchPhraseQuery是没有用分词起，matchQuery会使用分词器，将我们输入的值进行分割，如：“java动态”会分割成：“java”,“动态”）
            QueryBuilder qb1 = QueryBuilders.matchPhraseQuery("host.ip_str", virtualIp);
//            QueryBuilder qb1 = QueryBuilders.matchPhraseQuery("host.ip", virtualIp);
            BoolQueryBuilder bqb1 = QueryBuilders.boolQuery().must(qb1);
            boolQuery.must(bqb1);
            // 排序
            searchSourceBuilder.sort("@timestamp");
            searchSourceBuilder.query(boolQuery);
            searchSourceBuilder.size(1);
            searchRequest.source(searchSourceBuilder);
            // 3、执行
            SearchResponse resp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Map<String, Object> resultHashMap = new HashMap<>();
            // 4、打印
            for (SearchHit hit : resp.getHits()) {
                // 获取高亮返回值
                String sourceAsString = hit.getSourceAsString();
                if (StringUtils.isNotEmpty(sourceAsString)) {
                    HashMap hashMap = JSON.parseObject(sourceAsString, HashMap.class);
                    if (hashMap.get("host") instanceof Map) {
                        resultHashMap.putAll((Map<? extends String, ?>) hashMap.get("host"));
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(resultHashMap)) {
                String separator = ",";
                if (resultHashMap.get(IP) instanceof List) {
                    List ipList = (List) resultHashMap.get(IP);
                    List<String> tempIpList = Lists.newArrayList();
                    List<String> excludedIpSegmentLists = Arrays.asList(excludedIpSegment.split(separator)).stream().map(s -> s.trim()).collect(Collectors.toList());
                    List<String> excludedFixedIpList = Arrays.asList(excludedFixedIp.split(separator)).stream().map(s -> (s.trim())).collect(Collectors.toList());
                    ipList.forEach(item -> {
                        String tempIp = item.toString();
                        if (whetherforIp(item.toString()) && !virtualIp.equals(item.toString())
                                && !excludedFixedIpList.contains(tempIp)) {
                            tempIpList.add(tempIp);
                        }
                    });
                    //获取被过滤的IP地址
                    List<String> filterIps = tempIpList.stream().map((filterIp) -> {
                        for (String excludedIpSegmentList : excludedIpSegmentLists) {
                            if (filterIp.startsWith(excludedIpSegmentList)) {
                                return filterIp;
                            }
                        }
                        return virtualIp;
                    }).collect(Collectors.toList());
                    //取差集
                    List<String> resultIps = tempIpList.stream().filter(item -> !filterIps.contains(item)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(resultIps)) {
                        String realIp = resultIps.get(0);
                        logger.info("virtualIp->realIp真实IP："+realIp);
                        resultRealIp=realIp;
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            throw new IOException("IO异常");
        }
        return resultRealIp;
    }

    /**
     * 通过IP从metricBeat中获取hostname
     * @param ip
     * @param restHighLevelClient
     * @return
     * @throws IOException
     */
    public static String getHostname(String ip,RestHighLevelClient restHighLevelClient) throws IOException {
        //定义最终返回的真实IP
        String hostname ="" ;
        try {
            // 1、SearchRequest
            SearchRequest searchRequest = new SearchRequest("metricbeat-*");
            // 2、指定查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //2.1、查询字段过滤
            String[] excludes = {};
            String[] includes = {HOST_NAME};
            searchSourceBuilder.fetchSource(includes, excludes);
            // 2.2、查询条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            /**此处使用模糊匹配查询 类比数据库中 like    name代表的是字段名，‘动态’代表的是匹配的关键字*/
            //QueryBuilder 适用于单个字段查询（matchPhraseQuery是没有用分词起，matchQuery会使用分词器，将我们输入的值进行分割，如：“java动态”会分割成：“java”,“动态”）
            QueryBuilder qb1 = QueryBuilders.matchPhraseQuery("host.ip", ip);
            BoolQueryBuilder bqb1 = QueryBuilders.boolQuery().must(qb1);
            boolQuery.must(bqb1);
            // 排序
            searchSourceBuilder.sort("@timestamp");
            searchSourceBuilder.query(boolQuery);
            searchSourceBuilder.size(1);
            searchRequest.source(searchSourceBuilder);
            // 3、执行
            SearchResponse resp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Map<String, Object> resultHashMap = new HashMap<>();
            // 4、打印
            for (SearchHit hit : resp.getHits()) {
                // 获取高亮返回值
                String sourceAsString = hit.getSourceAsString();
                if (StringUtils.isNotEmpty(sourceAsString)) {
                    HashMap hashMap = JSON.parseObject(sourceAsString, HashMap.class);
                    if (hashMap.get("host") instanceof Map) {
                        resultHashMap.putAll((Map<? extends String, ?>) hashMap.get("host"));
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(resultHashMap)) {
                if (resultHashMap.get("name") instanceof String) {
                    hostname = resultHashMap.get("name").toString();
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return hostname;
    }

    /**
     * 利用正则表达式判断字符是否为IP
     * @param ipString
     * @return
     */
    public static boolean whetherforIp(String ipString) {
        String ipRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";    //IP地址的正则表达式
        //如果前三项判断都满足，就判断每段数字是否都位于0-255之间
        if (ipString.matches(ipRegex)) {
            String[] ipArray = ipString.split("\\.");
            for (int i = 0; i < ipArray.length; i++) {
                int number = Integer.parseInt(ipArray[i]);
                //4.判断每段数字是否都在0-255之间
                if (number < 0 || number > 255) {
                    return false;
                }
            }
            return true;
        } else {
            return false;    //如果与正则表达式不匹配，则返回false
        }
    }

}

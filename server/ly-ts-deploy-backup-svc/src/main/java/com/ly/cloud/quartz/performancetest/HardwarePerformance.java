package com.ly.cloud.quartz.performancetest;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ly.cloud.backup.util.JSchUtil;
import com.ly.cloud.backup.util.PublicUtil;
import com.ly.cloud.backup.vo.ServerVo;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


@Component
public class HardwarePerformance {

    private static final String PERFORMANCETEST = "performance-test";

    private static final String BENCH = "Bench.sh";

    private static final String PERFORMANCEMONITORING = "dc/performance-monitoring";

    private static Logger logger= LoggerFactory.getLogger(HardwarePerformance.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Async("threadPoolConf")
    public Future<String> uploadScriptFile(ServerVo serverPo) {
        String s = "";
        try {

            JSchUtil jSchUtil = new JSchUtil(serverPo.getIpv4(), serverPo.getUser(), serverPo.getPassword(), Integer.parseInt(serverPo.getPort()), 3 * 60 * 1000);
            //JSchUtil jSchUtil = new JSchUtil("192.168.35.202", "root", "Ly37621040", 22, 3 * 60 * 1000);
            //JSchUtil jSchUtil = new JSchUtil("192.168.34.136", "root", "^GM@CuSLe7", 22, 3 * 60 * 1000);
            //登录失败则跳过
            //判断文件是否存在，不存在则创建文件夹
            String exists = jSchUtil.execCommand("[ -f /data/" + PERFORMANCETEST + "/" + BENCH + " ] && echo yes || echo no");
            exists = exists.replace("\n","");
            if ("no".equals(exists)) {
                jSchUtil.execCommand("mkdir /data/" + PERFORMANCETEST);
                byte[] profileInfo = PublicUtil.readProfileInfo("/performanceTest/Bench.sh");
                profileInfo = new String(profileInfo).replaceAll("\r\n", "\n").getBytes();

                ByteArrayInputStream infoStream = new ByteArrayInputStream(profileInfo);
                jSchUtil.loginSftp();
                jSchUtil.uploadFileByte("/data/" + PERFORMANCETEST, BENCH, infoStream);
                jSchUtil.logoutSftp();
                exists = jSchUtil.execCommand("[ -f /data/" + PERFORMANCETEST + "/" + BENCH + " ] && echo yes || echo no").replace("\n","");
            }
            if ("yes".equals(exists)) {
                s = jSchUtil.execCommand("cd /data/" + PERFORMANCETEST + "&& chmod 777 ./* && ./Bench.sh --full");
            }
            if (StringUtils.isNotEmpty(s)) {
                String[] split = s.replaceAll("\r\n", "\n").split("\n");
                //对查询的结果去除空行，以->结尾， 1/3进度的行 如：
                /**
                 *
                 *  Speedtest Default              ->
                 *  10MB-1M Block          301 MB/s (287 IOPS, 0.03s)              ->
                 *  1 Thread - Write Test:         1/3
                 *  1 Thread - Write Test:         2/3
                 *  1 Thread - Write Test:         3/3
                 *  6  *
                 *  7  *
                 */
                String collect = Arrays.stream(split).filter(s1 -> StringUtils.isNotEmpty(s1) && !s1.endsWith("->") && !s1.matches(".*[0-9]+/[0-9].*") && !s1.matches(".*[0-9]+.*\\*")).collect(Collectors.joining("\n"));
                collect = collect.replaceAll("\u001B\\[[0-9]+m", "");
                String startTimeStr = "Bench Start Time: ";
                String finishTimeStr = "Bench Finish Time: ";
                String timePart = "yyyy-MM-dd HH:mm:ss";
                /**
                 * 截取出检测开始时间与结束时间
                 */
                String startTime = collect.substring(collect.indexOf(startTimeStr) + (startTimeStr.length()), collect.indexOf(startTimeStr) + (startTimeStr.length()) + timePart.length() + 1);
                String finishTime = collect.substring(collect.indexOf(finishTimeStr) + (finishTimeStr.length()), collect.indexOf(finishTimeStr) + (finishTimeStr.length()) + timePart.length() + 1);
                collect = collect.substring(collect.indexOf("->"));
                Map<String, String> map = new HashMap<>();
                startTime = startTime.replace("\n","");
                finishTime = finishTime.replace("\n","");
                map.put("startTime",startTime);
                map.put("finishTime",finishTime);
                map.put("serverId",serverPo.getIpv4());
                Boolean flag = false;
                while (collect.indexOf("->") > -1) {
                    //截取出键 -> 后面内容为键 如  -> CPU Performance Test (Standard Mode, 3-Pass @ 30sec)
                    collect = collect.substring(collect.indexOf("->") + 2);
                    String substring = collect.substring(0, collect.indexOf("\n")).trim();
                    String [] str = substring.split(" ");
                    List<String> list = new ArrayList<>();
                    //对键进行处理 按空格进行切割，去除test 并去除括号内容（除非括号内容为ipv4 或 ipv6）
                    // 如：CPU Performance Test (Standard Mode, 3-Pass @ 30sec) 处理后为 cpu.performance  ; Traceroute Test (IPV4)  处理后为 traceroute.ipv4
                    for (String s1 : str) {
                        s1 = s1.toLowerCase();
                        if (!"test".equals(s1)) {
                            if (s1.indexOf("(") > -1) {
                                s1 = s1.replace("(", "");
                                s1 = s1.replace(")", "");
                                if (!"ipv6".equals(s1) && !"ipv4".equals(s1)) {
                                    break;
                                } else {
                                    list.add(s1);
                                }
                            } else {
                                list.add(s1);
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(list)) {
                        substring = list.stream().collect(Collectors.joining("."));
                    }
                    String substring1 = "";
                    if (collect.indexOf("->") > -1) {
                        substring1 = collect.substring(collect.indexOf("\n"), collect.indexOf("->")).trim();
                    } else {
                        substring1 = collect.substring(collect.indexOf("\n")).trim();
                    }
                    substring = substring.replace("\n","");
                    substring1 = substring1.replace("\n",";");
                    map.put(substring,substring1);
                    flag = true;
                }
                if (flag) {
                    String hardware = putDataForIndex("hardware", map);
                }

            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            logger.info(e.getMessage());
        }

        return new AsyncResult<String>(s);
    }


    public String putDataForIndex(String indexName, Map<String,String> map){
        if (CollectionUtils.isEmpty(map)) {
            return "存入数据为空";
        }

        if (StringUtils.isEmpty(indexName)){
            return "请指定索引名称";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder indexBuilder = new StringBuilder();
        indexBuilder.append(indexName);
        indexBuilder.append("-");
        indexBuilder.append(simpleDateFormat.format(new Date()));

        String fullIndexName = indexBuilder.toString();
        System.out.println("索引名称是："+fullIndexName);


        try {
            XContentBuilder contentBuilder = XContentFactory.jsonBuilder();
            contentBuilder.startObject();
            Set<Map.Entry<String, String>> entries = map.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                contentBuilder.field(entry.getKey(), entry.getValue());
            }
            contentBuilder.endObject();

            //索引请求
            IndexRequest indexRequest = new IndexRequest(fullIndexName).source(contentBuilder);

            IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

//            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "SUCCESS";
    }

    @Async("defaultThreadPoolExecutor")
    public Future<Boolean>  test(ServerVo vo){

        JSchUtil jSchUtil = new JSchUtil(vo.getIpv4(), "root", vo.getPassword(), 22, 3 * 60 * 1000);
        boolean b = jSchUtil.loginSftp();
        System.out.println(vo.getIpv4() + ":" +b);
        return new AsyncResult<Boolean>(b);

    }

}

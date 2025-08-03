package com.ly.cloud.backup.util.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ly.cloud.backup.dto.*;
import com.ly.cloud.backup.po.SystemSetupPo;
import com.ly.cloud.backup.service.SystemSetupService;
import com.ly.cloud.backup.util.RealIpUtil;
import com.ly.cloud.backup.util.SqlTimeUtil;
import com.ly.cloud.backup.vo.LineChartVo;
import com.ly.cloud.backup.vo.LineDataVo;
import com.ly.cloud.backup.vo.PanelDuelYVo;
import com.ly.cloud.backup.vo.RingPieChartVo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.InternalSum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ly.cloud.backup.common.constant.SystemSetupConstant.*;
import static com.ly.cloud.backup.common.constant.SystemSetupConstant.EXCLUDED_FIXED_IP;

public class ChartDataUtil {
    private static Logger logger = LoggerFactory.getLogger(ChartDataUtil.class);

    //一图单条拆线
    public static LineChartVo singleLine(List<Map<String, Object>> res, PanelThresholdDto threshold, String valueFieldName, String... legend) {
        LineChartVo vo = new LineChartVo();
        List<LineDataVo> dataVos = new ArrayList<>();
        res.forEach(u -> {
            LineDataVo tmp = new LineDataVo();
            Boolean abnormal = false;
            Double val;
            try {
                val = Double.parseDouble((String) u.get(valueFieldName));
                if (abnormal || abnormal(val, threshold)) { //如有异常直接全线异常
                    abnormal = true;
                }
            } catch (Exception e) {
//                logger.error((String) u.get(valueFieldName),e.getMessage(),e);
            }
            Object time = u.get("@timestamp");
            if (time == null) time = u.get("timestamp");
            if (u.get(valueFieldName) != null && !"null".equals(u.get(valueFieldName))) {
                Object[] o = {time, getRound(u.get(valueFieldName))}; //"established"
                tmp.setValue(o);
                tmp.setAbnormal(abnormal);
                dataVos.add(tmp);
            }
        });
        if (legend.length > 0) {
            vo.setName(legend[0]);
        }
        vo.setData(dataVos);
        return vo;
    }
    public static LineChartVo singleLineDSL(JSONObject jsonObject, PanelThresholdDto threshold, String valueFieldName, String... legend) {
        LineChartVo vo = new LineChartVo();
        List<LineDataVo> dataVos = new ArrayList<>();
        String legendName="linevalues";
        if(legend.length!=0){
            legendName=legend[0];
            vo.setName(legendName);
        }
        if(jsonObject.getJSONObject("aggregations")==null){
            return vo;
        }
        JSONArray arr=jsonObject.getJSONObject("aggregations").getJSONObject(legendName).getJSONArray("buckets");
        for(int i=0;i<arr.size();i++){
            JSONObject obj=arr.getJSONObject(i);
            if(obj.getJSONObject(valueFieldName)==null){
                continue;
            }
            Double val=obj.getJSONObject(valueFieldName).getDouble("value");
            long key=obj.getLongValue("key");
            Boolean abnormal = false;
            if (abnormal || abnormal(val, threshold)) { //如有异常直接全线异常
                abnormal = true;
            }
            Object[] o = {key, getRound(val)}; //"established"
            LineDataVo tmp = new LineDataVo();
            tmp.setValue(o);
            tmp.setAbnormal(abnormal);
            dataVos.add(tmp);
        }
            vo.setData(dataVos);
        vo.setData(dataVos);
        return vo;
    }
    public static List<LineChartVo> multiLineOfTimeBucketForQueryDSL(JSONObject jsonObject, PanelThresholdDto threshold, String valueFieldName, int groupTimes) {
        String legendName="linevalues";
//        if(legend.length!=0){
//            legendName=legend[0];
//            vo.setName(legendName);
//        }
        JSONArray arr=jsonObject.getJSONObject("aggregations").getJSONObject(legendName).getJSONArray("buckets");
        Map<String,List<LineDataVo>>lineMap=new HashMap<>();
        for(int i=0;i<arr.size();i++) {//遍历每个时间,第一次遍历，记录有哪些code
            JSONObject obj=arr.getJSONObject(i);
            long key=obj.getLongValue("key");
            if(groupTimes==1){
                List<LineDataVo>line=new ArrayList<>();
                lineMap.put("line",line);
            }else {
                if(obj.getJSONObject(valueFieldName)==null){
                    continue;
                }
                JSONArray buckets=obj.getJSONObject(valueFieldName).getJSONArray("buckets");//获取某个时间有哪些code
                for (int j = 0; j < buckets.size(); j++) {
                    JSONObject bucket = buckets.getJSONObject(j);
                    String statuCode = bucket.getString("key");
                    lineMap.put(statuCode, new ArrayList<>());
                }
            }
        }
        for(int i=0;i<arr.size();i++){//遍历每个时间,第二次遍历，存放值
            JSONObject obj=arr.getJSONObject(i);
            long key=obj.getLongValue("key");

            if(groupTimes==1){
                int count=obj.getIntValue("doc_count");
                Boolean abnormal = false;
                if (abnormal || abnormal((double)count, threshold)) { //如有异常直接全线异常
                    abnormal = true;
                }
                Object[] o = {key, getRound(count)}; //"established"
                LineDataVo tmp = new LineDataVo();
                tmp.setValue(o);
                tmp.setAbnormal(abnormal);
                lineMap.get("line").add(tmp);
            }else{
                if(obj.getJSONObject(valueFieldName)==null){
                    continue;
                }
                JSONArray buckets=obj.getJSONObject(valueFieldName).getJSONArray("buckets");//获取某个时间有哪些code
                Map<String,LineDataVo> codeeMap=new HashMap<>();
                for(int j=0;j<buckets.size();j++){//存放值到codeeMap
                    JSONObject bucket=buckets.getJSONObject(j);
                    String statuCode=bucket.getString("key");
                    int count=bucket.getIntValue("doc_count");
                    Boolean abnormal = false;
                    if (abnormal || abnormal((double)count, threshold)) { //如有异常直接全线异常
                        abnormal = true;
                    }
                    Object[] o = {key, getRound(count)}; //"established"
                    LineDataVo tmp = new LineDataVo();
                    tmp.setValue(o);
                    tmp.setAbnormal(abnormal);
                    codeeMap.put(statuCode,tmp);
                }
                for(Map.Entry<String,List<LineDataVo>>en:lineMap.entrySet()){//设置值到lineMap
                    String statusCode=en.getKey();
                    List<LineDataVo> list=en.getValue();
                    if(codeeMap.containsKey(statusCode)){//如果有值就使用已经有点
                        list.add(codeeMap.get(statusCode));
                    }else{//如果这个时间点没有值，那么就放空值
                        Object[] o = {key, 0};
                        LineDataVo tmp = new LineDataVo();
                        tmp.setValue(o);
                        list.add(tmp);
                    }
                }
            }
        }
        List<LineChartVo>res=new ArrayList<>();
        for(Map.Entry<String,List<LineDataVo>>en:lineMap.entrySet()){//lineMap转成返回值格式
            String statusCode=en.getKey();
            List<LineDataVo> list=en.getValue();
            LineChartVo line=new LineChartVo();
            line.setName(statusCode);
            line.setData(list);
            res.add(line);
        }
        return res;
    }
    public static List<LineChartVo> multiLineDSL(JSONObject jsonObject, PanelThresholdDto threshold, String valueFieldName, int times, String... legend) {
        List<LineChartVo> vos = new ArrayList<>();

        String[] legendNames=new String[]{"linevalues"};
        if(legend.length!=0){
            legendNames=legend;
        }
        if(jsonObject.getJSONObject("aggregations")==null){
            return null;
        }
        for(String legendName:legendNames){
            List<LineDataVo> dataVos = new ArrayList<>();
            LineChartVo vo=new LineChartVo();
            vo.setName(legendName);
            if(times==1){
                JSONArray arr=jsonObject.getJSONObject("aggregations").getJSONObject(valueFieldName).getJSONArray("buckets");
                for(int i=0;i<arr.size();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    if(obj.getJSONObject(legendName)==null){
                        continue;
                    }
                    Double val=obj.getJSONObject(legendName).getDouble("value");
                    long key=obj.getLongValue("key");
                    Boolean abnormal = false;
                    if (abnormal || abnormal(val, threshold)) { //如有异常直接全线异常
                        abnormal = true;
                    }
                    Object[] o = {key, getRound(val)}; //"established"
                    LineDataVo tmp = new LineDataVo();
                    tmp.setValue(o);
                    tmp.setAbnormal(abnormal);
                    dataVos.add(tmp);
                }
            }else{
                JSONArray arr=jsonObject.getJSONObject("aggregations").getJSONObject(legendName).getJSONArray("buckets");
                for(int i=0;i<arr.size();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    if(obj.getJSONObject(valueFieldName)==null){
                        continue;
                    }
                    Double val=obj.getJSONObject(valueFieldName).getDouble("value");
                    long key=obj.getLongValue("key");
                    Boolean abnormal = false;
                    if (abnormal || abnormal(val, threshold)) { //如有异常直接全线异常
                        abnormal = true;
                    }
                    Object[] o = {key, getRound(val)}; //"established"
                    LineDataVo tmp = new LineDataVo();
                    tmp.setValue(o);
                    tmp.setAbnormal(abnormal);
                    dataVos.add(tmp);
                }
            }

            vo.setData(dataVos);
            vos.add(vo);
        }

        return vos;
    }

    //一图多折线
    public static List<LineChartVo> multiLine(List<Map<String, Object>> res, String legendFieldName, String valueFieldName, PanelThresholdDto thresholdDto, Boolean... ifTop10) { //,
        List<LineChartVo> list = new ArrayList<>();
        //每条拆线一个Map
        Map<String, Double> legendsMap = new HashMap<>();
        res.forEach(u -> {
            legendsMap.put((String) u.get(legendFieldName), 0d); //"device"
        });
        for (Map.Entry<String, Double> entry : legendsMap.entrySet()) {
            String key = entry.getKey();
            LineChartVo lineChartVo = new LineChartVo();
            List<LineDataVo> subli = new ArrayList<>();
            Boolean abnormal = false;
            for (Map<String, Object> u : res) {
                if (u.get(legendFieldName) != null && u.get(legendFieldName).equals(key)) { //"device"=categoryKey
                    LineDataVo tmp = new LineDataVo();
                    Object time = u.get("@timestamp");
                    if (time == null) time = u.get("timestamp");
                    try {
                        double value = Double.parseDouble((String) u.get(valueFieldName)); // / 1024 / 1024 / 1024
                        if (value > legendsMap.get(key)) {
                            legendsMap.put(key, value);
                        } //用于排序
                        if (abnormal || abnormal(value, thresholdDto)) { //如有异常直接全线异常
                            abnormal = true;
                        }
                        Object[] o = {time, getRound(value)}; //"pct" valueKey
                        tmp.setValue(o);
                        subli.add(tmp);
                    } catch (Exception e) {
                    }
                }
            }
            lineChartVo.setName(key);
            lineChartVo.setData(subli);
            lineChartVo.setAbnormal(abnormal);
            list.add(lineChartVo);
        }
        list.forEach(u->{
            //全部取平均
            u.setData(ChartDataUtil.getAvgLineDataListByTime(u.getData(),1d));
        });
        if (ifTop10.length > 0) {
            //排序
            List<LineChartVo> newList = new ArrayList<>();
            String key = "";
            for (int i = 0; i < 10 && i<list.size(); i++) {
                double max = 0d;
                for (Map.Entry<String, Double> entry : legendsMap.entrySet()) {
                    if(entry.getValue()==null)continue;
                    key =entry.getKey();
                    if (entry.getValue() > max) {
                        max = entry.getValue();
                        key = entry.getKey();
                    }
                }
                for (LineChartVo vo : list) {
                    if (vo.getName()!=null&&vo.getName().equals(key)) {
                        newList.add(vo);
                        break;
                    }
                }
                legendsMap.put(key,null); //legendsMap.remove(key); 不会真的把这个删除
            }
            return newList;
        } else {
            return list;
        }
    }

    //一图多折线，但值名即折线名的情况
    public static List<LineChartVo> multiLineOfLegends(List<Map<String, Object>> res, PanelThresholdDto thresholdDto, String[] legends) {
        List<LineChartVo> list = new ArrayList<>();
        Arrays.asList(legends).forEach(u -> {
            list.add(singleLine(res, thresholdDto, u, u));
        });
        return list;
    }
    public static List<LineChartVo> multiLineOfLegendsForQueryDSL(JSONObject res, PanelThresholdDto thresholdDto, String keyname,String[] legends,int times) {
        return multiLineDSL(res, thresholdDto, keyname, times,legends);
    }

    //一图多拆线，同时有一个线名(legend)根据不同类型（如分读/写）分拆成几条的
    public static List<LineChartVo> multiLinePerMetric(List<Map<String, Object>> res, String legendFieldName, String... valueFieldName) {
        List<LineChartVo> list = new ArrayList<>();

        //每条拆线一个Map
        Map<String, Integer> legendsMap = new HashMap<>();
        res.forEach(u -> {
            legendsMap.put((String) u.get(legendFieldName), 1); //"device"
        });

        for (Map.Entry<String, Integer> entry : legendsMap.entrySet()) {
            String key = entry.getKey();
            for (String s : valueFieldName) {
                //分区名
                LineChartVo lineChartVo = new LineChartVo();
                List<LineDataVo> subList = new ArrayList<>();
                res.forEach(u -> {
                    if (u.get(legendFieldName).equals(key)) { //"device"=categoryKey
                        LineDataVo tmp = new LineDataVo();
                        Object time = u.get("@timestamp");
                        if (time == null) time = u.get("timestamp");
                        Object[] o = {time, getRound(u.get(s))}; //"pct" valueKey
                        tmp.setValue(o);
                        subList.add(tmp);
                    }
                });
                String finalKey = key;
                if (valueFieldName.length > 1) {
                    finalKey += "_" + s;
                }
                lineChartVo.setName(finalKey);
                lineChartVo.setData(subList);
                list.add(lineChartVo);
            }

        }
        return list;
    }

    //一般保留两位，太小数保留4位小数点
    public static Object getRound(Object value) {
        int keep;
        //四舍五入，保留小数点后几位
        Double d = parseDouble(value);
        if (d > 0.01) {
            keep = 2;
        } else if (d < 0.01 && d > 0.0001) {
            keep = 4;
        } else {
            keep = 0;
        }
        return String.format("%." + keep + "f", d);
    }

    //getRoundInUnit获取GB,MB,KB单位的值
    public static String getRoundInUnit(Object value, String unit) {
        int keep;
        //四舍五入，保留小数点后几位
        Double d = parseDouble(value);
        switch (unit) {
            case "GB":
                d = d / Math.pow(2, 30);
                break;
            case "MB":
                d = d / Math.pow(2, 20);
                break;
            case "KB":
                d = d / Math.pow(2, 10);
                break;
        }
        if (d > 0.01) {
            keep = 2;
        } else if (d < 0.01 && d > 0.0001) {
            keep = 4;
        } else {
            keep = 0;
        }
        return String.format("%." + keep + "f", d);

    }

    public static String getKbMbGbByValue(Object value){
        int keep;String unit="";
        Double d = parseDouble(value);
        if(d>Math.pow(2,30)){
            d=d/Math.pow(2,30);
            unit="GB";
        }else if(d>Math.pow(2,20)){
            d=d/Math.pow(2,20);
            unit="MB";
        }else if(d>Math.pow(2,10)){
            d=d/Math.pow(2,10);
            unit="KB";
        }else{
            unit="B";
        }
        if (d > 0.01) {
            keep = 2;
        } else if (d < 0.01 && d > 0.0001) {
            keep = 4;
        } else {
            keep = 0;
        }
        return String.format("%." + keep + "f", d)+unit;
    }

    public static Map<String, List<RingPieChartVo>> ringPieChart(List<Map<String, Object>> res, String innerFields, String outerFields, Boolean hasOuter) {
        Map<String, Double> innerPie = new HashMap<>();
        Map<String, Double> outerPie = new HashMap<>();

        for (Map<String, Object> m : res) {
            if (innerPie.get(m.get(innerFields)) == null) {
                innerPie.put((String) m.get(innerFields), 1d); //"browser"
            } else {
                Double d = innerPie.get(m.get(innerFields)) + 1; //"browser"
                innerPie.put((String) m.get(innerFields), d); //"browser"
            }
            if (hasOuter) {
                if (outerPie.get(m.get(outerFields)) == null) { //"version"
                    outerPie.put((String) m.get(outerFields), 1d); //"version"
                } else {
                    Double d = outerPie.get(m.get(outerFields)) + 1; //"version"
                    outerPie.put((String) m.get(outerFields), d);  //"version"
                }
            }
        }
        Map<String, List<RingPieChartVo>> result = new HashMap<>();
        result.put("inner", getRingChartVo(innerPie));
        if (hasOuter) {
            result.put("outer", getRingChartVo(outerPie));
        }
        return result;
    }

    public static List<RingPieChartVo> getRingChartVo(Map<String, Double> pie) {
        List<RingPieChartVo> list = new ArrayList<>();
        for (Map.Entry<String, Double> entry : pie.entrySet()) {
            RingPieChartVo vo = new RingPieChartVo();
            vo.setName(entry.getKey());
            vo.setValue((String) ChartDataUtil.getRound(entry.getValue() / pie.size()));
            list.add(vo);
        }
        return list;
    }

    public static Boolean abnormal(Double d, PanelThresholdDto dto) {
        if (dto == null) return false;
        if (dto.getLte() != null && d < dto.getLte()) {
            return true;
        }
        if (dto.getGte() != null && d > dto.getGte()) {
            return true;
        }
        if (dto.getBtwLow() != null && dto.getBtwUpper() != null && d >= dto.getBtwLow() && d <= dto.getBtwUpper()) {
            return true;
        }
        return false;
    }

    public static List<PanelObjIPStatusDto> combineIPStatus(List<PanelObjIPStatusDto> list, List<Map<String, Object>> res) {
        //把没有服务地址的去掉
        for (int i = 0; i < list.size(); i++) {
            String ip=list.get(i).getIp();
            if (ip == null||"".equals(ip)||"null".equals(ip)) list.remove(i);
        }
        for (Map<String, Object> u : res) {
            String key = (String) u.get("ip");
            boolean exists = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getIp() != null && key.contains(list.get(i).getIp())) {
                    list.get(i).setIp(key);//改成es中查出的长ip
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                PanelObjIPStatusDto dto = new PanelObjIPStatusDto();
                dto.setIp((String) u.get("ip"));
                if (u.get("status") == null) {
                    dto.setStatus("0");
                } else {
                    dto.setStatus("1");
                }
                list.add(dto);
            }
        }
        //保证ip唯一性的map
        Map<String, Integer> ipsMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            ipsMap.put(list.get(i).getIp(), i);
        }
        List<PanelObjIPStatusDto> newList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ipsMap.entrySet()) {
            newList.add(list.get(entry.getValue()));
        }
        return newList;
    }

    //非数据类的分时段统计个数生成直方图的方法
    public static List<LineChartVo> getCountHistogram(RestHighLevelClient client, String table, String[] fields, String startTime, String endTime, String interval, PanelThresholdDto threshold) {
        List<LineChartVo> lineChartVos = new ArrayList<>();
        // from "metricbeat-*"
        SearchRequest request = new SearchRequest(table);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // select xxx
        builder.fetchSource(fields, new String[]{});
        // from  to
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp").gte(convertTime(startTime, null)).lte(convertTime(endTime, null));
        //完成查询条件
        builder.query(rangeQueryBuilder);
        //聚合
        for (int i = 0; i < fields.length; i++) {
            //按字段统计个数
            DateHistogramInterval interval1 = new DateHistogramInterval(interval); //"10s"
            DateHistogramAggregationBuilder t = AggregationBuilders.dateHistogram(fields[i]).field(fields[i]).fixedInterval(interval1).minDocCount(0);
            builder.aggregation(t);
        }
        request.source(builder);
        //查出结果
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            for (int i = 0; i < fields.length; i++) {
                //每个字段生成一条线
                LineChartVo lineChartVo = new LineChartVo();
                lineChartVo.setName(fields[i]);
                List<LineDataVo> dataVos = new ArrayList<>();
                lineChartVo.setData(dataVos);
                Boolean abnormal = false;
                //解析聚合数据
                ParsedDateHistogram aggs = aggregations.get(fields[i]);
                List<? extends Terms.Bucket> buckets = (List<? extends Terms.Bucket>) aggs.getBuckets();
                for (Terms.Bucket v : buckets) {
                    String time = (String) v.getKey();
                    Long count = v.getDocCount();
                    Object[] o = new Object[]{time, count};
                    LineDataVo vo = new LineDataVo();
                    vo.setValue(o);
                    if (threshold != null) {
                        if (abnormal || abnormal(Double.valueOf(count), threshold)) { //如有异常直接全线异常
                            abnormal = true;
                        }
                    }
                    vo.setAbnormal(false);
                    dataVos.add(vo);
                }

                lineChartVo.setAbnormal(abnormal);
                lineChartVos.add(lineChartVo);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return lineChartVos;
    }


    public static String convertTime(String timeStr, Long offset) {
        Date d = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (timeStr != null) {
            try {
                d = f.parse(timeStr);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        if (d == null && offset != null) {
            d = DateUtils.addHours(new Date(), (int) (-offset / 60 / 60 / 1000) - 8);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        if (d == null) {
            return null;
        }
        String s = format1.format(d);
        return format.format(d).replace(s + " ", s + "T") + "Z";
    }

    //getCountHistogramByCode 先按code聚合再分时段聚合http请求状态码
    public static List<LineChartVo> getCountHistogramByCode(RestHighLevelClient client, String table, String[] fields, String startTime, String endTime, String interval, PanelThresholdDto threshold) {
        List<LineChartVo> lineChartVos = new ArrayList<>();
        // from "metricbeat-*"
        SearchRequest request = new SearchRequest(table);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // select xxx
        builder.fetchSource(fields, new String[]{});
        // from  to
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp").gte(convertTime(startTime, null)).lte(convertTime(endTime, null));
        //完成查询条件  ---这个有200, 300, 400,500等，有多重聚合
        builder.query(rangeQueryBuilder);
        //聚合
        for (int i = 0; i < fields.length; i++) {
            //先按数据聚合
            TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("httpCode").field(fields[i]);
            //再按按字段统计个数
            termsAggregationBuilder.subAggregation(AggregationBuilders.dateHistogram(fields[i]).field(fields[i]).fixedInterval(new DateHistogramInterval(interval)).minDocCount(0));
            builder.aggregation(termsAggregationBuilder);
        }
        request.source(builder);

        //查出结果
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            for (int i = 0; i < fields.length; i++) {
                //解析聚合数据
                ParsedTerms aggregation = aggregations.get("httpCode");
                List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
                for (Terms.Bucket u : buckets) {
                    Aggregations subAggregations = u.getAggregations();
                    List<String> codes = new ArrayList<>();
                    for (Aggregation a : subAggregations) {
                        codes.add(String.valueOf(a.getName()));
                    }
                    for (String s : codes) {
                        //每个字段生成一条线
                        LineChartVo lineChartVo = new LineChartVo();
                        lineChartVo.setName(s); //线名
                        List<LineDataVo> dataVos = new ArrayList<>();
                        lineChartVo.setData(dataVos);
                        Boolean abnormal = false;

                        ParsedTerms subParsedTerms = subAggregations.get(s);
                        List<? extends Terms.Bucket> subBuckets = subParsedTerms.getBuckets();
                        for (Terms.Bucket y : subBuckets) {
                            String time = (String) u.getKey();
                            Long count = u.getDocCount();
                            Object[] o = new Object[]{time, count};
                            LineDataVo vo = new LineDataVo();
                            vo.setValue(o);
                            if (threshold != null) {
                                if (abnormal || abnormal(Double.valueOf(count), threshold)) { //如有异常直接全线异常
                                    abnormal = true;
                                }
                            }
                            vo.setAbnormal(false);
                            dataVos.add(vo);
                        }
                        lineChartVo.setAbnormal(abnormal);
                        lineChartVos.add(lineChartVo);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return lineChartVos;
    }

    //getSumHistogram 分时段聚合后再sum聚合
    public static List<LineChartVo> getSumHistogram(RestHighLevelClient client, String table, String[] fields, String startTime, String endTime, String interval, PanelThresholdDto thresholdDto) {
        List<LineChartVo> lineChartVos = new ArrayList<>();
        // from "metricbeat-*"
        SearchRequest request = new SearchRequest(table);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // select xxx
        builder.fetchSource(fields, new String[]{});
        // from  to
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp").gte(convertTime(startTime, null)).lte(convertTime(endTime, null));
        //完成查询条件  ---这个有200, 300, 400,500等，有多重聚合
        builder.query(rangeQueryBuilder);
        //聚合
        for (int i = 0; i < fields.length; i++) {
            //先按数据聚合 ,再按按字段统计个数
            DateHistogramAggregationBuilder dateAggr = AggregationBuilders.
                    dateHistogram("groupByTenSeconds").field(fields[i]).fixedInterval(new DateHistogramInterval(interval)).minDocCount(0);
            dateAggr.subAggregation(AggregationBuilders.sum("sumUp").field(fields[i]));
            builder.aggregation(dateAggr);
        }
        request.source(builder);

        //查出结果
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            ParsedDateHistogram terms = aggregations.get("groupByTenSeconds");
            //每个桶只把sum后的那个值拿出来
            LineChartVo lineChartVo = new LineChartVo();
            lineChartVo.setName("data_volume");
            List<LineDataVo> lineDataVos = new ArrayList<>();
            lineChartVo.setData(lineDataVos);
            Boolean abnormal = false;
            for (Histogram.Bucket bucket : terms.getBuckets()) {
                //取父级的时间
                String time = bucket.getKeyAsString();
                InternalSum sum = bucket.getAggregations().get("sumUp");
                Double value = sum.getValue();
                Object[] o = new Object[]{time, value};
                LineDataVo vo = new LineDataVo();
                vo.setValue(o);
                if (thresholdDto != null) {
                    if (abnormal || abnormal(Double.valueOf(value), thresholdDto)) { //如有异常直接全线异常
                        abnormal = true;
                    }
                }
                vo.setAbnormal(false);
                lineDataVos.add(vo);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return lineChartVos;
    }

    public static String getHostIPStr(Map<String, Object> u, String excludedIpSegment, String excludedFixedIp) {
        String realIP = null;
        Map<String, Object> host = (Map<String, Object>) u.get("host");
        if (host != null) {
            List<String> ipList = (List<String>) host.get("ip");
            Collections.sort(ipList);
            //取出数字开头的
            List<String> ipInNum = new ArrayList<>();
            for (String ip : ipList) {
                String p = "^[1-9]{1,3}.*";
                Pattern pattern = Pattern.compile(p);
                Matcher matcher = pattern.matcher(ip);
                if (matcher.matches()) {
                    ipInNum.add(ip);
                }
            }
            if (ipList == null) {
                return null;
            }
            //有数字ip的
            for (int i = ipInNum.size() - 1; i >= 0; i--) {
                try {
                    realIP = RealIpUtil.getRealIp(ipInNum.get(i), excludedIpSegment, excludedFixedIp);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return null;
                }
                return realIP;
            }
            for (String s : ipList) {
                try {
                    realIP = RealIpUtil.getRealIp(s, excludedIpSegment, excludedFixedIp);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return null;
                }
                return realIP;
            }
        }
        return null;
    }

    public static PanelExcludeIPDto getExcluded(SystemSetupService systemSetupService) {
        List<SystemSetupPo> systemSetupPos = systemSetupService.list();
//        SystemSetupPo systemSetupPoVirtualIpSegment = systemSetupPos.stream()
//                .filter(f -> VIRTUAL_IP_SEGMENT.equalsIgnoreCase(f.getSetKey()))
//                .collect(Collectors.toMap(SystemSetupPo::getSetKey, Function.identity())).get(VIRTUAL_IP_SEGMENT);
//        String virtualIpSegment = systemSetupPoVirtualIpSegment.getSystray();
        SystemSetupPo systemSetupPoExcludedIpSegment = systemSetupPos.stream()
                .filter(f -> EXCLUDED_IP_SEGMENT.equalsIgnoreCase(f.getSetKey()))
                .collect(Collectors.toMap(SystemSetupPo::getSetKey, Function.identity())).get(EXCLUDED_IP_SEGMENT);
        String excludedIpSegment = systemSetupPoExcludedIpSegment.getSystray();
        SystemSetupPo systemSetupPoExcludedFixedIp = systemSetupPos.stream()
                .filter(f -> EXCLUDED_FIXED_IP.equalsIgnoreCase(f.getSetKey()))
                .collect(Collectors.toMap(SystemSetupPo::getSetKey, Function.identity())).get(EXCLUDED_FIXED_IP);
        String excludedFixedIp = systemSetupPoExcludedFixedIp.getSystray();
        PanelExcludeIPDto dto = new PanelExcludeIPDto();
        dto.setExcludedIpSegment(excludedIpSegment);
        dto.setExcludedFixedIp(excludedFixedIp);
        return dto;
    }

    public static void c(Logger logger) {
        ChartDataUtil.logger = logger;
    }

    public static Double parseDouble(Object o) {
        if (o == null) return 0d;
        Double d = 0d;
        try {
            if (o instanceof String) {
                if("null".equals(o)||"".equals(o)) return 0d;
                d = Double.valueOf((String) o);
            } else if (o instanceof Long) {
                d = Double.valueOf((Long) o);
            } else if (o instanceof Integer) {
                d = Double.valueOf(o.toString());
            } else if (o instanceof Double) {
                d = (Double) o;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return d;
    }

    public static List<LineDataVo> getAvgLineDataListByTime(List<LineDataVo> lineDataVos, Double multipleTimes) {
        List<Object[]> objects = lineDataVos.stream().map(u -> u.getValue()).collect(Collectors.toList());
        List<Object[]> newObjects = getAvgListByTime(objects, multipleTimes);
        List<LineDataVo> vos = newObjects.stream().map(u -> {
            LineDataVo vo = new LineDataVo();
            vo.setValue(u);
            return vo;
        }).collect(Collectors.toList());
        return vos;
    }

    public static List<Object[]> getAvgListByTime(List<Object[]> originalList, Double multipleTimes) {
        List<Object[]> newList = new ArrayList<>();
        //Map时间唯一
        Map<String, PanelAvgMapDto> avgMap = new HashMap<>();
        for (Object[] o : originalList) {
            try {
                String time = (String) o[0];
                Double d = 0d;
                if(o[1] instanceof String){
                    String val= (String) o[1];
                    val=val.replace("GB","").replace("MB","").replace("KB","");
                    d = ChartDataUtil.parseDouble(val);  
                }else if(o[1] instanceof  Double){
                    d= (Double) o[1];
                }                
                if (avgMap.get(time) != null) {
                    PanelAvgMapDto tmpDto = avgMap.get(time);
                    avgMap.get(time).setCount(tmpDto.getCount() + 1);
                    avgMap.get(time).setTotal(tmpDto.getTotal() + d);
                } else {
                    PanelAvgMapDto tmpDto = new PanelAvgMapDto();
                    tmpDto.setCount(1);
                    tmpDto.setTotal(d);
                    avgMap.put(time, tmpDto);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        for (Map.Entry<String, PanelAvgMapDto> en : avgMap.entrySet()) {
            Object[] tmp = new Object[]{en.getKey(), en.getValue().getTotal() * multipleTimes / en.getValue().getCount()};
            newList.add(tmp);
        }
        //按时间排序
        newList.sort(new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                int i = 0;
                String t1 = ((String) o1[0]).replace("T", " ").replace("Z", "");
                String t2 = ((String) o2[0]).replace("T", " ").replace("Z", "");
                //将s转化成date
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                try {
                    Date d1 = format.parse(t1);
                    Date d2 = format.parse(t2);
                    if (d2.after(d1)) {
                        return -1;
                    } else {
                        return 1;
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                return 0;
            }
        });
        return newList;
    }

    public static List<LineChartVo> getTop10(List<LineChartVo> lineChartVos) {
        List<LineChartVo> newList = new ArrayList<>();
        Map<String, Double> maxValueMaps = new HashMap<>();

        lineChartVos.forEach(u -> {
            Double tmp = 0d;
            for (LineDataVo vo : u.getData()) {
                Double d = parseDouble(vo.getValue());
                if (d > tmp) {
                    tmp = d;
                }
            }
            maxValueMaps.put(u.getName(), tmp);
        });
        List<PanelTop10MapDto> maxValueList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : maxValueMaps.entrySet()) {
            PanelTop10MapDto dto = new PanelTop10MapDto();
            dto.setName(entry.getKey());
            dto.setValue(entry.getValue());
            maxValueList.add(dto);
        }
        //排序
        Collections.sort(maxValueList);
        for (int i = 0; i < maxValueList.size() && i < 10; i++) {
            for (LineChartVo vo : lineChartVos) {
                if (StringUtils.isNotEmpty(vo.getName()) && vo.getName().equals(maxValueList.get(i).getName())) {
                    newList.add(vo);
                }
            }
        }
        return newList;
    }

    public static List<LineDataVo> getMaxLineDataListByTime(List<LineDataVo> data, double multipleTimes) {
        List<Object[]> objects = data.stream().map(u -> u.getValue()).collect(Collectors.toList());
        List<Object[]> newObjects = getMaxLineDataByTime(objects, multipleTimes);
        List<LineDataVo> vos = newObjects.stream().map(u -> {
            LineDataVo vo = new LineDataVo();
            vo.setValue(u);
            return vo;
        }).collect(Collectors.toList());
        return vos;
    }

    private static List<Object[]> getMaxLineDataByTime(List<Object[]> objects, double multipleTimes) {
        List<Object[]> newList = new ArrayList<>();
        //Map时间唯一
        Map<String, Double> maxMap = new HashMap<>();
        for (Object[] o : objects) {
            try {
                String time = (String) o[0];
                Double d = ChartDataUtil.parseDouble(o[1]);
                if (maxMap.get(time) != null &&maxMap.get(time)<d) {
                    maxMap.put(time,d);
                } else {
                    maxMap.put(time, d);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        for (Map.Entry<String, Double> en : maxMap.entrySet()) {
            Object[] tmp = new Object[]{en.getKey(), en.getValue()};
            newList.add(tmp);
        }
        //按时间排序
        newList.sort(new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                int i = 0;
                String t1 = ((String) o1[0]).replace("T", " ").replace("Z", "");
                String t2 = ((String) o2[0]).replace("T", " ").replace("Z", "");
                //将s转化成date
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                try {
                    Date d1 = format.parse(t1);
                    Date d2 = format.parse(t2);
                    if (d2.after(d1)) {
                        return -1;
                    } else {
                        return 1;
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                return 0;
            }
        });
        return newList;
    }

    public static List<LineDataVo> getRateList(List<LineDataVo> data, double v) {
        List<LineDataVo> newList=new ArrayList<>();
        if(data.size()==0) return newList;

        Double lastValue=0d;
        for(int i=0;i<data.size();i++){
            LineDataVo u=new LineDataVo();
            if(i==0){
                u.setValue(new Object[]{data.get(0).getValue()[0],0});
            }
            Double value=parseDouble(data.get(i).getValue()[1]);
            Double rate=value-lastValue;
            lastValue = value;
            u.setValue(new Object[]{data.get(i).getValue()[0],rate});
            newList.add(u);
        }
        return newList;
    }

    public static PanelDuelYVo toDual(LineChartVo total) {
        PanelDuelYVo vo=new PanelDuelYVo();
        vo.setData(total.getData());
        vo.setName(total.getName());
        return vo;
    }
    //subtractWeekMonth 解决环比曲线显示因时间错开的问题
    public static List<LineDataVo> subtractWeekMonth(LineChartVo current, Long dayDiff) {
        current.getData().forEach(u->{
            String time= (String) u.getValue()[0];
            time = SqlTimeUtil.subtract(time,dayDiff);
            u.setValue(new Object[]{time,u.getValue()[1]});
        });
        return current.getData();
    }

    public static JSONObject OsBrowser(JSONObject res) {
        JSONObject list=new JSONObject();
        list.put("os",new JSONObject());
        list.put("browser",new JSONObject());
        list.getJSONObject("os").put("outer",new JSONArray());
        list.getJSONObject("os").put("inner",new JSONArray());
        list.getJSONObject("browser").put("outer",new JSONArray());
        list.getJSONObject("browser").put("inner",new JSONArray());
        JSONArray arr=res.getJSONObject("aggregations").getJSONObject("os").getJSONArray("buckets");
        for(int i=0;i<arr.size();i++){
            JSONObject obj=arr.getJSONObject(i);
//            if(obj.getJSONObject(valueFieldName)==null){
//                continue;
//            }
            JSONObject outer=new JSONObject();
            outer.put("name",obj.getString("key"));
            outer.put("value",obj.getIntValue("doc_count"));
            list.getJSONObject("os").getJSONArray("outer").add(outer);
            JSONArray b=obj.getJSONObject("osversion").getJSONArray("buckets");
            for(int j=0;j<b.size();j++) {
                JSONObject inn=b.getJSONObject(j);
                JSONObject inner=new JSONObject();
                inner.put("name",inn.getString("key"));
                inner.put("value",inn.getString("doc_count"));
                list.getJSONObject("os").getJSONArray("inner").add(inner);
            }
        }
        arr=res.getJSONObject("aggregations").getJSONObject("browser").getJSONArray("buckets");
        for(int i=0;i<arr.size();i++){
            JSONObject obj=arr.getJSONObject(i);
//            if(obj.getJSONObject(valueFieldName)==null){
//                continue;
//            }
//            Object b=obj.getJSONObject("osversion").get("buckets");
//            JSONObject os=new JSONObject();
//            os.put("browser",obj.getString("key"));
//            os.put("version",b);
//            os.put("count",obj.getIntValue("doc_count"));
//            list.getJSONArray("browser").add(os);

            JSONObject outer=new JSONObject();
            outer.put("name",obj.getString("key"));
            outer.put("value",obj.getIntValue("doc_count"));
            list.getJSONObject("browser").getJSONArray("outer").add(outer);
            JSONArray b=obj.getJSONObject("osversion").getJSONArray("buckets");
            for(int j=0;j<b.size();j++) {
                JSONObject inn=b.getJSONObject(j);
                JSONObject inner=new JSONObject();
                inner.put("name",inn.getString("key"));
                inner.put("value",inn.getString("doc_count"));
                list.getJSONObject("browser").getJSONArray("inner").add(inner);
            }
        }
        return list;
    }

    public static JSONObject OsBrowser2(JSONObject res) {
        JSONObject list=new JSONObject();
        list.put("os",new JSONArray());
        list.put("browser",new JSONArray());
        JSONArray arr=res.getJSONObject("aggregations").getJSONObject("os").getJSONArray("buckets");
        for(int i=0;i<arr.size();i++){
            JSONObject obj=arr.getJSONObject(i);
//            if(obj.getJSONObject(valueFieldName)==null){
//                continue;
//            }
            Object b=obj.getJSONObject("osversion").get("buckets");
            JSONObject os=new JSONObject();
            os.put("os",obj.getString("key"));
            os.put("version",b);
            os.put("count",obj.getIntValue("doc_count"));
            list.getJSONArray("os").add(os);
        }
        arr=res.getJSONObject("aggregations").getJSONObject("browser").getJSONArray("buckets");
        for(int i=0;i<arr.size();i++){
            JSONObject obj=arr.getJSONObject(i);
//            if(obj.getJSONObject(valueFieldName)==null){
//                continue;
//            }
            Object b=obj.getJSONObject("osversion").get("buckets");
            JSONObject os=new JSONObject();
            os.put("browser",obj.getString("key"));
            os.put("version",b);
            os.put("count",obj.getIntValue("doc_count"));
            list.getJSONArray("browser").add(os);
        }
        return list;
    }

    public static List<LineChartVo> convertToMB(List<LineChartVo> lineChartVos) {
        return lineChartVos.stream().map(u->{
            List<LineDataVo> dataVos=u.getData().stream().map(v->{
                Object d=0d;
                if(v.getValue()[1] instanceof String){
                    d= getRound(Double.parseDouble((String) v.getValue()[1])/1024/1024);
                }else if(v.getValue()[1] instanceof Double){
                    d=(Double)v.getValue()[1]/1024/1024;
                }
                v.getValue()[1]=d;
                return v;
            }).collect(Collectors.toList());
            u.setData(dataVos);
            return u;
        }).collect(Collectors.toList());
    }

    public static Boolean checkTotalAbnormal(List<LineChartVo> lineChartVos) {
        Boolean abnormal=false;
        for(LineChartVo vo: lineChartVos){
            if(vo.getAbnormal()!=null&&vo.getAbnormal()){ abnormal=true; break;}
        }
        return abnormal;
    }
}


//        Double d = null;
//        try {
//            if(value instanceof String){
//                d=Double.parseDouble((String) value);
//                if(d==null){ //防止数值太大转不了，先除1024/1024/1024
//                    BigDecimal bi=new BigDecimal((char[]) value);
//                    BigDecimal bigDecimal=  bi.divide(BigDecimal.valueOf(1024)).divide(BigDecimal.valueOf(1024)).divide(BigDecimal.valueOf(1024));
//                    d=Double.parseDouble(bigDecimal.toString());
//                }
//            }else{
//                d=(Double) value;
//            }
//        } catch (Exception e) {
//            return "0";
//        }
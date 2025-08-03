package com.ly.cloud.quartz.task;

import org.quartz.*;

/**
 * @Author ljb
 * @Date 2022/5/31
 */

//@PersistJobDataAfterExecution
//@DisallowConcurrentExecution
//@TaskNode(taskName = "监控执行定时任务")
public class WarningRuleTask implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
/*

    private static final Logger logger = LoggerFactory.getLogger(WarningRuleTask.class);

    */
/**运行告警记录数 **//*

    public static int record;
    private static final String SERVER = CommonEnums.TARGET_OBJECT_SERVER.getCode().toString();
    private static final String DATABASE = CommonEnums.TARGET_OBJECT_DATABASE.getCode().toString();
    private static final String SERVICE = CommonEnums.TARGET_OBJECT_SERVICE.getCode().toString();
    private static final String APPLICATION = CommonEnums.TARGET_OBJECT_APPLICATION.getCode().toString();

    @Autowired
    private RedisConf redisUtil;

    @Autowired
    private WarningRuleMapper warningRuleMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private WarningRecordService warningRecordService;

    @Autowired
    private DatabaseMapper databaseMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ApplicationMapper applicationMapper;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 告警记录dto
        WarningRecordDto warningRecordDto = new WarningRecordDto();

        // 获取任务数据
        JobDataMap dataMap = context.getMergedJobDataMap();
        WarningRuleDto dto = (WarningRuleDto) dataMap.get("dto");

        record = dataMap.getIntValue("record");
        // 本次任务连续失败或超出阈值的记录数

        int count = 0;


        // 获取需要监控的目标list
        List<String> objectList = dto.getEarlyWarningHostList();
        String target = dto.getTarget();
        // 需要告警的对象ids
        List<String> resultIdList = new ArrayList();
        //触发预警的对象名称
        List<String> resultNameList = new ArrayList<>();
        // 全部的ids ,下面更新状态时候用
        List<String> updateSuccList = new ArrayList<>();
        List<ServerPo> serverPos = new ArrayList<>();
        List<DatabasePo> databasePos = new ArrayList<>();
        List<ServicePo> servicePos = new ArrayList<>();
        List<ApplicationPo> applicationPos = new ArrayList<>();
        if (StringUtils.equals(SERVER, target)) {
            //是否选择全部
            if (objectList.contains("-1")) {
                //根据不同指标获取其全部内容
                serverPos = serverMapper.selectList(null);
            } else {
                serverPos = serverMapper.selectBatchIds(objectList);
            }
        }
        else if (StringUtils.equals(DATABASE, target)) {
            //是否选择全部
            if (objectList.contains("-1")) {
                //根据不同指标获取其全部内容
                databasePos = databaseMapper.selectList(null);
            } else {
                databasePos = databaseMapper.selectBatchIds(objectList);
            }
        }
        else if (StringUtils.equals(SERVICE, target)) {
            //是否选择全部
            if (objectList.contains("-1")) {
                //根据不同指标获取其全部内容
                servicePos = serviceMapper.selectList(null);
            } else {
                servicePos = serviceMapper.selectBatchIds(objectList);
            }
        }
        else if (StringUtils.equals(APPLICATION, target)) {
            //是否选择全部
            if (objectList.contains("-1")) {
                applicationPos = applicationMapper.selectList(null);
            } else {
                applicationPos = applicationMapper.selectBatchIds(objectList);
            }
        }

        // 通过target获取 redis结果，没有缓存则查询
        if (UsualUtil.objNotNull(dto.getType())) {
            String redisCacheResult = (String) redisUtil.get(REDIS_KEY_PREFIX_TC_TARGET_MODULE + dto.getType() + ":");
            List<Map<String, Object>> list;
            //有缓存结果
            if (UsualUtil.objNotNull(redisCacheResult)) {
                list = JSONObject.parseObject(redisCacheResult, List.class);
            } else {
                String sqlLine = warningRuleMapper.getSqlLineById(Long.parseLong(dto.getType()));
                // 要是传有其他参数,则替换这条sql的占位符.
                if (UsualUtil.strIsNotEmpty(dto.getParam0())) {
                    sqlLine = sqlLine.replace("?", " '" + dto.getParam0() + "' ");
                }
                logger.info(sqlLine);
                list = ElasticUtil.sql(sqlLine, restHighLevelClient);
            }

            // 能从elk或缓存读到对象的指标监控记录
            if (UsualUtil.collIsNotEmpty(list)) {
                //判断是那种类型
                //是主机类型
                if (UsualUtil.objEquals(SERVER, dto.getTarget())) {
                    ///循环遍历监控的主机是否有elk记录
                    for ( ServerPo serverPo : serverPos) {
                        String key = serverPo.getIpv4();
                        //  预警对象是否有 标志符
                        Boolean flag = false;
                        for (Map<String, Object> map : list) {
                            String ips = (String) map.get("ip");
                            // 目标ip 是否在此记录ip池里
                            if (ips.indexOf(key) >= 0) {
                                // 比对此记录的是否超出阈值
                                int newCount = compasreResult(map, dto, warningRecordDto, count, key);
                                System.out.println("newCount:"+newCount);
                                if ( newCount > count){
                                    resultIdList.add(serverPo.getId().toString());
                                    resultNameList.add(key);
                                    count = newCount;
                                }else{
                                    updateSuccList.add(serverPo.getId().toString());
                                }
                                flag = true;
                                break;
                            }
                        }
//                        // 遍历完没有找到该对象的监控记录
                        if (!flag) {
                            count++;
                            String oldKey = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningObject()) ? warningRecordDto.getWarningObject() + "、" : "";
                            String description = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningDescription()) ? warningRecordDto.getWarningDescription() + "、" : "";
                            // 设置记录dto
                            warningRecordDto.setWarningObject(oldKey + key);
                            warningRecordDto.setWarningDescription(description + "无法获取" + key + "的监控记录");

                        }
                    }
                }
                //是数据库类型
                if (UsualUtil.objEquals(DATABASE, dto.getTarget())) {
                    //将得到json数据转换为一个json对象
                    for (DatabasePo databasePo : databasePos ) {
                        String ipPort = databasePo.getIpv4() + ":" + databasePo.getPort();
                        String databaseName = ipPort+"/"+databasePo.getDatabaseName();
                        //  预警对象是否有标志符
                        Boolean flag = false;
                        // 判断监控的ip+端口是否有记录
                        for (Map<String, Object> map : list) {
                            String address = (String) map.get("name");
                            // ip:端口地址 是否匹配

                            if (address.indexOf(ipPort) >= 0) {
                                // 比对此记录的是否超出阈值
                                int newCount = compasreResult(map, dto, warningRecordDto, count, databaseName);
                                if ( newCount > count){
                                    resultIdList.add(databasePo.getId().toString());
                                    resultNameList.add(databaseName);
                                    count = newCount;
                                }else{
                                    updateSuccList.add(databasePo.getId().toString());
                                }
                                flag = true;
                                break;
                            }
                        }
                        // 遍历完没有找到该对象的监控记录
                        if (!flag) {
                            count++;
                            String oldKey = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningObject()) ? warningRecordDto.getWarningObject() + "、" : "";
                            String description = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningDescription()) ? warningRecordDto.getWarningDescription() + "、" : "";
                            // 设置记录dto

                            warningRecordDto.setWarningObject(oldKey + databaseName);
                            warningRecordDto.setWarningDescription(description + "无法获取" + databaseName + "的该指标监控记录");
                        }
                    }
                }
                //是服务类型
                if (UsualUtil.objEquals(SERVICE, dto.getTarget())) {
                    //将得到json数据转换为一个json对象
                    for (ServicePo servicePo : servicePos) {
                        String key = servicePo.getEnglishName();
                        //  预警对象是否有标志符
                        Boolean flag = false;
                        // 判断监控的ip是否有记录
                        for (Map<String, Object> map : list) {
                            String serviceName = map.containsKey("service.name") ? (String) map.get("service.name"):(String) map.get("name");
                            // 目标ip 是否在此记录ip池里
                            if (UsualUtil.objEquals(key, serviceName)) {
                                // 比对此记录的是否超出阈值
                                int newCount = compasreResult(map, dto, warningRecordDto, count, key);
                                if ( newCount > count){
                                    resultIdList.add(servicePo.getId().toString());
                                    resultNameList.add(key);
                                    count = newCount;
                                }else{
                                    updateSuccList.add(servicePo.getId().toString());
                                }
                                flag = true;
                                break;
                            }
                        }
                        // 遍历完没有找到该对象的监控记录
                        if (!flag) {
                            count++;
                            String oldKey = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningObject()) ? warningRecordDto.getWarningObject() + "、" : "";
                            String description = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningDescription()) ? warningRecordDto.getWarningDescription() + "、" : "";
                            // 设置记录dto
                            warningRecordDto.setWarningObject(oldKey + key);
                            warningRecordDto.setWarningDescription(description + "无法获取" + key + "的该指标监控记录");
                        }
                    }
                }
                //是应用类型
                if (UsualUtil.objEquals(APPLICATION, dto.getTarget())) {
                    //// 循环遍历监控的应用是否有elk记录
                    for (ApplicationPo applicationPo : applicationPos) {
                        String key = applicationPo.getEnglishName();
                        String h_url = applicationPo.getHealthMonitoringUrl();
                        //  预警对象是否有标志符
                        Boolean flag = false;
                        for (Map<String, Object> map : list) {
                            String id = (String) map.get("monitor.id");
                            // 跟据 id 比对唯一
                            if (UsualUtil.objEquals(key, id)) {
                                // 比对此记录的是否超出阈值
                                int newCount = compasreResult(map, dto, warningRecordDto, count, key);
                                if ( newCount > count){
                                    resultIdList.add(applicationPo.getId().toString());
                                    resultNameList.add(key);
                                    count = newCount;
                                }else{
                                    updateSuccList.add(applicationPo.getId().toString());
                                }
                                flag = true;
                                break;
                            }
                        }
                        // 遍历完没有找到该对象的监控记录
                        if (!flag) {
                            count++;
                            String oldKey = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningObject()) ? warningRecordDto.getWarningObject() + "、" : "";
                            String description = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningDescription()) ? warningRecordDto.getWarningDescription() + "、" : "";
                            // 设置记录dto
                            warningRecordDto.setWarningObject(oldKey + key);
                            warningRecordDto.setWarningDescription(description + "无法获取" + key + "的监控记录");
                        }
                    }
                }
            } else {
                count++;
                // 设置记录dto
                warningRecordDto.setWarningObject("null");
                warningRecordDto.setWarningDescription("从elk中无法获取该指标监控数据");
            }


        }


        //根据重复次数发送告警规则
        // 计算这次任务是否发送阈值,没发生则清空连续记录数
        record = count > 0 ? record + 1 : 0;
        int reportNum = Integer.parseInt(dto.getRepeatValue());
        if (UsualUtil.objEquals(">", dto.getRepeatOperators())) {
            if (record > reportNum) {
                // 超出次数
                // 设置告警记录
                String targetName = warningRuleMapper.getTargetNameById(Long.parseLong(dto.getType()));
                warningRecordDto.setExceptionType(targetName);
                warningRecordDto.setRuleId(dto.getId().toString());
                warningRecordDto.setWarningLevel(dto.getWarningLevel());
                warningRecordDto.setTarget(dto.getTarget());
                warningRecordDto.setWarningObjectIds(resultIdList);
                String realityWarningObject = resultNameList.stream().collect(Collectors.joining("、"));
                warningRecordDto.setRealityWarningObject(realityWarningObject);
                String[] splits =warningRecordDto.getWarningDescription().split("、");
                warningRecordDto.setRecordType("0");
                for (String s : splits
                ) {
                    if (s.indexOf("无法获取") == -1) {
                        warningRecordDto.setRecordType("1");
                    }
                }
                try {
                    warningRecordDto.setWarningTime(DateUtil.getCurrentDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println("规则:"+dto.getName()+"record超出" + record + "条件的" + reportNum+" 生成告警记录中..");
                //创建发送，清空record
                warningRecordService.create(warningRecordDto);
                record = 0;
            }
        }
        if (UsualUtil.objEquals(">=", dto.getRepeatOperators())) {
            if (record >= reportNum) {
                // 超出次数， 发送告警信息
                // 设置告警记录
                String targetName = warningRuleMapper.getTargetNameById(Long.parseLong(dto.getType()));
                warningRecordDto.setExceptionType(targetName);
                warningRecordDto.setRuleId(dto.getId().toString());
                warningRecordDto.setWarningLevel(dto.getWarningLevel());
                warningRecordDto.setTarget(dto.getTarget());
                warningRecordDto.setWarningObjectIds(resultIdList);
                String realityWarningObject = resultNameList.stream().collect(Collectors.joining("、"));
                warningRecordDto.setRealityWarningObject(realityWarningObject);
                String[] splits =warningRecordDto.getWarningDescription().split("、");
                warningRecordDto.setRecordType("0");
                for (String s : splits
                ) {
                    if (s.indexOf("无法获取") == -1) {
                        warningRecordDto.setRecordType("1");
                    }
                }
                try {
                    warningRecordDto.setWarningTime(DateUtil.getCurrentDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println("规则:"+dto.getName()+"record超出" + record+ "条件的" + reportNum +" 生成告警记录中..");
                //创建发送，清空record
                warningRecordService.create(warningRecordDto);
                record = 0;
            }
        }
        System.out.println("规则:"+dto.getName()+"超出阈值记录数"+record);
        updateStatus(resultIdList,updateSuccList,dto.getType());
        context.getJobDetail().getJobDataMap().put("record", record);
    }

















    */
/**
     * 根据dto设置 遍历查找elk的记录map 判断是否超出阈值
     *
     * @param map es记录的集合
     * @param dto
     * @param warningRecordDto 结果放进这个记录dto中
     * @param count            本次任务中超出阈值的目标数量
     * @param key              预警目标
     * @return
     *//*

    public int compasreResult(Map<String, Object> map, WarningRuleDto dto, WarningRecordDto warningRecordDto, int count, String key) {
        // 初始化当前运行记录超出阈值数

        String op = dto.getThresholdOperators1();
        String delayThroughputRelator = dto.getDelayThroughputRelator();
        String delayOperators = dto.getDelayOperators();
        String throughputOperators = dto.getThroughputOperators();

        String val = dto.getThreshold1();
        String delay = dto.getDelay();
        String throughput = dto.getThroughput();

        String oldKey = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningObject()) ? warningRecordDto.getWarningObject() + "、" : "";
        String description = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningDescription()) ? warningRecordDto.getWarningDescription() + "、" : "";
        // 如果存在上条获取上条记录的
        if (UsualUtil.objEquals("==", op)) {
            if (map.containsKey("status")) {
                if (UsualUtil.objEquals(map.get("status"), val)) {
                    // 满足阈值条件，记录  + 1
                    count++;
                    warningRecordDto.setWarningObject(oldKey + key);
                    warningRecordDto.setWarningDescription(description + key + "原始值" + "等于" + val + ",当前数据:"+ map.get("status") );
                }
            } else {
                //告警记录一次 该类型获取不到结果
                count++;
                warningRecordDto.setWarningObject(oldKey + key);
                warningRecordDto.setWarningDescription(description + "无法获取到状态");
            }
        } else if (UsualUtil.objEquals("!=", op)) {
            if (map.containsKey("status")) {
                if (!UsualUtil.objEquals(map.get("status"), val)) {
                    // 满足阈值条件，记录  + 1
                    count++;
                    warningRecordDto.setWarningObject(oldKey + key);
                    warningRecordDto.setWarningDescription(description + key + "原始值" + "不等于" + val+",当前数据:"+ map.get("status"));
                }
            } else {
                //告警记录一次 该类型获取不到结果
                count++;
                warningRecordDto.setWarningObject(oldKey + key);
                warningRecordDto.setWarningDescription(description + "无法获取该指标的状态（status）");
            }
        } else {
            double th = UsualUtil.objEquals("%", dto.getThresholdUnit1()) ?
                    Double.parseDouble(val) / 100 : Double.parseDouble(val);
            // 从结果map中取出pct 使用率 的结果
            if (map.containsKey("pct")) {
                Double pct = Double.parseDouble((String) map.get("pct"));
                if (UsualUtil.objEquals(">=", op)) {
                    if (pct > th || new BigDecimal(pct).compareTo(new BigDecimal(th)) == 0){
                        count++;
                        warningRecordDto.setWarningObject(oldKey + key);
                        String des = key + "原始值" + ">=" + val + "%"+",当前数据:"
                                +  DoubleUtils.getPercentFormat(Double.parseDouble((String) map.get("pct")),3,3);
                        warningRecordDto.setWarningDescription(description + des);
                    }
                }
                if (UsualUtil.objEquals(">", op)) {
                    if (pct > th){
                        count++;
                        warningRecordDto.setWarningObject(oldKey + key);
                        warningRecordDto.setWarningDescription(description + key + "原始值" + ">" + val + "%"+",当前数据:"
                                +  DoubleUtils.getPercentFormat(Double.parseDouble((String) map.get("pct")),3,3));
                        System.out.println("dec:"+warningRecordDto.getWarningDescription());
                    }
                }
            } else if (map.containsKey("num")) {
                Double num = Double.parseDouble((String) map.get("num"));
                if (UsualUtil.objEquals(">=", op)) {
                    if(num >= th){
                        count++;
                        warningRecordDto.setWarningObject(oldKey + key);
                        String des = key + "原始值" + ">=" + val +",当前数据:"+map.get("num");
                        warningRecordDto.setWarningDescription(description + des);
                    }

                }
                if (UsualUtil.objEquals(">", op)) {
                    if(num > th) {
                        count++;
                        warningRecordDto.setWarningObject(oldKey + key);
                        String des = key + "原始值" + ">" + val+",当前数据:"+map.get("num");
                        warningRecordDto.setWarningDescription(description + des);
                    }
                }
            } else {
                // 没结果 记录+1
                count++;
                warningRecordDto.setWarningObject(oldKey + key);
                warningRecordDto.setWarningDescription(description + key + "无法获取到结果（num、pct）");
            }
        }

        return count;

    }

    */
/**
     * 指标是服务器\数据库\服务\应用 状态检测的时候,将检测结果同步到资源管理里面
     *

     *//*

    public void updateStatus(List<String> resultIdList,List<String> updateSuccList, String type) {
        // 通过是那种指标执行不同的更新
        // 主机\服务器\服务\应用状态需要同步到资源管理的状态

        if (StringUtils.equals("1001", type)) {
            // 记录在告警目标里面的设置为异常
            if (UsualUtil.collIsNotEmpty(resultIdList)){
                serverMapper.updateStatusErr(resultIdList);
            }
            // 没有在告警目标里面 且属于监控范围的设置则为正常
            if (UsualUtil.collIsNotEmpty(updateSuccList)){
                serverMapper.updateStatusSucc(updateSuccList);
            }

        }
        // 如果是数据库状态检测指标id 2005
        if (StringUtils.equals("2005", type)) {
            // 记录在告警目标里面的设置为异常
            if (UsualUtil.collIsNotEmpty(resultIdList)){
                databaseMapper.updateStatusErr(resultIdList);
            }
            // 没有在告警目标里面 且属于监控范围的设置则为正常
            if (UsualUtil.collIsNotEmpty(updateSuccList)){
                databaseMapper.updateStatusSucc(updateSuccList);
            }
        }
        if (StringUtils.equals("3004", type)) {
            // 记录在告警目标里面的设置为异常
            if (UsualUtil.collIsNotEmpty(resultIdList)){
                serviceMapper.updateStatusErr(resultIdList);
            }
            // 没有在告警目标里面 且属于监控范围的设置则为正常
            if (UsualUtil.collIsNotEmpty(updateSuccList)){
                serviceMapper.updateStatusSucc(updateSuccList);
            }
        }
        if (StringUtils.equals("4001", type)) {
            // 记录在告警目标里面的设置为异常
            if (UsualUtil.collIsNotEmpty(resultIdList)){
                applicationMapper.updateStatusErr(resultIdList);
            }
            // 没有在告警目标里面 且属于监控范围的设置则为正常
            if (UsualUtil.collIsNotEmpty(updateSuccList)){
                applicationMapper.updateStatusSucc(updateSuccList);
            }
        }

    }
*/

}



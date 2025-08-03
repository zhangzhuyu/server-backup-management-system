package com.ly.cloud.quartz.task;

import org.quartz.*;

/**
 * @Author ljb
 * @Date 2022/5/31
 */

//@PersistJobDataAfterExecution
//@DisallowConcurrentExecution
//@TaskNode(taskName = "定时监控服务接口错误次数警告")
public class ErrorTimesAlarmTask implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }

    /*private static final Logger logger = LoggerFactory.getLogger(ErrorTimesAlarmTask.class);

    *//**
     * 运行告警记录数
     **//*
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
//        // 告警记录dto
//        WarningRecordDto warningRecordDto = new WarningRecordDto();

        // 获取任务数据
        JobDataMap dataMap = context.getMergedJobDataMap();
        WarningRuleDto dto = (WarningRuleDto) dataMap.get("dto");

        // 错误次数记录
        Map<String, Map<String, Object>> errorRecord = new HashMap<>();

        record = dataMap.getIntValue("record");
        // 本次任务连续失败或超出阈值的记录数

        int count = 0;


        // 获取需要监控的目标list
        List<String> objectList = dto.getEarlyWarningHostList();
        String target = dto.getTarget();
        // 需要告警的ids
        List<String> serviceErrorList = new ArrayList();
        // 全部的ids ,下面更新状态时候用
        List<String> updateSuccList = new ArrayList<>();
        List<ServerPo> serverPos = new ArrayList<>();
        List<DatabasePo> databasePos = new ArrayList<>();
        List<ServicePo> servicePos = new ArrayList<>();
        List<ApplicationPo> applicationPos = new ArrayList<>();

        if (StringUtils.equals(SERVICE, target)) {
            //是否选择全部
            if (objectList.contains("-1")) {
                //根据不同指标获取其全部内容
                servicePos = serviceMapper.selectList(null);
            } else {
                servicePos = serviceMapper.selectBatchIds(objectList);
            }
        }

        String redisCacheResult = (String) redisUtil.get(REDIS_KEY_PREFIX_TC_TARGET_MODULE + dto.getType() + ":");
        // 获取错误历史记录
        errorRecord = JSONObject.parseObject(redisCacheResult, Map.class);

        if (errorRecord == null) {
            errorRecord = new HashMap<>();
        }

        // 通过target获取 redis结果，没有缓存则查询
        if (UsualUtil.objNotNull(dto.getType())) {
            // es查询出来的结果
            List<Map<String, Object>> list;
            // 查询ESsql
            String sqlLine = warningRuleMapper.getSqlLineById(Long.parseLong(dto.getType()));

            int gapValue = Integer.parseInt(dto.getGapValue());
            String timeUnit = "";
            if ("h".equals(dto.getGapValueUnit())) {
                gapValue = Integer.parseInt(dto.getGapValue()) * 60;
                timeUnit = "MINUTE";
            } else if ("min".equals(dto.getGapValueUnit())) {
                timeUnit = "MINUTE";
            } else if ("day".equals(dto.getGapValueUnit())) {
                timeUnit = "DAY";
            }
            // 替换
            sqlLine=sqlLine.replace("'^'", " " + gapValue + " ").replace("$$", " " + timeUnit + " ");
//            sqlLine = "{\"query\": \"select service.name serviceName, histogram(\\\"@timestamp\\\",interval  5   DAY ) as timeFrame,count(1) errorNumber, last(\\\"@timestamp\\\",timestamp.us) timestamp, last(error.grouping_name,timestamp.us) errorName,last(error.culprit,timestamp.us) errorCulprit from \\\"apm-*-error\\\" where \\\"@timestamp\\\" >= CURRENT_TIMESTAMP() - INTERVAL  5   DAY  group by serviceName,timeFrame order by timeFrame,errorNumber desc  \",\"time_zone\":\"Asia/Shanghai\"}";
//            System.out.println("******************************\n\n" + gapValue + "\n\n" + timeUnit + "\n*************************************");
            logger.info(sqlLine);
            list = ElasticUtil.sql(sqlLine, restHighLevelClient);

            // es查询记录聚合
            Map<String, List<Map<String, Object>>> esRecordList = new HashMap<>();
            for (Map<String, Object> item : list) {
                String serviceName = ((String) item.get("serviceName")).toLowerCase();
                List<Map<String, Object>> listMap = esRecordList.get(serviceName);
                if (listMap == null) {
                    listMap = new ArrayList<>();
                }
                listMap.add(item);
                esRecordList.put(serviceName, listMap);
            }

            // 能从elk或缓存读到对象的指标监控记录
            if (UsualUtil.collIsNotEmpty(list)) {
                // 重复次数
                int reportNum = Integer.parseInt(dto.getRepeatValue());

                //判断是那种类型
                //是服务类型
                if (UsualUtil.objEquals(SERVICE, dto.getTarget())) {
                    //将得到json数据转换为一个json对象
                    for (ServicePo servicePo : servicePos) {
                        // 告警记录dto
                        WarningRecordDto warningRecordDto = new WarningRecordDto();
                        String oldKeyName = servicePo.getEnglishName();

                        // 查询服务状态
                        String serverStart = "select \\\"@timestamp\\\",monitor.status status,monitor.id name from \\\"heartbeat-*\\\" where monitor.type='tcp'  and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '2' MINUTE and LCASE(monitor.id) ='" + oldKeyName.toLowerCase() + "' order by \\\"@timestamp\\\" desc  limit 1\" ,\"time_zone\":\"Asia/Shanghai ";
                        logger.info(serverStart);
                        List<Map<String, Object>> serverStartList = ElasticUtil.sql(serverStart, restHighLevelClient);
                        if (serverStartList != null && serverStartList.size() > 0) {
                            Map<String, Object> map = serverStartList.get(0);
                            if (!StringUtils.equals("up", (String) map.get("status"))) {
                                continue;
                            }
                        }

                        String key = servicePo.getEnglishName().toLowerCase();
                        //  预警对象是否有 标志符
                        Boolean flag = false;
                        // 判断监控的服务是否有记录
                        if (esRecordList.containsKey(key)) {
                            // 取出本次查询，该服务下的所有接口错误记录
                            List<Map<String, Object>> errList = esRecordList.get(key);
                            // 存储在redis的接口错误超出阈值历史记录
                            Map<String, Object> redisInterfaceErr = errorRecord.get(key);
                            if (redisInterfaceErr == null) {
                                redisInterfaceErr = new HashMap<>();
                            }

                            // 本次查询有记录的记录
                            Map<String, Object> succeedRecord = new HashMap<>();
                            warningRecordDto.setWarningObject(oldKeyName + "、");
                            // 该服务下本此记录的是否超出阈值的接口数量
                            int newCount = 1;
                            for (Map<String, Object> map : errList) {
                                // 查询结果是否超出阀值，记录存在历史记录中
                                newCount = compasreResult(map, dto, warningRecordDto, redisInterfaceErr, key, succeedRecord, newCount);
                            }
                            // 判断当前记录中有没有超出阀值的记录,是否需要发送警告
                            pdSendWarning(dto, warningRecordDto, String.valueOf(servicePo.getId()));

                            serviceErrorList.add(String.valueOf(servicePo.getId()));
                            // 把结果存进原来在redis的记录里，覆盖原来的值
                            errorRecord.put(oldKeyName, succeedRecord);
                        } else {
                            // 如果本次查询该服务不存在错误，则直接删除
                            errorRecord.remove(oldKeyName);
                            updateSuccList.add(servicePo.getId().toString());
                        }
//
                    }
                }
                // 更改服务状态
                updateStatus(serviceErrorList, updateSuccList, "3004");

                String errorString = JSONObject.toJSONString(errorRecord);

                // 将记录存进redis中
                redisUtil.set(REDIS_KEY_PREFIX_TC_TARGET_MODULE + dto.getType() + ":", errorString, 30000);

            }


        }


        //根据重复次数发送告警规则
        // 计算这次任务是否发送阈值,没发生则清空连续记录数

    }

    *//**
     * 判断该服务的接口有没有超出阈值需要进行发送警告的
     *//*
    public void pdSendWarning(WarningRuleDto dto, WarningRecordDto warningRecordDto, String serviceId) {

        List serviceIds = new ArrayList();
        serviceIds.add(serviceId);

        // 超出次数
        // 设置告警记录
        String targetName = warningRuleMapper.getTargetNameById(Long.parseLong(dto.getType()));
        warningRecordDto.setExceptionType(targetName);
        warningRecordDto.setRuleId(dto.getId().toString());
        warningRecordDto.setWarningLevel(dto.getWarningLevel());
        warningRecordDto.setTarget(dto.getTarget());
        warningRecordDto.setType(dto.getType());
        warningRecordDto.setWarningObjectIds(serviceIds);
        String[] splits = warningRecordDto.getWarningDescription().split("、");
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
//        System.out.println("规则:" + dto.getName() + "record超出" + record + "条件的" + reportNum + " 生成告警记录中..");
        //创建发送，清空record
        warningRecordService.create(warningRecordDto);

//        System.out.println("规则:" + dto.getName() + "超出阈值记录数" + record);

    }


    *//**
     * 根据dto设置 遍历查找elk的记录map 判断是否超出阈值
     *
     * @param map               es记录的集合
     * @param dto               告警规则类
     * @param warningRecordDto  结果放进这个记录dto中
     * @param redisInterfaceErr 任务中超出阈值的目标历史记录
     * @param key               预警服务目标
     * @param succeedRecord     本次查询有记录但没有超出阀值，该保存在redis中的记录
     * @param newCount          本次查询超出历史记录的数量
     * @return
     *//*
    public int compasreResult(Map<String, Object> map, WarningRuleDto dto, WarningRecordDto warningRecordDto, Map<String, Object> redisInterfaceErr, String key, Map<String, Object> succeedRecord, int newCount) {
        // 初始化当前运行记录超出阈值数

        // 错误接口名称 (去掉前后空格)
        String interfaceErrorName = ((String) map.get("errorName")).trim();

        String op = dto.getThresholdOperators1();
        String val = dto.getThreshold1();
        String oldKey = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningInterface()) ? warningRecordDto.getWarningInterface() + "、" : "";
        String description = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningDescription()) ? warningRecordDto.getWarningDescription() + "、" : "";
        String markdownDescription = UsualUtil.strIsNotEmpty(warningRecordDto.getWarningDescriptionMarkdown()) ? warningRecordDto.getWarningDescriptionMarkdown() + "、" : "";

        // 如果存在上条获取上条记录的
        if (map.containsKey("errorNumber")) {
            Double num = Double.parseDouble((String) map.get("errorNumber"));
            if (UsualUtil.objEquals(">=", op)) {
                if (num >= Double.parseDouble(val)) {

                    // 先判断错误历史记录中该接口错误是否存在
                    if (redisInterfaceErr.containsKey(interfaceErrorName)) {
                        int oldErrorCount = Integer.parseInt((String) redisInterfaceErr.get(interfaceErrorName));
                        redisInterfaceErr.put(interfaceErrorName, String.valueOf(oldErrorCount + 1));
                    } else {
                        redisInterfaceErr.put(interfaceErrorName, "1");
                    }

//                    warningRecordDto.setWarningInterface(oldKey + interfaceErrorName);
//                    String des = key + "服务下的 " + interfaceErrorName + "错误发生次数,原始值" + ">=" + val + ",当前数据:" + map.get("errorNumber")
//                            + ",错误描述:" + map.get("errorCulprit") + " ,发生时间:" + map.get("timestamp");
//                    warningRecordDto.setWarningDescription(description + des);

                    // 放进超出阈值记录中
                    succeedRecord.put(interfaceErrorName, redisInterfaceErr.get(interfaceErrorName));
                }

            } else if (UsualUtil.objEquals(">", op)) {
                if (num > Double.parseDouble(val)) {
                    // 先判断错误历史记录中该接口错误是否存在
                    if (redisInterfaceErr.containsKey(interfaceErrorName)) {
                        int oldErrorCount = Integer.parseInt((String) redisInterfaceErr.get(interfaceErrorName));
                        redisInterfaceErr.put(interfaceErrorName, String.valueOf(oldErrorCount + 1));
                    } else {
                        redisInterfaceErr.put(interfaceErrorName, "1");
                    }

                    // 放进超出阈值记录中
                    succeedRecord.put(interfaceErrorName, redisInterfaceErr.get(interfaceErrorName));
                }
            }

            // 重复次数
            int reportNum = Integer.parseInt(dto.getRepeatValue());
            int errorNum = Integer.parseInt((String) succeedRecord.get(interfaceErrorName));
            String timestamp = "";
            try {
                timestamp = DateUtil.getIsoToSimpleDateFormat((String) map.get("timestamp")); // 时间
            } catch (Exception e) {
                logger.error("服务接口错误预警的转换时间错误");
            }
            if (UsualUtil.objEquals(">=", dto.getRepeatOperators())) {
                if (errorNum >= reportNum) {
                    warningRecordDto.setWarningInterface(oldKey + interfaceErrorName);
                    String des = "" + key + "服务下的 " + interfaceErrorName + "错误发生次数,原始值" + ">=" + val + ",当前数据:" + map.get("errorNumber")
                            + ",错误描述:" + map.get("errorCulprit") + " ,发生时间:" + timestamp + " ,总共超出阈值次数:" + errorNum + "; ";

                    String markdownDes = newCount + ". " + key + "服务下的 " + interfaceErrorName + "错误发生次数,原始值" + ">=" + val + ",当前数据:" + map.get("errorNumber")
                            + ",错误描述:" + map.get("errorCulprit") + " ,发生时间:" + timestamp + " ,总共超出阈值次数:" + errorNum + "; \n";

                    warningRecordDto.setWarningDescription(description + des);
                    warningRecordDto.setWarningDescriptionMarkdown(markdownDescription + markdownDes);

                    // 超出阀值，则删除该记录，因为本次准备发送，不再需要存入redis中  qw
                    succeedRecord.remove(interfaceErrorName);
                    newCount++;
                }
            } else if (UsualUtil.objEquals(">", dto.getRepeatOperators())) {
                if (errorNum > reportNum) {
                    warningRecordDto.setWarningInterface(oldKey + interfaceErrorName);
                    String des = key + "服务下的 " + interfaceErrorName + "错误发生次数,原始值" + ">=" + val + ",当前数据:" + map.get("errorNumber")
                            + ",错误描述:" + map.get("errorCulprit") + " ,发生时间:" + timestamp + " ,总共超出阈值次数:" + errorNum + "; ";
                    String markdownDes = newCount + ". " + key + "服务下的 " + interfaceErrorName + "错误发生次数,原始值" + ">=" + val + ",当前数据:" + map.get("errorNumber")
                            + ",错误描述:" + map.get("errorCulprit") + " ,发生时间:" + timestamp + " ,总共超出阈值次数:" + errorNum + "; \n";

                    warningRecordDto.setWarningDescription(description + des);
                    warningRecordDto.setWarningDescriptionMarkdown(markdownDescription + markdownDes);

                    // 超出阀值，则删除该记录，因为本次准备发送，不再需要存入redis中
                    succeedRecord.remove(interfaceErrorName);
                    newCount++;
                }
            }
        }
        return newCount;
    }

    *//**
     * 指标是服务器\数据库\服务\应用 状态检测的时候,将检测结果同步到资源管理里面
     *//*
    public void updateStatus(List<String> resultIdList, List<String> updateSuccList, String type) {
        // 通过是那种指标执行不同的更新
        // 主机\服务器\服务\应用状态需要同步到资源管理的状态

        if (StringUtils.equals("1001", type)) {
            // 记录在告警目标里面的设置为异常
            if (UsualUtil.collIsNotEmpty(resultIdList)) {
                serverMapper.updateStatusErr(resultIdList);
            }
            // 没有在告警目标里面 且属于监控范围的设置则为正常
            if (UsualUtil.collIsNotEmpty(updateSuccList)) {
                serverMapper.updateStatusSucc(updateSuccList);
            }

        }
        // 如果是数据库状态检测指标id 2005
        if (StringUtils.equals("2005", type)) {
            // 记录在告警目标里面的设置为异常
            if (UsualUtil.collIsNotEmpty(resultIdList)) {
                databaseMapper.updateStatusErr(resultIdList);
            }
            // 没有在告警目标里面 且属于监控范围的设置则为正常
            if (UsualUtil.collIsNotEmpty(updateSuccList)) {
                databaseMapper.updateStatusSucc(updateSuccList);
            }
        }
        if (StringUtils.equals("3004", type)) {
            // 记录在告警目标里面的设置为异常
            if (UsualUtil.collIsNotEmpty(resultIdList)) {
                serviceMapper.updateStatusErr(resultIdList);
            }
            // 没有在告警目标里面 且属于监控范围的设置则为正常
            if (UsualUtil.collIsNotEmpty(updateSuccList)) {
                serviceMapper.updateStatusSucc(updateSuccList);
            }
        }
        if (StringUtils.equals("4001", type)) {
            // 记录在告警目标里面的设置为异常
            if (UsualUtil.collIsNotEmpty(resultIdList)) {
                applicationMapper.updateStatusErr(resultIdList);
            }
            // 没有在告警目标里面 且属于监控范围的设置则为正常
            if (UsualUtil.collIsNotEmpty(updateSuccList)) {
                applicationMapper.updateStatusSucc(updateSuccList);
            }
        }

    }*/

}



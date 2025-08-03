package com.ly.cloud.quartz.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * @Author ljb
 * @Date 2022/5/30
 */

//@TaskNode(taskName = "定时监控指标")
public class TargetTask implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
/*    private static final Logger logger = LoggerFactory.getLogger(TargetTask.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RedisConf redisUtil;

    @Autowired
    private WarningRuleMapper warningRuleMapper;


    @Override
    public void  execute(JobExecutionContext context)   {

        try {
            List<TargetVo> list= warningRuleMapper.getTargetList();
            if (UsualUtil.collIsNotEmpty(list)){
                for (TargetVo val: list) {
                    if (UsualUtil.objIsNull(val.getSqlLine())){
                        continue;
                    }
                    String sql = val.getSqlLine().replace("\n"," ");
                    logger.info(sql);
                    List<Map<String, Object>> res = ElasticUtil.sql(sql, restHighLevelClient);
                    String jsonString = JSONObject.toJSONString(res);
                    boolean redisCacheResult = redisUtil.set(REDIS_KEY_PREFIX_TC_TARGET_MODULE + val.getId()+":", jsonString,30000);
                    System.out.println("指标："+val.getTargetName()+"  的缓存结果： "+redisCacheResult);
                }
            }
        }catch (Exception e){
                 System.out.println(e);
                logger.error(e.getMessage(), e);
                throw e;
          }


    }*/
}

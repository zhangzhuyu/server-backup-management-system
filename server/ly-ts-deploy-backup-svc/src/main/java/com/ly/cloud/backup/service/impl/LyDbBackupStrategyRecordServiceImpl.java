package com.ly.cloud.backup.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.auth.mapper.SysUserMapper;
import com.ly.cloud.auth.po.SysUser;
import com.ly.cloud.backup.common.constant.CommonConstant;
import com.ly.cloud.backup.common.enums.*;
import com.ly.cloud.backup.config.MinioConfig;
import com.ly.cloud.backup.config.RedisConf;
import com.ly.cloud.backup.dto.HttpResponseDto;
import com.ly.cloud.backup.dto.LyDbBackupStrategyRecordDto;
import com.ly.cloud.backup.dto.SaveAddOrUpdateTaskDto;
import com.ly.cloud.backup.exception.ServiceException;
import com.ly.cloud.backup.mapper.*;
import com.ly.cloud.backup.po.*;
import com.ly.cloud.backup.service.*;
import com.ly.cloud.backup.util.*;
import com.ly.cloud.backup.vo.*;
import com.ly.cloud.quartz.entity.JobAndTrigger;
import com.ly.cloud.quartz.mapper.JobAndTriggerMapper;
import com.ly.cloud.quartz.service.IJobService;
import com.ly.cloud.quartz.task.BackupStrategyTask;
import com.ly.cloud.quartz.task.CatalogueBackupTask;
import com.ly.cloud.quartz.util.BackupStrategyUtil;
import com.ly.cloud.quartz.util.CatalogueBackupUtil;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.xkzhangsan.time.cron.CronExpressionUtil;
import jcifs.smb.SmbFile;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ly.cloud.backup.common.constant.RedisConstants.REDIS_KEY_PREFIX_TC_BACKUP_EXPIRATION_TIMES;
import static com.ly.cloud.backup.common.constant.RedisConstants.REDIS_KEY_PREFIX_TC_LINK_MODULE;
import static com.ly.cloud.backup.common.constant.SystemSetupConstant.TAIER;

/**
 * 数据库备份策略信息表
 *
 * @author zhangzhuyu
 */
@Service
public class LyDbBackupStrategyRecordServiceImpl extends ServiceImpl<LyDbBackupStrategyRecordMapper,LyDbBackupStrategyRecordPo> implements LyDbBackupStrategyRecordService{

    private static final Logger logger = LoggerFactory.getLogger(LyDbBackupStrategyRecordServiceImpl.class);
//    private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    @Autowired
    private LyDbBackupStrategyRecordMapper lyDbBackupStrategyRecordMapper;

    @Autowired
    private LyDbBackupStrategyRecordService lyDbBackupStrategyRecordService;

    @Resource
    private WarningRuleMapper warningRuleMapper;

    @Resource
    private DatabaseMapper databaseMapper;

    @Autowired
    private MiddlewareMapper middlewareMapper;

    @Autowired
    public SystemSetupService systemSetupService;

//    @Autowired
//    private MiddlewareService middlewareService;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerMapper serverMapper;

    @Resource
    private JobAndTriggerMapper jobAndTriggerMapper;
    @Autowired
    private IJobService jobService;

    @Autowired
    public RedisConf redisUtil;

    @Autowired
    private LyDbBackupHistoryRecordMapper historyRecordMapper;

    @Autowired
    private BackupManagementMapper backupManagementMapper;

    @Autowired
    private DatabaseServerMapper databaseServerMapper;

    @Autowired
    private LySmSystemLicenseServerMapper lySmSystemLicenseServerMapper;

    @Autowired
    private LyDbBackupHistoryRecordService historyRecordService;

    @Autowired
    private SysUserMapper sysUserMapper;




    @Autowired
    private TransactionTemplate transactionTemplate;

/*    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;*/


    //taier储存在redis里面的token
    String redisKey = REDIS_KEY_PREFIX_TC_LINK_MODULE + "taier:" + "Cookie";

    String redisKeyBySSH = REDIS_KEY_PREFIX_TC_BACKUP_EXPIRATION_TIMES;

    @Autowired
    private RedisConf redisConf;
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClientUtils minioClientUtils;

    @Autowired
    private BackupStrategyUtil backupStrategyUtil;

    @Autowired
    private CatalogueBackupUtil catalogueBackupUtil;

    ResponseWithHttpClient response = new ResponseWithHttpClient();


    private String redisGetDataBaseList = "backup:" + "getDataBaseList:";

    private String redisGetBackupTypeList = "backup:" + "getBackupTypeList:";

    private String redisGetTaskModeList = "backup:" + "getTaskModeList:";

    private static final String ENABLE = "enable";
    private static final String DISABLE = "disable";
    private static final String JOB_GROUP_NAME = "target";
    private static final String SERVICE_JOB_GROUP_NAME = "service_target";
    private final String dollarSign = CommonConstant.DOLLAR_SIGN;
    private final String all = CommonConstant.ALL;
    private static final String enable = CommonEnums.STATUS_ENABLE.getCode().toString();
    private static final String disable = CommonEnums.STATUS_DISABLE.getCode().toString();
    private static final String SERVER = CommonEnums.TARGET_OBJECT_SERVER.getCode().toString();
    private static final String DATABASE = CommonEnums.TARGET_OBJECT_DATABASE.getCode().toString();
    private static final String SERVICE = CommonEnums.TARGET_OBJECT_SERVICE.getCode().toString();
    private static final String APPLICATION = CommonEnums.TARGET_OBJECT_APPLICATION.getCode().toString();
    private static final String MIDDLEWARE = CommonEnums.TARGET_OBJECT_MIDDLEWARE.getCode().toString();
    private static final String JOB_CLASS = "com.ly.cloud.quartz.task.WarningRuleTask";

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss");

    private static final Object object = new Object();

    /**
     * 条件查询数据库备份策略信息
     *
     * @param page    : 页码
     * @param pageSize    : 页码大小
     * @param title : 查询参数
     * @param dataSourceType : 数据库类型
     * @param backupWay : 备份方式
     * @param authDeptId
     * @author: zhangzhuyu
     * @date: 2023/4/24 3:00
     * @return: IPage<LyDbBackupStrategyRecordVo> : 数据库备份策略信息
     */
    @Override
    public IPage<LyDbBackupStrategyRecordVo> selectPageLike(Integer page, Integer pageSize, String title, List<String> dataSourceType, List<String> backupWay, List<String> totalMethod, String authDeptId) {

        IPage<LyDbBackupStrategyRecordPo> iPage = null;
        IPage<LyDbBackupStrategyRecordVo> iPage1 = new Page<>(page > 0 ? page : 1, pageSize > 0 ? pageSize : 20);
        List<LyDbBackupStrategyRecordVo> list1 = new ArrayList<>();
        try {
            IPage<LyDbBackupStrategyRecordPo> page1 = new Page<>(page,pageSize);

            LambdaQueryWrapper<LyDbBackupStrategyRecordPo> lyDbBackupStrategyRecordPoLambdaQueryWrapper = new LambdaQueryWrapper<LyDbBackupStrategyRecordPo>()
                    .like(!StringUtils.isEmpty(title), LyDbBackupStrategyRecordPo::getTitle, title)
                    .in(!CollectionUtils.isEmpty(dataSourceType), LyDbBackupStrategyRecordPo::getDataSourceType, dataSourceType)
                    .in(!CollectionUtils.isEmpty(backupWay), LyDbBackupStrategyRecordPo::getBackupWay, backupWay)
                    .in(!CollectionUtils.isEmpty(totalMethod), LyDbBackupStrategyRecordPo::getTotalMethod, totalMethod)
                   ;

            //2023-11-30 增加按部门隔离资源
            if (!StringUtils.isEmpty(UsualUtil.getString(authDeptId))){
                lyDbBackupStrategyRecordPoLambdaQueryWrapper.apply("( auth_dept_id regexp '" + authDeptId + "' or auth_dept_id is null )");
            }
            lyDbBackupStrategyRecordPoLambdaQueryWrapper.orderByDesc(LyDbBackupStrategyRecordPo::getBackupTime);

            iPage = lyDbBackupStrategyRecordMapper.selectPage(page1,lyDbBackupStrategyRecordPoLambdaQueryWrapper);

            List<LyDbBackupStrategyRecordPo> list = iPage.getRecords();
            //转化
            for (int i = 0; i < list.size(); i++) {
                LyDbBackupStrategyRecordVo recordVo = new LyDbBackupStrategyRecordVo();
                BeanUtils.copyProperties(list.get(i),recordVo);

                if (list.get(i).getUrl() != null) {
                    List<String> l1 = getTablesList(list.get(i).getUrl());
                    recordVo.setUrl(l1);
                }

                if (list.get(i).getTables() != null) {
                    List<String> l2 = getTablesList(list.get(i).getTables());
                    recordVo.setTables(l2);
                }

                if (list.get(i).getBackupTarget() != null) {
                    List<String> l3 = getTablesList(list.get(i).getBackupTarget());
                    recordVo.setBackupTarget(l3);
                }

                if ("".equals(list.get(i).getTables())) {
                    recordVo.setTables(null);
                }

                //查询历史表最新百分比
//                if (list.get(i).getId() != null) {
//                    List<LyDbBackupHistoryRecordPo> historyList = historyRecordMapper.selectList(new LambdaQueryWrapper<LyDbBackupHistoryRecordPo>()
//                            .eq(LyDbBackupHistoryRecordPo::getStrategyId, list.get(i).getId())
//                            .orderByDesc(LyDbBackupHistoryRecordPo::getBackupTime));
//
//                    if (historyList != null && historyList.size()>0) {
//                        recordVo.setLastProportion(historyList.get(0).getProportion());
//                    }
//                }

                //查询最新备份状态
                if (list.get(i).getId() != null) {
                    List<LyDbBackupHistoryRecordPo> historyList = historyRecordMapper.selectList(new LambdaQueryWrapper<LyDbBackupHistoryRecordPo>()
                            .eq(LyDbBackupHistoryRecordPo::getStrategyId, list.get(i).getId())
                            .orderByDesc(LyDbBackupHistoryRecordPo::getBackupTime));

                    if (historyList != null && historyList.size()>0) {
                        recordVo.setLastStatus(historyList.get(0).getBackupStatus());
                    }
                }

                list1.add(recordVo);
            }

        }catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
        iPage1.setCurrent(iPage.getCurrent());
        iPage1.setPages(iPage.getPages());
        iPage1.setSize(iPage.getSize());
        iPage1.setTotal(iPage.getTotal());
        list1.forEach(u->u.setAuthDeptIds(DeptIdUtil.getAuthDeptIds(u.getAuthDeptId())));
        iPage1.setRecords(list1);
        //System.out.println(iPage1);
        return iPage1;
    }


    /**
     * 添加数据库备份策略信息表
     *
     * @param lyDbBackupStrategyRecordDto 数据库备份策略信息表
     * @return Boolean ：操作是否成功
     * @author: zhangzhuyu
     * @date: 2023/4/24 3:00
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(HttpServletRequest request, LyDbBackupStrategyRecordDto lyDbBackupStrategyRecordDto) {
        try {
            //检查策略名字是否相同
            List<LyDbBackupStrategyRecordPo> allList = lyDbBackupStrategyRecordMapper.selectList(new QueryWrapper<>());
            for (int i = 0; i < allList.size(); i++) {
                if (allList.get(i).getTitle()!=null && allList.get(i).getTitle().equals(lyDbBackupStrategyRecordDto.getTitle())) {
                    throw new RuntimeException("策略名称重复");
                }
            }

            if (!ObjectUtils.isEmpty(lyDbBackupStrategyRecordDto)) {
                //请求头查询用户名
                String loginuserid = request.getHeader("Loginuserid");

                lyDbBackupStrategyRecordDto.setOperatorId(loginuserid);
                //设置主键id
                Long ruleId = IdWorker.getNextId();
                LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo = new LyDbBackupStrategyRecordPo();
                BeanUtils.copyProperties(lyDbBackupStrategyRecordDto,lyDbBackupStrategyRecordPo);
                lyDbBackupStrategyRecordPo.setId(ruleId);
                lyDbBackupStrategyRecordPo.setBackupTime(new Date());
                lyDbBackupStrategyRecordPo.setOperatorId(loginuserid);
                lyDbBackupStrategyRecordPo.setEnable("2");
                if (lyDbBackupStrategyRecordPo.getTaskMode().equals(TaskModeEnums.getCode("立即"))) {
                    lyDbBackupStrategyRecordPo.setOperationTime(new Date());
                }
                //数组改字符串
                if (lyDbBackupStrategyRecordDto.getBackupTarget() != null) {
                    lyDbBackupStrategyRecordPo.setBackupTarget(getTablesString(lyDbBackupStrategyRecordDto.getBackupTarget()));
                }
                if (lyDbBackupStrategyRecordDto.getUrl() != null) {
                    lyDbBackupStrategyRecordPo.setUrl(getTablesString(lyDbBackupStrategyRecordDto.getUrl()));
                }

                if (!lyDbBackupStrategyRecordDto.getBackupWay().equals("3")) {
                    String s = "";
                    List<String> tables = lyDbBackupStrategyRecordDto.getTables();
                    if (tables!=null && !tables.equals(""))
                        for (int i = 0; i < tables.size(); i++) {
                            if (i != tables.size() - 1) {
                                s = s+tables.get(i)+";";
                            }else {
                                s = s+tables.get(i);
                            }
                        }
                    lyDbBackupStrategyRecordPo.setTables(s);
                    //添加到备份策略表`
                    setTotalMethod(lyDbBackupStrategyRecordPo);
                    if (Optional.ofNullable(lyDbBackupStrategyRecordPo).isPresent() && StringUtils.isEmpty(lyDbBackupStrategyRecordPo.getId())){
                        lyDbBackupStrategyRecordPo.setId(ruleId);
                    }

                    //检查有没有相同的cron值，如果有会造成堵塞，导致不会执行
                    //遍历出正在备份的历史对象
                    /*List<LyDbBackupHistoryRecordPo> historyRecordPoList = historyRecordMapper
                            .selectList(new LambdaQueryWrapper<LyDbBackupHistoryRecordPo>().eq(LyDbBackupHistoryRecordPo::getBackupStatus, "2"));
                    List<String> strategyIdList = historyRecordPoList.stream().map(LyDbBackupHistoryRecordPo::getStrategyId).collect(Collectors.toList());
                    List<LyDbBackupStrategyRecordPo> recordPoList = lyDbBackupStrategyRecordMapper.selectBatchIds(strategyIdList);*/

                    //遍历策略表
                    /*String cron = lyDbBackupStrategyRecordPo.getCron();
                    List<LyDbBackupStrategyRecordPo> recordPoList = lyDbBackupStrategyRecordMapper.selectList(new LambdaQueryWrapper<LyDbBackupStrategyRecordPo>()
                            .eq(LyDbBackupStrategyRecordPo::getEnable, "1")
                            .eq(LyDbBackupStrategyRecordPo::getCron, cron));
                    boolean flag = true;
                    while (flag) {
                        flag = false;
                        for (int i = 0; i < recordPoList.size(); i++) {
                            if (cron.equals(recordPoList.get(i).getCron())) {
                                //改变cron
                                String minute = cron.split(" ")[1];
                                int minuteNum = Integer.parseInt(minute) + 2;
                                cron = cron.replaceAll(minute, String.valueOf(minuteNum));
                                //重新set
                                lyDbBackupStrategyRecordPo.setCron(cron);
                                flag = true;
                            }
                        }
                    }*/

                    int insert = lyDbBackupStrategyRecordMapper.insert(lyDbBackupStrategyRecordPo);
                    System.out.println(insert);

                }else {
                    //存入数据库
                    //调用taier接口
                    SaveAddOrUpdateTaskDto saveAddOrUpdateTaskDto = new SaveAddOrUpdateTaskDto();
                    BeanUtils.copyProperties(lyDbBackupStrategyRecordDto,saveAddOrUpdateTaskDto);
                    saveAddOrUpdateTaskDto.setName(lyDbBackupStrategyRecordDto.getTitle());

//                    taierService.saveTaskAddOrUpdateTask(saveAddOrUpdateTaskDto);
                }
                //开始备份
                if (lyDbBackupStrategyRecordPo.getTaskMode().equals("1")) {
                    if ("0".equals(lyDbBackupStrategyRecordDto.getIsFirstBackup())) {
                        lyDbBackupStrategyRecordPo.setEnable("1");
                        int i = lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);
                    }else {
                        backup(String.valueOf(ruleId));
                    }
                } else if (lyDbBackupStrategyRecordPo.getTaskMode().equals("2")) {
//                    if ("0".equals(lyDbBackupStrategyRecordDto.getIsFirstBackup())) {
//                        startBackup(String.valueOf(ruleId));
//                    }else {
//                        backup(String.valueOf(ruleId));
//                        //是否启用改回2，进入定时逻辑
//                        lyDbBackupStrategyRecordPo.setEnable("2");
//                        int i = lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);
//                        startBackup(String.valueOf(ruleId));
//                    }
                    startBackup(String.valueOf(ruleId));
                }
            }

        }catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new ServiceException("新增备份策略失败："+e.getMessage());
        }

    }

    //判断totalMethod字段存放什么
    private void setTotalMethod(LyDbBackupStrategyRecordPo recordPo) {
        if (recordPo.getBackupWay().equals("1") && recordPo.getDataSourceType().equals("1") && recordPo.getExpdb().equals("0")) {
            recordPo.setTotalMethod("2");
        } else if (recordPo.getBackupWay().equals("1") && recordPo.getDataSourceType().equals("1") && recordPo.getExpdb().equals("1")) {
            recordPo.setTotalMethod("3");
        } else if (recordPo.getBackupWay().equals("1") && recordPo.getDataSourceType().equals("2")) {
            recordPo.setTotalMethod("1");
        } else if (recordPo.getBackupWay().equals("1") && recordPo.getDataSourceType().equals("6")) {
            recordPo.setTotalMethod("4");
        } else if (recordPo.getBackupWay().equals("2") && recordPo.getBackupMethod().equals("1")) {
            recordPo.setTotalMethod("5");
        } else if (recordPo.getBackupWay().equals("2") && recordPo.getBackupMethod().equals("2")) {
            recordPo.setTotalMethod("6");
        } else if (recordPo.getBackupWay().equals("2") && recordPo.getBackupMethod().equals("3")) {
            recordPo.setTotalMethod("7");
        } else if (recordPo.getBackupWay().equals("2") && recordPo.getBackupMethod().equals("4")) {
            recordPo.setTotalMethod("8");
        } else if (recordPo.getBackupWay().equals("3")) {
            recordPo.setTotalMethod("9");
        }

    }


    /**
     * 更改数据库备份策略信息表
     *
     * @param lyDbBackupStrategyRecordDto 数据库备份策略信息表
     * @return Boolean ：操作是否成功
     * @author: zhangzhuyu
     * @date: 2023/4/24 3:00
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSource(LyDbBackupStrategyRecordDto lyDbBackupStrategyRecordDto) {
        try {
            if (lyDbBackupStrategyRecordDto != null) {
                LyDbBackupStrategyRecordPo newStrategyRecordPo = new LyDbBackupStrategyRecordPo();

                LyDbBackupStrategyRecordPo oldStrategyRecordPo = lyDbBackupStrategyRecordMapper.selectById(lyDbBackupStrategyRecordDto);

                BeanUtils.copyProperties(lyDbBackupStrategyRecordDto,newStrategyRecordPo);
                //set之前原始的数值
                newStrategyRecordPo.setBackupTime(oldStrategyRecordPo.getBackupTime());
                //newStrategyRecordPo.setOperationTime(new Date());
                newStrategyRecordPo.setOperatorId(oldStrategyRecordPo.getOperatorId());
                newStrategyRecordPo.setEnable(oldStrategyRecordPo.getEnable());

                if (lyDbBackupStrategyRecordDto.getUrl()!=null) {
                    newStrategyRecordPo.setUrl(getTablesString(lyDbBackupStrategyRecordDto.getUrl()));
                }
                if (lyDbBackupStrategyRecordDto.getBackupTarget()!=null) {
                    newStrategyRecordPo.setBackupTarget(getTablesString(lyDbBackupStrategyRecordDto.getBackupTarget()));
                }
                if (lyDbBackupStrategyRecordDto.getTables()!=null) {
                    newStrategyRecordPo.setTables(getTablesString(lyDbBackupStrategyRecordDto.getTables()));
                }

                setTotalMethod(newStrategyRecordPo);
                int i = lyDbBackupStrategyRecordMapper.updateById(newStrategyRecordPo);
                if (i < 1) {
                    logger.error("更新备份信息失败");
                    throw new RuntimeException();
                }
                //如果前前后后都是定时任务的策略修改，要同步修改cron表  以及 未开始的历史表
                if (oldStrategyRecordPo.getTaskMode().equals("2") && newStrategyRecordPo.getTaskMode().equals("2")) {
                    //如果前前后后都是定时任务的策略修改，要同步修改cron表  以及 未开始的历史表
                    //int i1 = lyDbBackupStrategyRecordMapper.updateStrategyRecordById(String.valueOf(lyDbBackupStrategyRecordPo.getId()), lyDbBackupStrategyRecordPo.getCron());
                    //获取对应的数据源信息
                    DatabasePo databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>()
                            .eq(DatabasePo::getId,newStrategyRecordPo.getUrl()));
                    MiddlewarePo middlewarePo = middlewareMapper.selectOne(new LambdaQueryWrapper<MiddlewarePo>()
                            .eq(MiddlewarePo::getId,newStrategyRecordPo.getUrl()));
                    ServerPo serverPo = serverMapper.selectOne(new LambdaQueryWrapper<ServerPo>()
                            .eq(ServerPo::getId, newStrategyRecordPo.getUrl()));
                    LyDbBackupHistoryRecordPo historyRecordPo = new LyDbBackupHistoryRecordPo();
                    //查询表格
                    String tables = newStrategyRecordPo.getTables();
                    List<String> tablesList = null;
                    if (tables != null) {
                        tablesList = new ArrayList<>();
                        String[] split = tables.split(";");
                        for (int j1 = 0; j1 < split.length; j1++) {
                            tablesList.add(split[j1]);
                        }
                    }
                    //去定时框架执行更新
                    boolean b = true;
                    /*if (!oldStrategyRecordPo.getDataSourceType().equals("6")) {
                        b = insertOrUpdateAlarmTask(databasePo,null,serverPo,newStrategyRecordPo,historyRecordPo,tablesList);
                    }else {
                        b = insertOrUpdateAlarmTask(null,middlewarePo,serverPo,newStrategyRecordPo,historyRecordPo,tablesList);
                    }*/
                    b = insertOrUpdateAlarmTask(databasePo,middlewarePo,serverPo,newStrategyRecordPo,historyRecordPo,tablesList);
                    if (!b) {
                        logger.error("定时任务表的cron同步失败");
                        throw new RuntimeException("定时任务表的cron同步失败");
                    }
                    //更改未开始的历史表
                    String cron = newStrategyRecordPo.getCron();
                    if (CronExpressionUtil.isValidExpression(cron)) {
                        Date nextTime = CronExpressionUtil.getNextTime(cron);
                        int i2 = historyRecordMapper.updateBackupTime(nextTime, String.valueOf(newStrategyRecordPo.getId()));
                    }

                } else if (oldStrategyRecordPo.getTaskMode().equals("2") && newStrategyRecordPo.getTaskMode().equals("1")) {
                    //如果是定时备份修改为立即备份
                    int i1 = lyDbBackupStrategyRecordMapper.changeStrategyRecordById(String.valueOf(newStrategyRecordPo.getId()), "ERROR");
                    int i2 = lyDbBackupStrategyRecordMapper.updateById(newStrategyRecordPo);
                    if (i1 < 1) {
                        logger.error("定时备份修改为立即备份失败");
                        throw new RuntimeException();
                    }
                } else if (oldStrategyRecordPo.getTaskMode().equals("1") && newStrategyRecordPo.getTaskMode().equals("2")) {
                    //如果是立即备份修改为定时备份
                    //通过判断cron表里面是否存在主键来 判断是否是第一次修改
                    if (lyDbBackupStrategyRecordMapper.selectStrategyRecordById(String.valueOf(newStrategyRecordPo.getId())) == null) {
                        newStrategyRecordPo.setEnable("2");
                        int i1 = lyDbBackupStrategyRecordMapper.updateById(newStrategyRecordPo);
                        startBackup(String.valueOf(newStrategyRecordPo.getId()));
                    }else {
                        //获取对应的数据源信息
                        DatabasePo databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>()
                                .eq(DatabasePo::getId,newStrategyRecordPo.getUrl()));
                        MiddlewarePo middlewarePo = middlewareMapper.selectOne(new LambdaQueryWrapper<MiddlewarePo>()
                                .eq(MiddlewarePo::getId,newStrategyRecordPo.getUrl()));
                        ServerPo serverPo = serverMapper.selectOne(new LambdaQueryWrapper<ServerPo>()
                                .eq(ServerPo::getId, newStrategyRecordPo.getUrl()));
                        LyDbBackupHistoryRecordPo historyRecordPo = new LyDbBackupHistoryRecordPo();
                        //查询表格
                        String tables = newStrategyRecordPo.getTables();
                        List<String> tablesList = null;
                        if (tables != null) {
                            tablesList = new ArrayList<>();
                            String[] split = tables.split(";");
                            for (int j1 = 0; j1 < split.length; j1++) {
                                tablesList.add(split[j1]);
                            }
                        }
                        //去定时框架执行更新
                        boolean b = true;
                        /*if (!oldStrategyRecordPo.getDataSourceType().equals("6")) {
                            b = insertOrUpdateAlarmTask(databasePo,null,serverPo,newStrategyRecordPo,historyRecordPo,tablesList);
                        }else {
                            b = insertOrUpdateAlarmTask(null,middlewarePo,serverPo,newStrategyRecordPo,historyRecordPo,tablesList);
                        }*/
                        b = insertOrUpdateAlarmTask(databasePo,middlewarePo,serverPo,newStrategyRecordPo,historyRecordPo,tablesList);
                        if (!b) {
                            logger.error("定时任务表的cron同步失败");
                            throw new RuntimeException();
                        }
                        //更改未开始的历史表
                        String cron = newStrategyRecordPo.getCron();
                        if (CronExpressionUtil.isValidExpression(cron)) {
                            Date nextTime = CronExpressionUtil.getNextTime(cron);
                            int i2 = historyRecordMapper.updateBackupTime(nextTime, String.valueOf(newStrategyRecordPo.getId()));
                        }
                        //更改cron状态
                        int i1 = lyDbBackupStrategyRecordMapper.changeStrategyRecordById(String.valueOf(newStrategyRecordPo.getId()), "WAITING");
                    }
                }

            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }


    }


    /**
     * 批量删除数据库备份策略信息表信息
     *
     * @param ids : 主键集合
     * @return Boolean ：操作是否成功
     * @author: zhangzhuyu
     * @date: 2023/4/25 6:00
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByMulti(List<Long> ids) {
        try{
            if (!CollectionUtils.isEmpty(ids)) {
                List<LyDbBackupStrategyRecordPo> strategyRecordPoList = lyDbBackupStrategyRecordMapper.selectBatchIds(ids);
                List<LyDbBackupHistoryRecordPo> list = new ArrayList<>();
                for (int i = 0; i < strategyRecordPoList.size(); i++) {
                    //判断是否是定时任务，如果是则要同步修改定时框架的值
                    if (strategyRecordPoList.get(i).getTaskMode().equals("2")) {
                        int error = lyDbBackupStrategyRecordMapper.changeStrategyRecordById(String.valueOf(strategyRecordPoList.get(i).getId()), "ERROR");
                    }
                    //同步修改历史表
                    List<LyDbBackupHistoryRecordPo> historyRecordPoList = historyRecordMapper.selectList(new LambdaQueryWrapper<LyDbBackupHistoryRecordPo>()
                                    .eq(LyDbBackupHistoryRecordPo::getStrategyId, strategyRecordPoList.get(i).getId()));
                    list.addAll(historyRecordPoList);
                }
                List<Long> collect = list.stream().map(LyDbBackupHistoryRecordPo::getId).collect(Collectors.toList());
                List<Long> collect1 = list.stream().filter(historyRecordPo -> historyRecordPo.getBackupStatus().equals("4")).map(LyDbBackupHistoryRecordPo::getId).collect(Collectors.toList());
                //停止 删除的策略的备份任务
                if (collect.size() >= 1) {
                    //stopBackup(collect);
                    //更改备份状态
                    List<LyDbBackupHistoryRecordPo> historyRecordPoList = historyRecordMapper.selectBatchIds(collect);
                    for (int i = 0; i < historyRecordPoList.size(); i++) {
                        LyDbBackupHistoryRecordPo historyRecordPo = historyRecordPoList.get(i);
                        if (!historyRecordPo.getBackupStatus().equals(String.valueOf(BackupStatusEnums.getCode("备份成功"))) && !historyRecordPo.getBackupStatus().equals(String.valueOf(BackupStatusEnums.getCode("备份失败")))) {
                            historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("已停止备份")));
                        }
                        historyRecordPo.setProportion("100");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                }
                //直接 删除未开始的策略的备份任务
                if (collect1.size() >= 1) {
                    int i = historyRecordMapper.deleteBatchIds(collect1);
                }

                //批量删除id
                int i = lyDbBackupStrategyRecordMapper.deleteBatchIds(ids);

            }

        }catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }



    /*备份类型的下拉框*/
    @Override
    public List<SelectVo> getBackupTypeList() {
        if (redisConf.get(redisGetBackupTypeList) != null) {
            return (List<SelectVo>) redisConf.get(redisGetBackupTypeList);
        }
        List<Map<String, String>> list = BackupWayEnums.toList();
        //将elk获取的路径转成下拉框形式
        List<SelectVo> selectVoList = new ArrayList<>();
        if (UsualUtil.collIsNotEmpty(list)) {
            list.forEach(u->{
                SelectVo selectVo = new SelectVo();
                selectVo.setLabel(u.get("code"));
                selectVo.setValue(u.get("value"));
                selectVoList.add(selectVo);
            });
        }

        redisConf.set(redisGetDataBaseList,list,30);
        return selectVoList;
    }



    /*数据源类型的下拉框*/
    @Override
    public List<SelectVo> getDataBaseList() {
        if (redisConf.get(redisGetDataBaseList) != null) {
            return (List<SelectVo>) redisConf.get(redisGetDataBaseList);
        }
        List<Map<String, String>> list = DataBaseEnums.toList();
        //将elk获取的路径转成下拉框形式
        List<SelectVo> selectVoList = new ArrayList<>();
        if (UsualUtil.collIsNotEmpty(list)) {
            list.forEach(u->{
                SelectVo selectVo = new SelectVo();
                selectVo.setLabel(u.get("code"));
                selectVo.setValue(u.get("value"));
                selectVoList.add(selectVo);
            });
        }
        //System.out.println("selectVoList:"+selectVoList);

        redisConf.set(redisGetDataBaseList,list,30);
        return selectVoList;
    }


    /*执行模式的下拉框*/
    @Override
    public List<SelectVo> getTaskModeList() {
        if (redisConf.get(redisGetTaskModeList) != null) {
            return (List<SelectVo>) redisConf.get(redisGetTaskModeList);
        }
        List<Map<String, String>> list = TaskModeEnums.toList();
        //将elk获取的路径转成下拉框形式
        List<SelectVo> selectVoList = new ArrayList<>();
        if (UsualUtil.collIsNotEmpty(list)) {
            list.forEach(u->{
                SelectVo selectVo = new SelectVo();
                selectVo.setLabel(u.get("code"));
                selectVo.setValue(u.get("value"));
                selectVoList.add(selectVo);
            });
        }
        //System.out.println("selectVoList:"+selectVoList);

        redisConf.set(redisGetTaskModeList,list,30);
        return selectVoList;
    }


    /*备份目标url的下拉框*/
    @Override
    public List<SelectVo> getBackupTargetList(Integer sourceType,Integer backupWay, String backupMethod) {
        /*if (redisConf.get(redisGetBackupTargetList) != null) {
            return (List<SelectVo>) redisConf.get(redisGetBackupTargetList);
        }*/
        List<SelectVo> selectVoList = new ArrayList<>();
        if (backupWay == 1) {
            if (sourceType == 1) {
                //oracle
                List<DatabasePo> list = databaseService.list(new LambdaQueryWrapper<DatabasePo>().eq(sourceType!=null,DatabasePo::getSourceType,sourceType));
                //将elk获取的路径转成下拉框形式
                if (!CollectionUtils.isEmpty(list)) {
                    list.forEach(s->{
                        if (UsualUtil.collIsNotEmpty(list)) {
                            SelectVo selectVo = new SelectVo();
                            selectVo.setValue(String.valueOf(s.getId()));
                            selectVo.setTitle(s.getName());
                            selectVo.setLabel(s.getUser()+"@"+s.getIpv4()+":"+s.getPort()+"/orcl");
                            selectVoList.add(selectVo);
                        }
                    });
                }

            } else if (sourceType == 2) {
                //mysql
                List<DatabasePo> list = databaseService.list(new LambdaQueryWrapper<DatabasePo>().eq(sourceType!=null,DatabasePo::getSourceType,sourceType));
                //将elk获取的路径转成下拉框形式
                if (!CollectionUtils.isEmpty(list)) {
                    list.forEach(s->{
                        if (UsualUtil.collIsNotEmpty(list)) {
                            SelectVo selectVo = new SelectVo();
                            selectVo.setLabel(s.getUrl());
                            selectVo.setValue(String.valueOf(s.getId()));
                            selectVo.setTitle(s.getName());
                            selectVoList.add(selectVo);
                        }
                    });
                }
            }else if (sourceType == 6){
                //mongodb在另外一张表
                //String url = "mongodb://admin:37621040@192.168.35.152:27017";
//                String mongo = "mongodb://";
//                List<MiddlewarePo> list1 = middlewareService.list(new LambdaQueryWrapper<MiddlewarePo>().eq(sourceType != null, MiddlewarePo::getMiddlewareType, "2"));
//                if (!CollectionUtils.isEmpty(list1)) {
//                    list1.forEach(l->{
//                        String s = mongo + l.getUser() + ":" + l.getPassword() + "@" + l.getIp() + ":" + l.getPort();
//                        SelectVo selectVo = new SelectVo();
//                        selectVo.setLabel(s);
//                        selectVo.setValue(String.valueOf(l.getId()));
//                        selectVo.setTitle(l.getName());
//                        selectVoList.add(selectVo);
//                    });
//                }

            }
        } else if (backupWay == 2) {
            //ssh备份
            if (backupMethod.equals("2")) {
                List<ServerPo> list = serverService.list(new LambdaQueryWrapper<ServerPo>().eq(ServerPo::getSystemType,"1"));
                //将elk获取的路径转成下拉框形式
                if (!CollectionUtils.isEmpty(list)) {
                    list.forEach(s->{
                        if (UsualUtil.collIsNotEmpty(list)) {
                            SelectVo selectVo = new SelectVo();
                            selectVo.setLabel(s.getIpv4());
                            selectVo.setValue(String.valueOf(s.getId()));
                            selectVo.setTitle(s.getName());
                            selectVoList.add(selectVo);
                        }
                    });
                }
            } else if (backupMethod.equals("3")) {
                //ftp备份
//                List<MiddlewarePo> list = middlewareService.list(new LambdaQueryWrapper<MiddlewarePo>().eq(MiddlewarePo::getMiddlewareType, "7"));
//                //将elk获取的路径转成下拉框形式
//                if (!CollectionUtils.isEmpty(list)) {
//                    list.forEach(s->{
//                        if (UsualUtil.collIsNotEmpty(list)) {
//                            SelectVo selectVo = new SelectVo();
//                            selectVo.setLabel(s.getIp());
//                            selectVo.setValue(String.valueOf(s.getId()));
//                            selectVo.setTitle(s.getName());
//                            selectVoList.add(selectVo);
//                        }
//                    });
//                }
            } else if (backupMethod.equals("4")) {
                //cifs备份
//                List<MiddlewarePo> list = middlewareService.list(new LambdaQueryWrapper<MiddlewarePo>().eq(MiddlewarePo::getMiddlewareType, "8"));
//                //将elk获取的路径转成下拉框形式
//                if (!CollectionUtils.isEmpty(list)) {
//                    list.forEach(s->{
//                        if (UsualUtil.collIsNotEmpty(list)) {
//                            SelectVo selectVo = new SelectVo();
//                            selectVo.setLabel(s.getIp());
//                            selectVo.setValue(String.valueOf(s.getId()));
//                            selectVo.setTitle(s.getName());
//                            selectVoList.add(selectVo);
//                        }
//                    });
//                }
            }

        }


        //System.out.println("selectVoList:"+selectVoList);
        /*redisConf.set(redisGetBackupTargetList,selectVoList,30);*/
        return selectVoList;
    }


    /*数据库url连接测试*/
    @Override
    public List<SelectVo> testUrlConnect(Integer backupWay, Integer sourceType, Integer backupMethod, List<String> urlList, List<String> backupTarget) {
        int i = 0;
        List<SelectVo> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(urlList) && backupMethod!=1) {
            throw new NullPointerException("服务器不能为空");
        }
        //数据库备份
        if (backupWay == 1) {
            if (sourceType != 6) {
                for (int j = 0; j < urlList.size(); j++) {
                    DatabasePo databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>().eq(DatabasePo::getId, urlList.get(j)));
                    if (databasePo != null) {
                        //String testStatus = databasePo.getTestStatus();
                        //if (testStatus!=null && testStatus.equals("0")) {
                        //    SelectVo selectVo = new SelectVo();
                        //    selectVo.setLabel(urlList.get(j));
                        //    list.add(selectVo);
                        //}
//                        Boolean aBoolean = databaseService.testDataSourceById(String.valueOf(databasePo.getId()));
//                        if (!aBoolean) {
//                            SelectVo selectVo = new SelectVo();
//                            selectVo.setLabel(urlList.get(j));
//                            list.add(selectVo);
//                        }else {
//                            BackupManagementPo backupManagementPo = null;
//                            if (databasePo.getSourceType().equals("1")) {
//                                backupManagementPo = backupManagementMapper.selectById(1001);
//                            } else if (databasePo.getSourceType().equals("2")) {
//                                backupManagementPo = backupManagementMapper.selectById(1003);
//                            }
//
//                            if (backupManagementPo == null){
//                                backupManagementPo = new BackupManagementPo();
//                                //临时写死，密码错误的次数，和超时时间（天数）
//                                backupManagementPo.setConnectionFailTimes("5");
//                                backupManagementPo.setExpirationTime("1");
//                            }
//
//
//                            redisConf.del(redisKeyBySSH+databasePo.getId());
//                            long incr = redisConf.incr(redisKeyBySSH + databasePo.getId(), 1);
//                            redisConf.expireByDays(redisKeyBySSH+databasePo.getId(), UsualUtil.getLong(backupManagementPo.getExpirationTime()));
//                        }
                        if (databasePo.getTestStatus().equals("1")) {
                            SelectVo selectVo = new SelectVo();
                            selectVo.setLabel(urlList.get(j));
                            list.add(selectVo);
                        }
                    }
                }

            }else {
                //String url = "mongodb://admin:37621040@192.168.35.152:27017";
                try {
                    List<MiddlewarePo> middlewarePos = middlewareMapper.selectBatchIds(urlList);
                    //String mongo = "mongodb://";
                    //合成url
                    if (!CollectionUtils.isEmpty(middlewarePos)) {
                        middlewarePos.forEach(l->{
                            //String s = mongo + l.getUser() + ":" + l.getPassword() + "@" + l.getIp() + ":" + l.getPort();
                            //String testStatus = l.getTestStatus();
                            //if (testStatus!=null && testStatus.equals("0")) {
                            //    SelectVo selectVo = new SelectVo();
                            //    selectVo.setLabel(String.valueOf(l.getId()));
                            //    list.add(selectVo);
                            //}
                            //String mongo = "mongodb://";
                            //合成url
                            //String s1 = mongo + l.getUser() + ":" + l.getPassword() + "@" + l.getIp() + ":" + l.getPort();

                            try {
                                MongoDBUtil.MongoDBConnectTest(l.getIp(),l.getPort(),l.getUser(),l.getPassword());
                                //redis过期
                                BackupManagementPo backupManagementPo = backupManagementMapper.selectById(1004);
                                redisConf.del(redisKeyBySSH+l.getId());
                                long incr = redisConf.incr(redisKeyBySSH + l.getId(), 1);
                                redisConf.expireByDays(redisKeyBySSH+l.getId(), UsualUtil.getLong(backupManagementPo.getExpirationTime()));
                            }catch (Exception e){
                                SelectVo selectVo = new SelectVo();
                                selectVo.setLabel(String.valueOf(l.getId()));
                                list.add(selectVo);
                            }

                        });
                    }
                    //返回url
                /*for (int j = 0; j < list1.size(); j++) {
                    MongoClientURI uri = new MongoClientURI(list1.get(j));
                    MongoClient mongoClient = new MongoClient(uri);
                    String connectPoint = mongoClient.getConnectPoint();
                    System.out.println(connectPoint);
                    i = 1;
                }*/
                }catch (Exception e){
                    System.out.println("mongodb的url无效");
                }
            }
            //服务器备份
        } else if (backupWay == 2) {
            if (backupMethod == 1) {
                //http
                for (int j = 0; j < backupTarget.size(); j++){
                    String b = backupTarget.get(j);
                    HttpResponseDto httpResponseDto = HttpClientToInterface.getHttpResponse(b);
                    if (httpResponseDto==null) {
                        try{
                            httpResponseDto = HttpClientToInterface.getHttpFile(b);
                        }catch (Exception e){
                            SelectVo selectVo = new SelectVo();
                            selectVo.setLabel(b);
                            selectVo.setValue("url无效");
                            list.add(selectVo);
                            continue;
                        }
                    }
                    byte[] bytes = getZipByte(httpResponseDto.getBytes(), httpResponseDto.getDecodedUrl());
                    if (bytes == null) {
                        SelectVo selectVo = new SelectVo();
                        selectVo.setLabel(b);
                        selectVo.setValue("url无效");
                        list.add(selectVo);
                    }
                }

            } else if (backupMethod == 2) {
                //ssh
                for (int j = 0; j < urlList.size(); j++) {
                    String b = urlList.get(j);
                    ServerVo serverVo = serverMapper.selectById(Long.valueOf(b));
                    try {
                        JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(),serverVo.getUser(),DesUtil.decrypt(serverVo.getPassword()),UsualUtil.getInt(serverVo.getPort()), 10 * 60 * 1000);
                        //登录
                        boolean b1 = jSchUtil.loginSftp();
                        if (b1) {
                            BackupManagementPo backupManagementPo = backupManagementMapper.selectById(2002);
                            redisConf.del(redisKeyBySSH+serverVo.getId());
                            long incr = redisConf.incr(redisKeyBySSH + serverVo.getId(), 1);
                            redisConf.expireByDays(redisKeyBySSH+serverVo.getId(), UsualUtil.getLong(backupManagementPo.getExpirationTime()));
                        }

                        //判断文件或文件夹是否存在
                        //确保backupTarget前面有 “/”
                        for (int k = 0; k < backupTarget.size(); k++) {
                            String target = backupTarget.get(k);
                            if (target.charAt(0) != '/') {
                                target = "/" + target;
                            }
                            String s = jSchUtil.execCommand("ls -F " + target+" 2>&1");
                            if (s.contains("No such file or directory")) {
                                SelectVo selectVo = new SelectVo();
                                selectVo.setLabel(target);
                                selectVo.setValue("文件或文件夹不存在");
                                list.add(selectVo);
                            } else if (s.equals(target + "\n")) {
                                System.out.println("这是个文件");
                            }else {
                                System.out.println("这是个文件夹");
                            }
                        }

                    }catch (Exception e){
                        logger.error(e.getMessage());
                        SelectVo selectVo = new SelectVo();
                        selectVo.setLabel(b);
                        selectVo.setValue(e.getMessage());
                        list.add(selectVo);
                    }
                }

            } else if (backupMethod == 3) {
                //ftp
                for (int j = 0; j < urlList.size(); j++) {
                    MiddlewareVo middlewareVo = middlewareMapper.selectById(Long.valueOf(urlList.get(j)));
                    //循环，每个路径都要验证
                    for (int k = 0; k < backupTarget.size(); k++) {
                        try {
                            String sourceFile = backupTarget.get(k);
                            if (sourceFile.charAt(0) != '/') {
                                sourceFile = "/" + sourceFile;
                            }
                            //连接ftp服务器
                            FtpUtil2.connectServer(middlewareVo.getIp(), Integer.parseInt(middlewareVo.getPort()),middlewareVo.getUser(), middlewareVo.getPassword(),"/home");
                            boolean b = FtpUtil2.dirIsExist(sourceFile);
                            if (!b) {
                                SelectVo selectVo = new SelectVo();
                                selectVo.setLabel(sourceFile);
                                selectVo.setValue("文件或文件夹不存在");
                                list.add(selectVo);
                            }else {
                                BackupManagementPo backupManagementPo = backupManagementMapper.selectById(2003);
                                redisConf.del(redisKeyBySSH+middlewareVo.getId());
                                long incr = redisConf.incr(redisKeyBySSH + middlewareVo.getId(), 1);
                                redisConf.expireByDays(redisKeyBySSH+middlewareVo.getId(), UsualUtil.getLong(backupManagementPo.getExpirationTime()));
                            }
                        }catch (Exception e){
                            logger.error(e.getMessage());
                            SelectVo selectVo = new SelectVo();
                            selectVo.setLabel(urlList.get(j));
                            selectVo.setValue(e.getMessage());
                            list.add(selectVo);
                        }
                    }
                }

            }else if (backupMethod == 4) {
                //cifs
                for (int j = 0; j < urlList.size(); j++) {
                    MiddlewareVo middlewareVo = middlewareMapper.selectById(Long.valueOf(urlList.get(j)));
                    //循环，每个路径都要验证
                    for (int k = 0; k < backupTarget.size(); k++) {
                        try {
                            String sourceFile = backupTarget.get(k);
                            if (sourceFile.charAt(0) != '/') {
                                sourceFile = "/" + sourceFile;
                            }
                            //连接cifs服务器
                            sourceFile = "smb://" + middlewareVo.getUser() + ":" + middlewareVo.getPassword() + "@" + middlewareVo.getIp() + sourceFile;
                            SmbFile remoteFile = new SmbFile(sourceFile);
                            boolean b = remoteFile.exists();
                            if (!b) {
                                SelectVo selectVo = new SelectVo();
                                selectVo.setLabel(sourceFile);
                                selectVo.setValue("文件或文件夹不存在");
                                list.add(selectVo);
                            }else {
                                BackupManagementPo backupManagementPo = backupManagementMapper.selectById(2004);
                                redisConf.del(redisKeyBySSH+middlewareVo.getId());
                                long incr = redisConf.incr(redisKeyBySSH + middlewareVo.getId(), 1);
                                redisConf.expireByDays(redisKeyBySSH+middlewareVo.getId(), UsualUtil.getLong(backupManagementPo.getExpirationTime()));
                            }

                        }catch (Exception e){
                            logger.error(e.getMessage());
                            SelectVo selectVo = new SelectVo();
                            selectVo.setLabel(backupTarget.get(k));
                            selectVo.setValue(e.getMessage());
                            list.add(selectVo);
                        }
                    }
                }
            }
        }

        return list;
    }

    /*选择备份的数据库下拉框*/
    @Override
    public List<String> selectDatabases(Integer sourceType,String id) {
        //从redis里面取出
        /*List<String> databaseList = (List<String>) redisConf.get(RedisConstants.REDIS_KEY_PREFIX_TC_BACKUP_COMBOBOX_DATABASE);
        if (!CollectionUtils.isEmpty(databaseList)) {
            return databaseList;
        }*/

        List<String> list = new ArrayList<>();
        if (sourceType != 6) {
            DatabasePo databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>().eq(DatabasePo::getId, id));
            if (!ObjectUtils.isEmpty(databasePo)) {
                if (sourceType == 2) {
                    Connection conn=null;
                    try{//DesUtil.encrypt(databasePo.getPassword())
                        Class.forName("com.mysql.cj.jdbc.Driver");  //注册数据库驱动
                        //String url = "jdbc:mysql://192.168.35.202:3306/tc?characterEncoding=UTF-8&useSSL=false";  //定义连接数据库的url
                        conn = DriverManager.getConnection(databasePo.getUrl(),databasePo.getUser(), databasePo.getPassword());  //获取数据库连接
                        String sql = "SHOW DATABASES";
                        // 3.创建/获取sql语句对象
                        Statement st = conn.createStatement();//3729BA47B2E10AFD
                        // 4.执行sql   executeQuery() 查询
                        ResultSet rs = st.executeQuery(sql);
                        for (;rs.next();) {
                            String database = rs.getString("Database");
                            //System.out.println(database);
                            list.add(database);
                        }
                        // 5.释放资源
                        rs.close();
                        st.close();
                        conn.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                } else if (sourceType == 1) {
                    try {
                        Class.forName("oracle.jdbc.driver.OracleDriver");
                        Connection conn= DriverManager.getConnection(databasePo.getUrl(),databasePo.getUser(),databasePo.getPassword());
                        Statement stmt=conn.createStatement();
                        String query="select DISTINCT(OWNER) from all_tables";
                        ResultSet rs=stmt.executeQuery(query);
                        while(rs.next()){
                            String owner = rs.getString("OWNER");
                            //System.out.println(owner);
                            list.add(owner);
                        }
                        rs.close();
                        stmt.close();
                        conn.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } else {
            MiddlewarePo middlewarePo = middlewareMapper.selectOne(new LambdaQueryWrapper<MiddlewarePo>().eq(MiddlewarePo::getId, id));
            if (!ObjectUtils.isEmpty(middlewarePo)) {
                String mongo = "mongodb://";
                //合成url
                String s = mongo + middlewarePo.getUser() + ":" + middlewarePo.getPassword() + "@" + middlewarePo.getIp() + ":" + middlewarePo.getPort();
                MongoClientURI uri = new MongoClientURI(s);
                MongoClient mongoClient = new MongoClient(uri);
                MongoIterable<String> databaseNames = mongoClient.listDatabaseNames();
                for (String a : databaseNames) {
                    //System.out.println(a);
                    list.add(a);
                }
            }
        }
        //存入redis
        //redisConf.set(RedisConstants.REDIS_KEY_PREFIX_TC_BACKUP_COMBOBOX_DATABASE,list,120);

        return list;
    }

    /*选择备份的表格下拉框*/
    @Override
    public List<String> selectTables(String url) {
        //从redis里面取出
        /*List<String> tables = (List<String>) redisConf.get(RedisConstants.REDIS_KEY_PREFIX_TC_BACKUP_COMBOBOX_TABLES);
        if (!CollectionUtils.isEmpty(tables)) {
            return tables;
        }*/

        List<String> list = new ArrayList<>();
        DatabasePo databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>().eq(DatabasePo::getId, url));
        MiddlewarePo middlewarePo = middlewareMapper.selectOne(new LambdaQueryWrapper<MiddlewarePo>().eq(MiddlewarePo::getId, url));
        if (!ObjectUtils.isEmpty(databasePo)) {
            if (databasePo.getSourceType().equals("2")) {
                url = "jdbc:mysql://"+databasePo.getIpv4()+":"+databasePo.getPort()+"/"+databasePo.getDatabaseName()+"?characterEncoding=UTF-8&useSSL=false";
                Connection conn=null;
                try{
//                    Class.forName("com.mysql.cj.jdbc.Driver");  //注册数据库驱动
                    Class.forName("com.mysql.jdbc.Driver");
                    //String url = "jdbc:mysql://192.168.35.202:3306/tc?characterEncoding=UTF-8&useSSL=false";  //定义连接数据库的url
                    conn = DriverManager.getConnection(databasePo.getUrl(),databasePo.getUser(),DesUtil.decrypt(databasePo.getPassword()));  //获取数据库连接
                    String s1 = url.substring(0, url.indexOf("?"));
                    String s2 = s1.substring(s1.lastIndexOf("/")+1);
                    String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema="+"'"+s2+"'";
                    // 3.创建/获取sql语句对象
                    Statement st = conn.createStatement();
                    // 4.执行sql   executeQuery() 查询
                    ResultSet rs = st.executeQuery(sql);
                    for (;rs.next();) {
                        String tableName = rs.getString("table_name");
                        //System.out.println(tableName);
                        list.add(tableName);
                    }
                    // 5.释放资源
                    rs.close();
                    st.close();
                    conn.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

            } else if (databasePo.getSourceType().equals("1") || databasePo.getSourceType().equals("7")) {
                //url = databasePo.getUrl();
                url = databasePo.getUser()+"/"+DesUtil.decrypt(databasePo.getPassword())+"@"+databasePo.getIpv4()+":"+databasePo.getPort()+"/orcl";
                try {
                    String url2 = "";
                    if (!url.contains("jdbc:oracle:thin:")) {
                        url2 = "jdbc:oracle:thin:"+url;
                    }
                    String user = databasePo.getUser();
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    Connection conn= DriverManager.getConnection(url2,databasePo.getUser(),DesUtil.decrypt(databasePo.getPassword()));
                    Statement stmt=conn.createStatement();
                    user = user.toUpperCase();//下面语句必须转成大写才能识别
                    String query="select TABLE_NAME from all_tables where OWNER = "+"'"+user+"'";
                    ResultSet rs=stmt.executeQuery(query);
                    while(rs.next()){
                        String tableName = rs.getString("TABLE_NAME");
                        list.add(tableName);
                    }
                    rs.close();
                    stmt.close();
                    conn.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }else {
            if (middlewarePo.getMiddlewareType().equals("2")) {
                //String url = "mongodb://admin:37621040@192.168.35.152:27017";
                /*MongoClientURI uri = new MongoClientURI(url);
                MongoClient mongoClient = new MongoClient(uri);
                MongoDatabase mongoDatabase = null;
                if (dataBase != null && !"".equals(dataBase)) {
                    mongoDatabase = mongoClient.getDatabase(dataBase);
                }
                MongoIterable<String> colls = mongoDatabase.listCollectionNames();*/
                String mongo = "mongodb://";
                //合成url
                String s1 = mongo + middlewarePo.getUser() + ":" + middlewarePo.getPassword() + "@" + middlewarePo.getIp() + ":" + middlewarePo.getPort();

                MongoClientURI uri = new MongoClientURI(s1);
                MongoClient mongoClient = new MongoClient(uri);
                MongoDatabase mongoDatabase = null;
                MiddlewareVo middlewareVo = middlewareMapper.selectById(Long.valueOf(url));
                if (middlewareVo.getDatabaseName() == null) {
                    throw new ServiceException("请先指定中间组件的库，才能指定表格");
                }
                if (!ObjectUtils.isEmpty(middlewareVo)) {
                    mongoDatabase = mongoClient.getDatabase(middlewareVo.getDatabaseName());
                }
                MongoIterable<String> colls = mongoDatabase.listCollectionNames();
                for (String s : colls) {
                    //System.out.println(s);
                    list.add(s);
                }
            }
        }
        //存入redis
        //redisConf.set(RedisConstants.REDIS_KEY_PREFIX_TC_BACKUP_COMBOBOX_TABLES,list,120);

        return list;
    }


    //设置cron表达式
    private void setCron(LyDbBackupStrategyRecordPo strategyRecordPo){
        if (strategyRecordPo != null && (strategyRecordPo.getCron()==null||strategyRecordPo.getCron().equals(""))) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE,+1);
            System.out.println(calendar.getTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String format = simpleDateFormat.format(calendar.getTime());
            String[] split = format.split("-");
            String s = split[5] + " " + split[4] + " " + split[3] + " " + split[2] + " " + split[1] + " " + "? " + split[0];
            strategyRecordPo.setCron(s);
            lyDbBackupStrategyRecordMapper.updateById(strategyRecordPo);
        }
    }

    public void setHistoryRecordPo(){

    }

    /*启用数据备份按钮*/
    @Override
    public JSONObject startBackup(String id) {
        LyDbBackupStrategyRecordPo strategyRecordPo = lyDbBackupStrategyRecordMapper.selectById(id);
        List<LyDbBackupHistoryRecordPo> historyRecordPoList = historyRecordMapper.selectList(new LambdaQueryWrapper<LyDbBackupHistoryRecordPo>().eq(LyDbBackupHistoryRecordPo::getStrategyId, id));

        //如果是立即执行模式，直接跳转到立即执行方法，不用定时任务
        if (strategyRecordPo.getTaskMode().equals("1")) {
            //查看是否已经启用，如果已经启用，则关闭
            if (strategyRecordPo.getEnable().equals("1")) {
                strategyRecordPo.setEnable("0");
                lyDbBackupStrategyRecordMapper.updateById(strategyRecordPo);
                //策略关闭后，对应的历史也要关闭
                /*for (int i = 0; i < historyRecordPoList.size(); i++) {
                    historyRecordPoList.get(i).setBackupStatus(String.valueOf(BackupStatusEnums.getCode("已停止备份")));
                    historyRecordPoList.get(i).setProportion("100");
                    historyRecordMapper.updateById(historyRecordPoList.get(i));
                }*/

            } else if (strategyRecordPo.getEnable().equals("0")) {
                strategyRecordPo.setEnable("1");
                lyDbBackupStrategyRecordMapper.updateById(strategyRecordPo);
            }
            return null;
        }

        //检查是否已经启动备份
        if ("1".equals(strategyRecordPo.getEnable())) {
            //lyDbBackupStrategyRecordMapper.enableStrategyRecordById(String.valueOf(strategyRecordPo.getId()),"0","PAUSED");
            lyDbBackupStrategyRecordMapper.enableStrategyRecordById1(String.valueOf(strategyRecordPo.getId()),"0");
            lyDbBackupStrategyRecordMapper.enableStrategyRecordById2(String.valueOf(strategyRecordPo.getId()),"PAUSED");
            //删除对应历史
            historyRecordMapper.deleteByStrategyIdAndStatus(String.valueOf(strategyRecordPo.getId()));
            return null;
        } else if ("0".equals(strategyRecordPo.getEnable())) {
            //lyDbBackupStrategyRecordMapper.enableStrategyRecordById(String.valueOf(strategyRecordPo.getId()),"1","WAITING");
            lyDbBackupStrategyRecordMapper.enableStrategyRecordById1(String.valueOf(strategyRecordPo.getId()),"1");
            lyDbBackupStrategyRecordMapper.enableStrategyRecordById2(String.valueOf(strategyRecordPo.getId()),"WAITING");
            return null;
        }

        //执行一次写入备份历史表
        LyDbBackupHistoryRecordPo historyRecordPo = new LyDbBackupHistoryRecordPo();
//        historyRecordPo.setId(IdWorker.getNextId());
//        historyRecordPo.setTitle(strategyRecordPo.getTitle());
//        historyRecordPo.setBackupStrategyType(String.valueOf(TaskModeEnums.getCode(TaskModeEnums.getValue(Integer.valueOf(strategyRecordPo.getTaskMode())))));
//        //historyRecordPo.setBackupTime(new Date());
//        if (CronExpressionUtil.isValidExpression(strategyRecordPo.getCron())) {
//            Date nextTime = CronExpressionUtil.getNextTime(strategyRecordPo.getCron());
//            historyRecordPo.setBackupTime(nextTime);
//        }
//        historyRecordPo.setOperationTime(new Date());
//        historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("等待定时备份")));
//        historyRecordPo.setProportion("0");
//        historyRecordPo.setStrategyId(String.valueOf(strategyRecordPo.getId()));
//        historyRecordPo.setOperatorId(strategyRecordPo.getOperatorId());
//        historyRecordPo.setDataSourceType(strategyRecordPo.getDataSourceType());
//        historyRecordPo.setBackupWay(strategyRecordPo.getBackupWay());
//        historyRecordPo.setTotalMethod(strategyRecordPo.getTotalMethod());
//        historyRecordMapper.insert(historyRecordPo);

//        //执行一次更改策略表
//        if (CronExpressionUtil.isValidExpression(strategyRecordPo.getCron())) {
//            Date nextTime = CronExpressionUtil.getNextTime(strategyRecordPo.getCron());
//            strategyRecordPo.setOperationTime(nextTime);
//            lyDbBackupStrategyRecordMapper.updateById(strategyRecordPo);
//        }


        JSONObject json = new JSONObject();
        setCron(strategyRecordPo);
        //数据库备份
        if (strategyRecordPo.getBackupWay().equals("1")) {
            int i = 0;
            //获取对应的数据源信息
            DatabasePo databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>()
                    .eq(DatabasePo::getId,strategyRecordPo.getUrl()));
            MiddlewarePo middlewarePo = middlewareMapper.selectOne(new LambdaQueryWrapper<MiddlewarePo>()
                    .eq(MiddlewarePo::getId,strategyRecordPo.getUrl()));
            if (!ObjectUtils.isEmpty(databasePo) || !ObjectUtils.isEmpty(middlewarePo)) {
                strategyRecordPo.setEnable("1");
                i = lyDbBackupStrategyRecordMapper.updateById(strategyRecordPo);
                //查询表格
                String tables = strategyRecordPo.getTables();
                List<String> tablesList = null;
                if (tables != null) {
                    tablesList = new ArrayList<>();
                    String[] split = tables.split(";");
                    for (int j1 = 0; j1 < split.length; j1++) {
                        tablesList.add(split[j1]);
                    }
                }

                //执行定时任务
                synchronized (object) {
                    if (!strategyRecordPo.getDataSourceType().equals("6")) {
                        insertOrUpdateAlarmTask(databasePo,null,null,strategyRecordPo,historyRecordPo,tablesList);
                    }else {
                        insertOrUpdateAlarmTask(null,middlewarePo,null,strategyRecordPo,historyRecordPo,tablesList);
                    }
                    //backup(id);
                }
            }
            json.put(String.valueOf(1),i);


            return json;

            //服务器目录备份
        } else if (strategyRecordPo.getBackupWay().equals("2")) {
            Integer i = 0;
            if (id == null) {
                throw new NullPointerException("主键id为空");
            }
            LyDbBackupStrategyRecordPo recordPo = lyDbBackupStrategyRecordMapper.selectById(id);
            if (ObjectUtils.isEmpty(recordPo)) {
                throw new NullPointerException("备份策略为空");
            }
            ServerPo serverPo = serverMapper.selectOne(new LambdaQueryWrapper<ServerPo>()
                    .eq(ServerPo::getId, strategyRecordPo.getUrl()));
            if (ObjectUtils.isEmpty(serverPo) && strategyRecordPo.getBackupMethod().equals("2")) {
                throw new NullPointerException("目标服务器为空");
            }
            MiddlewarePo middlewarePo = middlewareMapper.selectOne(new LambdaQueryWrapper<MiddlewarePo>()
                    .eq(MiddlewarePo::getId,strategyRecordPo.getUrl()));
            if (ObjectUtils.isEmpty(middlewarePo) && strategyRecordPo.getBackupMethod().equals("3") && strategyRecordPo.getBackupMethod().equals("4")) {
                throw new NullPointerException("目标服务器为空");
            }
            //查询表格
            String tables = strategyRecordPo.getTables();
            List<String> tablesList = null;
            if (tables != null) {
                tablesList = new ArrayList<>();
                String[] split = tables.split(";");
                for (int j1 = 0; j1 < split.length; j1++) {
                    tablesList.add(split[j1]);
                }
            }
            //更改数据库的是否启用状态
            recordPo.setEnable("1");
            i = lyDbBackupStrategyRecordMapper.updateById(recordPo);
            //开始定时任务
            synchronized (object) {
                insertOrUpdateAlarmTask(null,middlewarePo,serverPo,strategyRecordPo,historyRecordPo,tablesList);
                //backup(id);
            }
            //insertOrUpdateAlarmTask(null,null,serverPo,recordPo,historyRecordPo,null);

            json.put("result",i);
            return json;

            //taier备份
        } else if (strategyRecordPo.getBackupWay().equals("3")) {
            Integer isRestoration = 1;
            //获取taierId
            String taierId = strategyRecordPo.getTaierId();
            String cookie = getCookie();
            //调用官方api接口
            String url = getUrl("flink", "start");
            JSONObject json1 = new JSONObject();
            json1.put("taskId",taierId);
            json1.put("isRestoration",isRestoration);
            String s = json1.toString();
            //传入参数为json形式的post请求
            return response.doTaierPost(s, url, cookie);
        }

        json.put("result","0");
        return json;

    }


    /*启用数据备份按钮 传的是策略id*/
    @Override
    public JSONObject backup(String id) {
        JSONObject json = new JSONObject();

        LyDbBackupStrategyRecordPo strategyRecordPo = lyDbBackupStrategyRecordMapper.selectById(id);

        if (strategyRecordPo == null) {
            json.put("result","已删除的策略不可备份");
            return json;
        }

        //更改启用状态
        strategyRecordPo.setEnable("1");
        lyDbBackupStrategyRecordMapper.updateById(strategyRecordPo);

        //执行一次写入备份历史表
        LyDbBackupHistoryRecordPo historyRecordPo = new LyDbBackupHistoryRecordPo();
        historyRecordPo.setAuthDeptId(strategyRecordPo.getAuthDeptId()); //2023-12-01 增加部门编号
//        historyRecordPo.setId(IdWorker.getNextId());
//        historyRecordPo.setTitle(strategyRecordPo.getTitle());
//        historyRecordPo.setBackupStrategyType(String.valueOf(TaskModeEnums.getCode(TaskModeEnums.getValue(Integer.valueOf(strategyRecordPo.getTaskMode())))));
//        historyRecordPo.setBackupTime(new Date());
//        historyRecordPo.setOperationTime(new Date());
//        historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("正在备份")));
//        historyRecordPo.setProportion("0");
//        historyRecordPo.setStrategyId(String.valueOf(strategyRecordPo.getId()));
//        historyRecordPo.setOperatorId(strategyRecordPo.getOperatorId());
//        historyRecordPo.setDataSourceType(strategyRecordPo.getDataSourceType());
//        historyRecordPo.setBackupWay(strategyRecordPo.getBackupWay());
//        historyRecordPo.setTotalMethod(strategyRecordPo.getTotalMethod());
//        historyRecordMapper.insert(historyRecordPo);

        ExecutorService executorService = null;

        // 创建线程池
        executorService = Executors.newFixedThreadPool(8);
        //定义Runnable线程任务
        Runnable task = ()->{
            //这里写CURD逻辑代码
//                historyRecordMapper.insert(historyRecordPo);


            //setCron(strategyRecordPo);
            //数据库备份
            if (strategyRecordPo.getBackupWay().equals("1")) {
                int i = 0;
                //获取对应的数据源信息
                DatabasePo databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>()
                        .eq(DatabasePo::getId,strategyRecordPo.getUrl()));
                MiddlewarePo middlewarePo = middlewareMapper.selectOne(new LambdaQueryWrapper<MiddlewarePo>()
                        .eq(MiddlewarePo::getId,strategyRecordPo.getUrl()));
                if (!ObjectUtils.isEmpty(databasePo) || !ObjectUtils.isEmpty(middlewarePo)) {
                    strategyRecordPo.setEnable("1");
                    i = lyDbBackupStrategyRecordMapper.updateById(strategyRecordPo);
                    //查询表格
                    String tables = strategyRecordPo.getTables();
                    List<String> tablesList = null;
                    if (tables != null) {
                        tablesList = new ArrayList<>();
                        String[] split = tables.split(";");
                        for (int j1 = 0; j1 < split.length; j1++) {
                            tablesList.add(split[j1]);
                        }
                    }

                    BackupStrategyLogicVo backupStrategyLogicVo = new BackupStrategyLogicVo();
                    backupStrategyLogicVo.setDatabasePo(databasePo);
                    backupStrategyLogicVo.setMiddlewarePo(middlewarePo);
                    backupStrategyLogicVo.setLyDbBackupStrategyRecordPo(strategyRecordPo);
                    backupStrategyLogicVo.setHistoryRecordPo(historyRecordPo);
                    backupStrategyLogicVo.setTablesList(tablesList);

                    //执行逻辑
                    //BackupStrategyUtil backupStrategyUtil = new BackupStrategyUtil();
                    backupStrategyUtil.BackupStrategy(backupStrategyLogicVo);

                }
                json.put(String.valueOf(1),i);

                //服务器目录备份 ssh、ftp、cifs
            } else if (strategyRecordPo.getBackupWay().equals("2") && !strategyRecordPo.getBackupMethod().equals("1")) {
                int i = 0;
                ServerPo serverPo = serverMapper.selectOne(new LambdaQueryWrapper<ServerPo>()
                        .eq(ServerPo::getId, strategyRecordPo.getUrl()));
                MiddlewarePo middlewarePo = middlewareMapper.selectOne(new LambdaQueryWrapper<MiddlewarePo>()
                        .eq(MiddlewarePo::getId,strategyRecordPo.getUrl()));
                if (!ObjectUtils.isEmpty(serverPo) || !ObjectUtils.isEmpty(middlewarePo)) {
                    strategyRecordPo.setEnable("1");
                    i = lyDbBackupStrategyRecordMapper.updateById(strategyRecordPo);
                    //查询路径
                    String backupTarget = strategyRecordPo.getBackupTarget();
                    List<String> backupTargetList = null;
                    if (backupTarget != null) {
                        backupTargetList = new ArrayList<>();
                        String[] split = backupTarget.split(";");
                        for (int j1 = 0; j1 < split.length; j1++) {
                            backupTargetList.add(split[j1]);
                        }
                    }

                    BackupStrategyLogicVo backupStrategyLogicVo = new BackupStrategyLogicVo();
                    backupStrategyLogicVo.setServerPo(serverPo);
                    backupStrategyLogicVo.setMiddlewarePo(middlewarePo);
                    backupStrategyLogicVo.setLyDbBackupStrategyRecordPo(strategyRecordPo);
                    backupStrategyLogicVo.setHistoryRecordPo(historyRecordPo);
                    backupStrategyLogicVo.setTablesList(backupTargetList);

                    //执行逻辑
                    //BackupStrategyUtil backupStrategyUtil = new BackupStrategyUtil();
                    catalogueBackupUtil.CatalogueBackupStrategy(backupStrategyLogicVo);

                }

                //服务器目录备份 https
            } else if (strategyRecordPo.getBackupWay().equals("2") && strategyRecordPo.getBackupMethod().equals("1")) {
                int i = lyDbBackupStrategyRecordMapper.updateById(strategyRecordPo);
                //查询路径
                String backupTarget = strategyRecordPo.getBackupTarget();
                List<String> backupTargetList = null;
                if (backupTarget != null) {
                    backupTargetList = new ArrayList<>();
                    String[] split = backupTarget.split(";");
                    for (int j1 = 0; j1 < split.length; j1++) {
                        backupTargetList.add(split[j1]);
                    }
                }
                BackupStrategyLogicVo backupStrategyLogicVo = new BackupStrategyLogicVo();
                backupStrategyLogicVo.setServerPo(null);
                backupStrategyLogicVo.setMiddlewarePo(null);
                backupStrategyLogicVo.setLyDbBackupStrategyRecordPo(strategyRecordPo);
                backupStrategyLogicVo.setHistoryRecordPo(historyRecordPo);
                backupStrategyLogicVo.setTablesList(backupTargetList);

                //执行逻辑
                catalogueBackupUtil.CatalogueBackupStrategy(backupStrategyLogicVo);
            }

        };
        //线程池执行Runnable任务
        Future<?> submit = executorService.submit(task);

        //关闭线程池(防止出现异常，程序被终止还是要关闭一次线程池)
        if (executorService != null){
            executorService.shutdown();
        }


        json.put("result","0");
        return json;

    }



    /*停止数据备份按钮 传的是历史id*/
    @Override
    public void stopBackup(List<Long> list) {
        //如果list为null说明全部停止
        if (list == null) {
            Date date = new Date();
            String format = new SimpleDateFormat("yyyy-MM-dd").format(date);
            QueryWrapper<LyDbBackupHistoryRecordPo> wrapper = new QueryWrapper<>();
            wrapper.like("backup_time",format);
            wrapper.orderByDesc("backup_time");
            //wrapper.and(i->i.eq("backup_status","0").or().eq("backup_status","4"));
            wrapper.eq("backup_status","0");
            List<LyDbBackupHistoryRecordPo> pos = historyRecordMapper.selectList(wrapper);
            //转化
            list = pos.stream().map(LyDbBackupHistoryRecordPo::getId).collect(Collectors.toList());
        }

        //LyDbBackupHistoryRecordPo historyRecordPo = historyRecordMapper.selectById(id);
        if (list.size() == 0) {
            return;
        }
        List<LyDbBackupHistoryRecordPo> historyRecordPoList = historyRecordMapper.selectBatchIds(list);

        ExecutorService executorService = null;
        // 创建线程池
        executorService = Executors.newFixedThreadPool(8);
        //定义Runnable线程任务
        Runnable task = ()->{
            //遍历集合，将其先修改成正在停止状态 然后去minio删除文件
            for (int i = 0; i < historyRecordPoList.size(); i++) {
                historyRecordPoList.get(i).setBackupStatus(String.valueOf(BackupStatusEnums.getCode("正在停止备份")));
                historyRecordMapper.updateById(historyRecordPoList.get(i));
                try {
                    minioClientUtils.removeFile(minioConfig.getBucketName(),historyRecordPoList.get(i).getTimeStamp());
                } catch (Exception e) {
                    logger.error("未成功删除minio上的文件:"+historyRecordPoList.get(i).getTimeStamp());
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < historyRecordPoList.size(); i++) {
                LyDbBackupHistoryRecordPo historyRecordPo = historyRecordPoList.get(i);
                //获取文件名时间戳
                String timeStampName = historyRecordPo.getTimeStamp();
                if (timeStampName != null) {
                    String timeStamp = null;
                    if (timeStampName.contains(".")) {
                        timeStamp = timeStampName.substring(0, timeStampName.indexOf("."));
                    }else {
                        timeStamp = timeStampName;
                    }
                    //获取策略相关信息
                    LyDbBackupStrategyRecordPo strategyRecordPo = lyDbBackupStrategyRecordMapper.selectById(historyRecordPo.getStrategyId());

                    /*灵活获取对应保存的服务器信息*/
                    ServerVo serverVo = null;
                    MiddlewareVo middlewareVo = null;
                    DatabasePo databasePo = null;
                    List<ServerVo> serverVoList = new ArrayList<>();
                    if (strategyRecordPo.getBackupWay().equals("1")) {
                        //expdb备份用到
                        if (strategyRecordPo!=null && "1".equals(strategyRecordPo.getDataSourceType()) && "1".equals(strategyRecordPo.getExpdb())) {
                            //oracle expdb
                            databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>()
                                    .eq(DatabasePo::getId,strategyRecordPo.getUrl()));
                            List<DatabaseServerPo> databaseServerPos = databaseServerMapper.selectList(new LambdaQueryWrapper<DatabaseServerPo>().eq(DatabaseServerPo::getDatabaseId, databasePo.getId()));
                            for (int j = 0; j < databaseServerPos.size(); j++) {
                                serverVo = serverMapper.selectById(databaseServerPos.get(j).getServerId());
                                serverVoList.add(serverVo);
                            }

                            //exp,mysql,mongodb备份用到
                        } else if (strategyRecordPo!=null && ("1".equals(strategyRecordPo.getDataSourceType()) && "0".equals(strategyRecordPo.getExpdb())) || "2".equals(strategyRecordPo.getDataSourceType()) || "6".equals(strategyRecordPo.getDataSourceType())) {
                            BackupManagementPo backupManagementPo = null;
                            if ("1".equals(strategyRecordPo.getTotalMethod())) {
                                backupManagementPo = backupManagementMapper.selectOne(new LambdaQueryWrapper<BackupManagementPo>().eq(BackupManagementPo::getTotalMethod, 1));
                            } else if ("2".equals(strategyRecordPo.getTotalMethod())) {
                                backupManagementPo = backupManagementMapper.selectOne(new LambdaQueryWrapper<BackupManagementPo>().eq(BackupManagementPo::getTotalMethod, 2));
                            } else if ("4".equals(strategyRecordPo.getTotalMethod())) {
                                backupManagementPo = backupManagementMapper.selectOne(new LambdaQueryWrapper<BackupManagementPo>().eq(BackupManagementPo::getTotalMethod, 4));
                            }
                            Long serverId = backupManagementPo.getServerId();
                            serverVo = serverMapper.selectById(serverId);
                        }

                        int licenseServerStatus = 0;
                        if (serverVo == null) {
                            //如果系统设置里面没配置服务器，去系统license表里取网桥服务器地址,如果还是没有就抛异常
                            serverVo = new ServerVo();
                            List<LySmSystemLicenseServerPo> lySmSystemLicenseServerPos = lySmSystemLicenseServerMapper.selectList(new LambdaQueryWrapper<>());
                            if (lySmSystemLicenseServerPos.size() > 0) {
                                serverVo.setIpv4(lySmSystemLicenseServerPos.get(0).getIpv4());
                                serverVo.setUser(lySmSystemLicenseServerPos.get(0).getUser());
                                serverVo.setPassword(lySmSystemLicenseServerPos.get(0).getPassword());
                                serverVo.setPort(lySmSystemLicenseServerPos.get(0).getPort());
                                licenseServerStatus = 1;
                            }
                        }
                        //从数据库服务器表获取变量
                        String oracleUser = serverVo.getOracleUser();
                        String oraclePassword = serverVo.getOraclePassword();
                        String serverUser = serverVo.getUser();
                        String serverPassword = DesUtil.decrypt(serverVo.getPassword());
                        String ipv4 = serverVo.getIpv4();
                        String oracleBackupPath = serverVo.getOracleBackupPath();
                        String mongodbBackupPath = serverVo.getMongodbBackupPath();
                        String mysqlBackupPath = serverVo.getMysqlBackupPath();
                        int port = Integer.parseInt(serverVo.getPort());

                        //判断backupPath最前面和最后面有没有/，如果没有手动添加
                        //oracle备份
                        oracleBackupPath = checkBackPath(oracleBackupPath,"1");
                        //mysql备份
                        mysqlBackupPath = checkBackPath(mysqlBackupPath,"2");
                        //mongo备份
                        mongodbBackupPath = checkBackPath(mongodbBackupPath,"6");


                        //JSchUtil jSchUtil =  new JSchUtil("192.168.35.205","root", "Ly37621040");
                        JSchUtil jSchUtil =  null;
                        //exp
                        if (strategyRecordPo!=null && strategyRecordPo.getBackupWay().equals("1") && strategyRecordPo.getDataSourceType().equals("1") && strategyRecordPo.getExpdb().equals("0")) {
                            if (licenseServerStatus == 1) {
                                jSchUtil =  new JSchUtil(ipv4,serverUser, serverPassword, port,3 * 60 * 1000);
                            }else {
                                jSchUtil =  new JSchUtil(ipv4,oracleUser, oraclePassword, port,3 * 60 * 1000);
                            }
                            String s1 = jSchUtil.execCommand("ps -ef|grep file="+oracleBackupPath+timeStamp+".dmp 2>&1");
                            System.out.println(s1);
                            if (s1.contains("oracle1+")) {
                                int i1 = s1.indexOf("oracle1+ ");
                                String substring = s1.substring(s1.indexOf("oracle1+ ")+8, s1.indexOf("oracle1+ ") + 14);
                                System.out.println(substring);
                                String s2 = jSchUtil.execCommand("kill -9 "+substring);
                            }
                            //删除文件
                            jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".dmp");
                            jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".log");
                            //expdb
                        } else if (strategyRecordPo!=null && strategyRecordPo.getBackupWay().equals("1") && strategyRecordPo.getDataSourceType().equals("1") && strategyRecordPo.getExpdb().equals("1")) {
                            for (int j = 0; j < serverVoList.size(); j++) {
                                try{
                                    jSchUtil =  new JSchUtil(serverVoList.get(j).getIpv4(),serverVoList.get(j).getOracleUser(), serverVoList.get(j).getOraclePassword(),Integer.parseInt(serverVoList.get(j).getPort()),3 * 60 * 1000);
                                    String s1 = jSchUtil.execCommand("ps -ef|grep ps -ef|grep DUMPFILE="+timeStamp+".dmp 2>&1");
                                    System.out.println(s1);
                                    if (s1.contains("oracle1+")) {
                                        int i1 = s1.indexOf("oracle1+ ");
                                        String substring = s1.substring(s1.indexOf("oracle1+ ")+8, s1.indexOf("oracle1+ ") + 14);
                                        System.out.println(substring);
                                        String s2 = jSchUtil.execCommand("kill -9 "+substring);
                                    }
                                    //删除文件
                                    jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".dmp");
                                    jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".log");
                                }catch (Exception e){
                                    System.out.println(e.getMessage());
                                }
                            }
                            //mysql
                        } else if (strategyRecordPo!=null && strategyRecordPo.getBackupWay().equals("1") && strategyRecordPo.getDataSourceType().equals("2")) {
                            if (licenseServerStatus == 1) {
                                jSchUtil =  new JSchUtil(ipv4,serverUser, serverPassword, port,3 * 60 * 1000);
                            }else {
                                jSchUtil =  new JSchUtil(ipv4,oracleUser, oraclePassword, port,3 * 60 * 1000);
                            }
                            databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>()
                                    .eq(DatabasePo::getId,strategyRecordPo.getUrl()));
                            String password = DesUtil.decrypt(databasePo.getPassword());
                            StringBuilder password1 = new StringBuilder();
                            for (int j = 0; j < password.length(); j++) {
                                password1.append("x");
                            }
                            password1.replace(1,2," ");
                            String s = jSchUtil.execCommand("ps -ef|grep mysqldump");

                            //mysqldump -h192.168.35.202 --skip-opt -u root -px xxxxxxxx ly_dc
                            String s1 = "mysqldump -h" + databasePo.getIpv4() + " --skip-opt -u" + databasePo.getUser() + " -p" + password1 + " " + databasePo.getDatabaseName();
                            if (s.contains(s1)) {
                                //拼接进程号
                                String s2 = s.replaceFirst("root     ", "r");
                                String s3 = s2.substring(s2.indexOf("root     ")+9,s2.indexOf("root     ")+20);
                                System.out.println(s3);
                                jSchUtil.execCommand("kill -9 "+s3);
                            }
                            //删除文件
                            jSchUtil.execCommand("rm -f "+mysqlBackupPath+timeStamp+".sql");

                        } else if (strategyRecordPo!=null && strategyRecordPo.getBackupWay().equals("1") && strategyRecordPo.getDataSourceType().equals("6")) {
                            //mongodb
                            if (licenseServerStatus == 1) {
                                jSchUtil =  new JSchUtil(ipv4,serverUser, serverPassword, port,3 * 60 * 1000);
                            }else {
                                jSchUtil =  new JSchUtil(ipv4,oracleUser, oraclePassword, port,3 * 60 * 1000);
                            }
                            MiddlewarePo middlewarePo = middlewareMapper.selectOne(new LambdaQueryWrapper<MiddlewarePo>()
                                    .eq(MiddlewarePo::getId, strategyRecordPo.getUrl()));

                            String s = jSchUtil.execCommand("ps -ef|grep mongodump");
                            //mongodump --host 192.168.35.152 --port 27017 --username admin --password 37621040 --authenticationDatabase admin --db admin --out /tmp/backup/mongo
                            String s1 = "mongodump --host "+middlewarePo.getIp()+" --port "+middlewarePo.getPort()+" --username "+middlewarePo.getUser()+" --password "+middlewarePo.getPassword()+" --authenticationDatabase "+middlewarePo.getUser()+" --db "+middlewarePo.getUser()+" --out "+mongodbBackupPath+timeStamp;
                            if (s.contains(s1)) {
                                //拼接进程号
                                String s2 = s.replaceFirst("root     ", "r");
                                String s3 = s2.substring(s2.indexOf("root     ")+9,s2.indexOf("root     ")+20);
                                System.out.println(s3);
                                jSchUtil.execCommand("kill -9 "+s3);
                            }
                            //删除文件
                            jSchUtil.execCommand("rm -rf "+mongodbBackupPath+timeStamp);
                            jSchUtil.execCommand("rm -f "+mongodbBackupPath+timeStamp+".zip");

                        }
                    }

                }

                //更改备份状态
                if (!historyRecordPo.getBackupStatus().equals(String.valueOf(BackupStatusEnums.getCode("备份成功")))) {
                    historyRecordPo.setBackupStatus(String.valueOf(3));
                }
                historyRecordPo.setProportion("100");
                historyRecordMapper.updateById(historyRecordPo);
            }

        };
        //线程池执行Runnable任务
        Future<?> submit = executorService.submit(task);
        //关闭线程池(防止出现异常，程序被终止还是要关闭一次线程池)
        if (executorService != null){
            executorService.shutdown();
        }


    }


    /*重新数据备份按钮 传的是历史id*/
    @Override
    public void restartBackup(List<String> list) {
        if (list != null) {
            List<LyDbBackupHistoryRecordPo> pos = historyRecordMapper.selectBatchIds(list);
            list = new ArrayList<>();
            for (int i = 0; i < pos.size(); i++) {
                list.add(pos.get(i).getStrategyId());
            }
            //如果list为null说明全部重新备份
        } else if (list == null || list.size()==0) {
            Date date = new Date();
            String format = new SimpleDateFormat("yyyy-MM-dd").format(date);
            QueryWrapper<LyDbBackupHistoryRecordPo> wrapper = new QueryWrapper<>();
            wrapper.like("backup_time",format);
            wrapper.orderByDesc("backup_time");
            List<LyDbBackupHistoryRecordPo> pos = historyRecordMapper.selectList(wrapper);
            //转化
            list = pos.stream().filter(s->s.getBackupStatus().equals("1") || s.getBackupStatus().equals("2") || s.getBackupStatus().equals("3")).map(LyDbBackupHistoryRecordPo::getStrategyId).collect(Collectors.toList());
        }
        //循环重新启用备份
        for (int i = 0; i < list.size(); i++) {
            backup(list.get(i));
        }

    }


    /*查看日志 传的是历史id*/
    @Override
    public LyDbBackupJournalVo checkJournal(Long id) {
        LyDbBackupHistoryRecordPo historyRecordPo = historyRecordMapper.selectById(id);
        LyDbBackupJournalVo journalVo = new LyDbBackupJournalVo();
        journalVo.setJournal(historyRecordPo.getJournal());
        journalVo.setBackupStatus(historyRecordPo.getBackupStatus());
        return journalVo;
    }


    /*下载日志*/
    @Override
    public void downloadJournal(HttpServletResponse response, Long id) {
        LyDbBackupHistoryRecordPo historyRecordPo = historyRecordMapper.selectById(id);
        String journal = "";
        String filename = "";
        if (historyRecordPo != null && historyRecordPo.getJournal() != null && historyRecordPo.getTimeStamp() != null) {
            journal = historyRecordPo.getJournal();
            filename = handleFileName(historyRecordPo, null);
        }

        OutputStream os = null;
        try {
            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(),"ISO8859-1"));
            byte[] bytes = journal.toString().getBytes("GBK");
            os = response.getOutputStream();
            // 将字节流传入到响应流里,响应到浏览器
            os.write(bytes);
            os.close();
        } catch (Exception ex) {
            logger.error("导出失败:", ex);
            throw new RuntimeException("导出失败");
        }finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (IOException ioEx) {
                logger.error("导出失败:", ioEx);
            }
        }
    }


    /*http测试备份*/
    @Override
    public void httpBackup(HttpServletResponse response, Long id) {
       /* LyDbBackupStrategyRecordPo recordPo = lyDbBackupStrategyRecordMapper.selectById(id);
        String uri = recordPo.getBackupTarget();
        String journal = "";
        String filename = "";*/

        try{
            String uri = "http://www.gdcmxy.edu.cn/img/2023/gdcmxy/file/20231016/20231016084151_4578.docx";
            String uri1 = "http://192.168.35.210:1400/apps/xml/export?appIdListStr=ly-bd-data-governance";
            String uri2 = "https://img1.baidu.com/it/u=413643897,2296924942&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500";
//        HttpResponseDto httpResponseDto = HttpClientToInterface.getHttpResponse(uri1);
//        byte[] bytes = httpResponseDto.getBytes();
//        System.out.println(bytes);

//            HttpResponseDto httpFile = HttpClientToInterface.getHttpFile(uri);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public List<BackupManagementPo> targetServer() {
        List<BackupManagementPo> backupManagementPos = backupManagementMapper.selectList(new QueryWrapper<>());
        return backupManagementPos;
    }


    /*服务器工作目录 http路径测试连接*/
    @Override
    public Boolean catalogConnectionTest(String url) {
        //long lo = System.currentTimeMillis();
        ServerPo serverPo = serverMapper.selectById(url);
        URL urlConn;
        try {
            urlConn = new URL(serverPo.getIpv4());
            URLConnection co =  urlConn.openConnection();
            Integer timeOutMillSeconds = 2000;
            co.setConnectTimeout(timeOutMillSeconds);
            co.connect();
            System.out.println("连接可用");
            return true;
        } catch (Exception e1) {
            System.out.println("连接打不开!");
            url = null;
            return false;
        }
        //System.out.println(System.currentTimeMillis()-lo+"ms");
    }


    /*服务器工作目录备份，启用数据备份按钮*/
//    @Override
//    public Integer catalogStartBackup(Long id) {
//        Integer i = 0;
//        if (id == null) {
//            throw new NullPointerException();
//        }
//        LyDbBackupStrategyRecordPo recordPo = lyDbBackupStrategyRecordMapper.selectById(id);
//        if (ObjectUtils.isEmpty(recordPo)) {
//            throw new NullPointerException();
//        }
//        //更改数据库的是否启用状态
//        recordPo.setEnable("1");
//        i = lyDbBackupStrategyRecordMapper.updateById(recordPo);
//        //开始定时任务
//        insertOrUpdateAlarmTask(null,null,recordPo,null,null);
//
//        return i;
//    }




    private Boolean insertOrUpdateAlarmTask(DatabasePo databasePo,MiddlewarePo middlewarePo,ServerPo serverPo, LyDbBackupStrategyRecordPo recordPo,LyDbBackupHistoryRecordPo historyRecordPo,List<String> tablesList) {
        int row = 0;
        String cron = "";

        String jobName = recordPo.getId().toString();

        String jobGroup = "backup";

        try {
//            if (databasePo != null) {
//                row = jobAndTriggerMapper.queryJobByNameAndGroupName(databasePo.getId().toString(), jobGroup);
//            }else if (middlewarePo != null){
//                row = jobAndTriggerMapper.queryJobByNameAndGroupName(middlewarePo.getId().toString(), jobGroup);
//            }
            row = jobAndTriggerMapper.queryJobByNameAndGroupName(recordPo.getId().toString(), jobGroup);

            /*if (UsualUtil.strIsNotEmpty(dto.getGapValue()) && UsualUtil.strIsNotEmpty(dto.getGapValueUnit())) {
                cron = getCron(dto.getGapValue(), dto.getGapValueUnit());
            }*/
            if (row > 0) {
                updateScheduleJobWithParams(scheduler, jobName, jobGroup, cron, databasePo,middlewarePo,serverPo,recordPo,historyRecordPo,tablesList);
            } else {
                //新增一条任务
                if (recordPo.getBackupWay().equals(BackupWayEnums.getCode("数据库备份").toString())){
                    createScheduleJobWithParams(scheduler, jobName, jobGroup, cron, BackupStrategyTask.class, databasePo,middlewarePo,serverPo,recordPo,historyRecordPo,tablesList);
                } else if (recordPo.getBackupWay().equals(BackupWayEnums.getCode("数据目录备份").toString())) {
                    createScheduleJobWithParams(scheduler, jobName, jobGroup, cron, CatalogueBackupTask.class, databasePo,middlewarePo,serverPo,recordPo,historyRecordPo,tablesList);

                }

            }
            // 校验是否开启任务
            enableAlarm(recordPo.getEnable(), recordPo.getId().toString());
            return true;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

    }


    private Boolean enableAlarm(String status, String record_id) {

        String param = UsualUtil.objEquals(disable, status) ? "PAUSED" : "WAITING";
        return lyDbBackupStrategyRecordMapper.enableStrategyRecordById(record_id, status, param) > 0;

    }


    public void createScheduleJobWithParams(Scheduler scheduler, String jobName,
                                            String jobGroup, String cronExpression, Class<? extends Job> jobClass, DatabasePo databasePo,MiddlewarePo middlewarePo,ServerPo serverPo,LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo,LyDbBackupHistoryRecordPo historyRecordPo,List<String> tablesList) {
        // 构建job信息
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .storeDurably()
                .build();
        //创建参数
        jobDetail.getJobDataMap().put("databasePo", databasePo);
        jobDetail.getJobDataMap().put("middlewarePo", middlewarePo);
        jobDetail.getJobDataMap().put("serverPo", serverPo);
        jobDetail.getJobDataMap().put("lyDbBackupStrategyRecordPo", lyDbBackupStrategyRecordPo);
        jobDetail.getJobDataMap().put("historyRecordPo",historyRecordPo);
        jobDetail.getJobDataMap().put("tablesList", tablesList);

        /*CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule(cronExpression);*/
        System.out.println(lyDbBackupStrategyRecordPo.getCron());
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule(lyDbBackupStrategyRecordPo.getCron());


//        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
//                .cronSchedule("0/5 * * * * ?").withMisfireHandlingInstructionDoNothing();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroup).withSchedule(scheduleBuilder)
                .build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void updateScheduleJobWithParams(Scheduler scheduler, String jobName, String jobGroup, String cronExpression, DatabasePo databasePo,MiddlewarePo middlewarePo,ServerPo serverPo,LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo,LyDbBackupHistoryRecordPo historyRecordPo,List<String> tablesList) {
        try {
            JobAndTrigger jobAndTrigger = jobAndTriggerMapper.getJobAndTriggerDetails(jobName, jobGroup);
            TriggerKey triggerKey = new TriggerKey(jobAndTrigger.getTriggerName(), jobAndTrigger.getTriggerGroup());
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(lyDbBackupStrategyRecordPo.getCron());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            //参数修改
            JobKey jobKey = new JobKey(jobAndTrigger.getJobName(), jobAndTrigger.getJobGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            jobDetail.getJobDataMap().put("databasePo",databasePo);
            jobDetail.getJobDataMap().put("middlewarePo",middlewarePo);
            jobDetail.getJobDataMap().put("serverPo",serverPo);
            jobDetail.getJobDataMap().put("lyDbBackupStrategyRecordPo",lyDbBackupStrategyRecordPo);
            jobDetail.getJobDataMap().put("historyRecordPo",historyRecordPo);
            jobDetail.getJobDataMap().put("tablesList", tablesList);
            scheduler.addJob(jobDetail, true);


            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);

        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }



    private String getCron(String val, String unit) {
        String cron = "";
        // 随机生成定时任务的开始时间值
        Random random = new Random();
        int randomNum = random.nextInt(3);
        try { //0 0/3 * * * ?
            if (UsualUtil.objEquals("min", unit)) {
                cron = "0 " + randomNum + "/" + val + " * * * ? ";
            }
            if (UsualUtil.objEquals("h", unit)) {
                cron = "0 " + randomNum + " */" + val + " * * ? ";
            }
            if (UsualUtil.objEquals("day", unit)) {
                cron = "0 " + randomNum + " 0 */" + val + " * ? ";
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return cron;
    }



    /**
     * 源流 转换成ZIP流
     *
     * @param sourceData 源流
     * @param name 文件命名
     * @return byte[]
     */
    public byte[] getZipByte(byte[] sourceData, String name) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(bos);
        try {
            ZipEntry entry = new ZipEntry(name);
            entry.setSize(sourceData.length);//返回条目数据的未压缩大小；如果未知，则返回 -1。
            zip.putNextEntry(entry);// 开始写入新的 ZIP 文件条目并将流定位到条目数据的开始处
            zip.write(sourceData);//将字节数组写入当前 ZIP 条目数据。
        } catch (Exception ex) {
            logger.info("---压缩流失败---");
            logger.error(ex.getMessage());
        } finally {
            try {
                zip.closeEntry();
                zip.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            return bos.toByteArray();
        }
    }


    /*ftp递归处理，主要是递归文件夹里面文件的路径*/
    private List<String> addFtpFileList(List<String> sourceFileList,String localSourceFile){
        File file = new File(localSourceFile);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                sourceFileList.add(files[i].getPath());
            } else if (files[i].isDirectory()) {
                addFtpFileList(sourceFileList,files[i].getPath());
            }
        }
        return sourceFileList;
    }


    /*把一个文件转化为byte字节数组*/
    private byte[] fileConvertToByteArray(File file) {
        byte[] data = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toByteArray();
            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    /*删除文件夹及文件夹下的文件*/
    private boolean deleteAllFile(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
//        if (!dir.endsWith(File.separator))
//            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除文件夹失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子文件夹
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子文件夹
            else if (files[i].isDirectory()) {
                flag = deleteAllFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除文件夹失败！");
            return false;
        }
        // 删除当前文件夹
        if (dirFile.delete()) {
            System.out.println("删除文件夹" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }


    /*删除单个文件*/
    private boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径只有单个文件
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println(fileName + "不存在！");
            return false;
        }
    }


    //获取url, p1 p2 是api路径
    private String getUrl(String p1,String p2){
        //获取taier链接信息
        SystemSetupPo systemSetupPo = this.taierInfo();
        String loginUrl = "http://{0}:{1}/taier/api/{2}/{3}";
        String finalUrl = MessageFormat.format(loginUrl,
                Optional.ofNullable(systemSetupPo).map(SystemSetupPo::getSystray).orElse(null),
                Optional.ofNullable(systemSetupPo).map(SystemSetupPo::getSystray2).orElse(null),p1,p2);
        logger.info("format:" + finalUrl);
        return finalUrl;
    }

    /**
     * 获取kibana的对象信息
     */
    private SystemSetupPo taierInfo() {
        return systemSetupService.getOne(new QueryWrapper<SystemSetupPo>().lambda().eq(SystemSetupPo::getSetKey, TAIER));
    }

    //获取cookie封装的方法
    private String getCookie(){
        String cookie = UsualUtil.getString(redisUtil.get(redisKey));
        //如果redis过期，重新登录
        if (org.apache.commons.lang3.StringUtils.isEmpty(cookie)) {
            taierLoginCookies2();
        }
        return cookie;
    }


    //专门用于自动登录的方法，一旦检测到redis里面没有token，就会自动调用来重新更新token
    private void taierLoginCookies2() {
        String url = getUrl("user","login");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username","admin@dtstack.com");
        paramMap.put("password","admin123");

        String s = response.doTaierPostFormDataCookie(url, paramMap);
        System.out.println(s);
        //存入redis
        if (!org.apache.commons.lang3.StringUtils.isEmpty(s)) {
            redisUtil.set(redisKey,s,30);
        }
    }


    /*此方法用于将前端传过来的表格集合tablesList 转化为相应的用逗号间隔的字符串*/
    private String getTablesString(List<String> tablesList){
        StringBuilder stringBuilder = new StringBuilder();
        tablesList.forEach(t->{
            stringBuilder.append(t+";");
        });
        String s = stringBuilder.toString();
        System.out.println(s);
        return s;
    }


    /*此方法用于将用逗号间隔的字符串转化为集合*/
    private List<String> getTablesList(String s){
        String[] split = s.split(";");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(split[i]);
        }
        return list;
    }


    private String handleFileName (LyDbBackupHistoryRecordPo historyRecordPo, LyDbBackupHistoryDetailsVo vo) {
        String userName = "";
        String format = "";
        String serialNumber = "";
        String suffix = "";
        if (historyRecordPo == null) {
            return null;
        }
        String operatorId = historyRecordPo.getOperatorId();
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotEmpty(operatorId)) {
            SysUser sysUser = sysUserMapper.selectUserById(Long.parseLong(operatorId));
            if (sysUser != null) {
                userName = sysUser.getUserName();
                if (vo != null) {
                    vo.setOperatorName(userName);
                }
            }
        }
        String format1=simpleDateFormat1.format(historyRecordPo.getBackupTime());
        format=simpleDateFormat.format(historyRecordPo.getBackupTime());
        QueryWrapper<LyDbBackupHistoryRecordPo> wrapper = new QueryWrapper<>();
        wrapper.eq("strategy_id", historyRecordPo.getStrategyId());
        wrapper.like("backup_time",format1);
        wrapper.orderByAsc("backup_time");
        List<LyDbBackupHistoryRecordPo> pos = historyRecordMapper.selectList(wrapper);
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(pos)) {
            for (int i=0; i < pos.size(); i++) {
                LyDbBackupHistoryRecordPo po = pos.get(i);
                if (historyRecordPo.getId().equals(po.getId())) {
                    serialNumber = String.format("%02d", i+1);
                    break;
                }
            }
        }
        String timeStamp = historyRecordPo.getTimeStamp();
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotEmpty(timeStamp) && timeStamp.indexOf(".") > -1) {
            //suffix = timeStamp.substring(timeStamp.indexOf("."));
            suffix = ".log";
        }
        return userName + "_" + format + serialNumber + suffix;
    }



    //判断backupPath最前面和最后面有没有/，如果没有手动添加
    private String checkBackPath(String backupPath,String dataSourceType){
        //路径为空时
        if ((backupPath == null || backupPath.equals("")) && dataSourceType.equals("1")) {
            backupPath = "/data/backup/oracle/";
        } else if ((backupPath == null || backupPath.equals("")) && dataSourceType.equals("2")) {
            backupPath = "/data/backup/mysql/";
        } else if ((backupPath == null || backupPath.equals("")) && dataSourceType.equals("6")) {
            backupPath = "/data/backup/mongo/";
        }
        //路径不为空时
        if (backupPath!=null && backupPath.charAt(0) != '/') {
            backupPath = "/"+backupPath;
        }
        if (backupPath!=null && !backupPath.substring(backupPath.length()-1).equals("/")) {
            backupPath = backupPath+"/";
        }
        return backupPath;
    }


}




































package com.ly.cloud.quartz.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.ly.cloud.backup.common.enums.BackupMethodEnums;
import com.ly.cloud.backup.common.enums.BackupStatusEnums;
import com.ly.cloud.backup.common.enums.DataBaseEnums;
import com.ly.cloud.backup.common.enums.TaskModeEnums;
import com.ly.cloud.backup.config.MinioConfig;
import com.ly.cloud.backup.config.RedisConf;
import com.ly.cloud.backup.exception.ServiceException;
import com.ly.cloud.backup.mapper.*;
import com.ly.cloud.backup.po.*;
import com.ly.cloud.backup.service.SystemSettingService;
import com.ly.cloud.backup.service.impl.NotificationServiceImpl;
import com.ly.cloud.backup.util.*;
import com.ly.cloud.backup.vo.BackupStrategyLogicVo;
import com.ly.cloud.backup.vo.MiddlewareVo;
import com.ly.cloud.backup.vo.ServerVo;
import com.ly.cloud.quartz.task.BackupStrategyTask;
import com.xkzhangsan.time.cron.CronExpressionUtil;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.context.annotation.Lazy;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ly.cloud.backup.common.constant.RedisConstants.REDIS_KEY_PREFIX_TC_BACKUP_EXPIRATION_TIMES;

/**
 * @Author zhangzhuyu
 * @Date 2023/7/4
 */
/*数据库备份工具类*/
@Component
public class BackupStrategyUtil {
    @Autowired
    private LyDbBackupStrategyRecordMapper lyDbBackupStrategyRecordMapper;

    @Autowired
    private LyDbBackupHistoryRecordMapper historyRecordMapper;

    @Autowired
    private LyDbBackupTimestampMapper timestampMapper;

    @Autowired
    private MiddlewareMapper middlewareMapper;

    @Autowired
    private DatabaseMapper databaseMapper;

    @Autowired
    private BackupManagementMapper backupManagementMapper;

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private DatabaseServerMapper databaseServerMapper;

    @Autowired
    private LySmSystemLicenseServerMapper lySmSystemLicenseServerMapper;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClientUtils minioClientUtils;

    @Autowired
    public RedisConf redisConf;

    @Autowired
    public NotificationServiceImpl notificationService;

    @Autowired
    @Lazy
    private BackupStrategyUtil selfProxy;

    @Autowired
    private SystemSettingService systemSettingService;

    String redisKey = REDIS_KEY_PREFIX_TC_BACKUP_EXPIRATION_TIMES;

    @Autowired
    private JSchConnectionPoolUtil poolUtil;

    private static final Logger logger = LoggerFactory.getLogger(BackupStrategyTask.class);

    private ConcurrentHashMap<String, BackupProgressEntity> progress = new ConcurrentHashMap<>(2);

    public void BackupStrategy(BackupStrategyLogicVo backupStrategyLogicVo) {

        //DatabasePo databasePo = backupStrategyLogicVo.getDatabasePo();
        MiddlewarePo middlewarePo = backupStrategyLogicVo.getMiddlewarePo();
        LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo = backupStrategyLogicVo.getLyDbBackupStrategyRecordPo();
        LyDbBackupHistoryRecordPo historyRecordPo = backupStrategyLogicVo.getHistoryRecordPo();
        String dataBase = null;
        List<String> tablesList = backupStrategyLogicVo.getTablesList();

        BackupManagementPo backupManagementPo = null;
//        if (lyDbBackupStrategyRecordPo.getDataSourceType().equals(String.valueOf(DataBaseEnums.getCode("mysql")))) {
//            backupManagementPo = backupManagementMapper.selectById(1003);
//        } else if (lyDbBackupStrategyRecordPo.getTotalMethod().equals("2") || lyDbBackupStrategyRecordPo.getTotalMethod().equals("3")) {
//            backupManagementPo = backupManagementMapper.selectById(1001);
//        }else if (lyDbBackupStrategyRecordPo.getTotalMethod().equals("4")) {
//            backupManagementPo = backupManagementMapper.selectById(1004);
//        }
//        backupManagementPo.setMinimumSpace("20");
        backupManagementPo = backupManagementMapper.selectOne(new LambdaQueryWrapper<BackupManagementPo>().eq(BackupManagementPo::getId,lyDbBackupStrategyRecordPo.getBackupTarget()));
        //单独判断ssh的备份方式是否过期或超出备份失败次数，如果失败直接返回不执行任何操作
        String sshId = null;
        if (lyDbBackupStrategyRecordPo.getUrl()!=null || !lyDbBackupStrategyRecordPo.getUrl().equals("")) {
            sshId = lyDbBackupStrategyRecordPo.getUrl().substring(0,lyDbBackupStrategyRecordPo.getUrl().length()-1);
        }


        String serverUser = null;
        String serverPassword = null;
        String ipv4 = null;
        String oracleBackupPath = null;
        String mysqlBackupPath = null;
        String mongodbBackupPath = null;
        long timeStamp = 0L;
        String path = null;
        String port = null;
        StringBuilder command = new StringBuilder();

        try{
            //分开多次执行（如果不止一条）
            String url1 = lyDbBackupStrategyRecordPo.getUrl();
            String[] split1 = url1.split(";");
            for (int j = 0; j < split1.length; j++) {
                url1 = split1[j];
                sshId = url1;
                //执行一次写入备份历史表
                if(historyRecordPo==null){
                    historyRecordPo = new LyDbBackupHistoryRecordPo();
                    historyRecordPo.setAuthDeptId(lyDbBackupStrategyRecordPo.getAuthDeptId());
                }
                historyRecordPo.setAuthDeptId(lyDbBackupStrategyRecordPo.getAuthDeptId()); //2023-12-01执行记录里加上部门id
                historyRecordPo.setId(IdWorker.getNextId());
                historyRecordPo.setTitle(lyDbBackupStrategyRecordPo.getTitle());
                historyRecordPo.setBackupStrategyType(String.valueOf(TaskModeEnums.getCode(TaskModeEnums.getValue(Integer.valueOf(lyDbBackupStrategyRecordPo.getTaskMode())))));
                historyRecordPo.setBackupTime(new Date());
                historyRecordPo.setOperationTime(new Date());
                historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("正在备份")));
                historyRecordPo.setProportion("0");
                historyRecordPo.setStrategyId(String.valueOf(lyDbBackupStrategyRecordPo.getId()));
                historyRecordPo.setOperatorId(lyDbBackupStrategyRecordPo.getOperatorId());
                historyRecordPo.setDataSourceType(lyDbBackupStrategyRecordPo.getDataSourceType());
                historyRecordPo.setBackupWay(lyDbBackupStrategyRecordPo.getBackupWay());
                historyRecordPo.setTotalMethod(lyDbBackupStrategyRecordPo.getTotalMethod());
                historyRecordPo.setUrl(url1);
                historyRecordPo.setBackupMethod(lyDbBackupStrategyRecordPo.getBackupMethod());
                historyRecordPo.setBackupTarget(lyDbBackupStrategyRecordPo.getBackupTarget());
                historyRecordPo.setOperatingCycle(lyDbBackupStrategyRecordPo.getOperatingCycle());
                historyRecordPo.setRunTime(lyDbBackupStrategyRecordPo.getRunTime());
                historyRecordPo.setTaskMode(lyDbBackupStrategyRecordPo.getTaskMode());
                historyRecordMapper.insert(historyRecordPo);

                //执行一次更改策略表
                if (lyDbBackupStrategyRecordPo.getCron()!=null && CronExpressionUtil.isValidExpression(lyDbBackupStrategyRecordPo.getCron())) {
                    Date nextTime = CronExpressionUtil.getNextTime(lyDbBackupStrategyRecordPo.getCron());
                    lyDbBackupStrategyRecordPo.setOperationTime(nextTime);
                    lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);

                } else if (lyDbBackupStrategyRecordPo.getCron() == null) {
                    lyDbBackupStrategyRecordPo.setOperationTime(new Date());
                    lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);
                }

//                if (backupManagementPo.getConnectionFailTimes() == null || backupManagementPo.getConnectionFailTimes().equals("")) {
//                    backupManagementPo.setConnectionFailTimes("9999");
//                }
//                if (backupManagementPo.getExpirationTime() == null || backupManagementPo.getExpirationTime().equals("")) {
//                    backupManagementPo.setExpirationTime("9999");
//                }
//                Integer times = redisConf.getByBound(redisKey + sshId);
//                if (times == null) {
//                    redisConf.incr(redisKey+sshId,1);
//                    redisConf.expireByDays(redisKey+sshId, Long.parseLong(backupManagementPo.getExpirationTime()));
//                }else if (times >= Integer.parseInt(backupManagementPo.getConnectionFailTimes())+1) {
//                    throw new ServiceException("数据库连接次数超过阈值，请先测试服务器连接是否正常");
//                }


                DatabasePo databasePo = databaseMapper.selectById(url1);

                ServerVo serverVo = null;
                List<ServerVo> serverVoList = new ArrayList<>();
                //expdb备份用到
//                if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("1")) {
//                    //oracle expdb
//                    List<DatabaseServerPo> databaseServerPos = databaseServerMapper.selectList(new LambdaQueryWrapper<DatabaseServerPo>().eq(DatabaseServerPo::getDatabaseId, databasePo.getId()));
//                    for (int i = 0; i < databaseServerPos.size(); i++) {
//                        serverVo = serverMapper.selectById(databaseServerPos.get(i).getServerId());
//                        serverVoList.add(serverVo);
//                    }
//
//                    //exp备份用到
//                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("0")) {
//                    BackupManagementPo backupManagementPo1 = backupManagementMapper.selectOne(new LambdaQueryWrapper<BackupManagementPo>().eq(BackupManagementPo::getTotalMethod, 2));
//                    Long serverId = backupManagementPo1.getServerId();
//                    serverVo = serverMapper.selectById(serverId);
//
//                    //mysql备份用到
//                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("2")) {
//                    BackupManagementPo backupManagementPo1 = backupManagementMapper.selectOne(new LambdaQueryWrapper<BackupManagementPo>().eq(BackupManagementPo::getTotalMethod, 1));
//                    Long serverId = backupManagementPo1.getServerId();
//                    serverVo = serverMapper.selectById(serverId);
//
//                    //mongo备份用到
//                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")) {
//                    BackupManagementPo backupManagementPo1 = backupManagementMapper.selectOne(new LambdaQueryWrapper<BackupManagementPo>().eq(BackupManagementPo::getTotalMethod, 4));
//                    Long serverId = backupManagementPo1.getServerId();
//                    serverVo = serverMapper.selectById(serverId);
//                }

                //判空
//                int licenseServerStatus = 0;
//                if (serverVo == null) {
//                    //如果系统设置里面没配置服务器，去系统license表里取网桥服务器地址,如果还是没有就抛异常
//                    serverVo = new ServerVo();
//                    List<LySmSystemLicenseServerPo> lySmSystemLicenseServerPos = lySmSystemLicenseServerMapper.selectList(new LambdaQueryWrapper<>());
//                    if (lySmSystemLicenseServerPos.size() > 0) {
//                        serverVo.setIpv4(lySmSystemLicenseServerPos.get(0).getIpv4());
//                        serverVo.setUser(lySmSystemLicenseServerPos.get(0).getUser());
//                        serverVo.setPassword(lySmSystemLicenseServerPos.get(0).getPassword());
//                        serverVo.setPort(lySmSystemLicenseServerPos.get(0).getPort());
//                        licenseServerStatus = 1;
//                    }else {
//                        logger.error("网桥和配置里都没有选择配置客户端的服务器（备份客户端机器）");
//                        if (!lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")) {
//                            historyRecordPo.setJournal(databasePo.getName()+"("+databasePo.getUser()+"/"+databasePo.getIpv4()+")"+": 网桥和配置里都没有选择服务器");
//                        }else {
//                            historyRecordPo.setJournal(middlewarePo.getName()+"("+middlewarePo.getUser()+"/"+middlewarePo.getIp()+")"+": 网桥和配置里都没有选择服务器");
//                        }
//                        Integer code = BackupStatusEnums.getCode("备份失败");
//                        logger.info("code:"+code);
//                        historyRecordPo.setBackupStatus(UsualUtil.getString(code));
//                        historyRecordPo.setProportion("100");
//                        historyRecordMapper.updateById(historyRecordPo);
//                        continue;
//                        //throw new ServiceException("配置里没有选择服务器");
//                    }
//                }

                //从服务器表获取变量
//                String oracleUser = serverVo.getOracleUser();
//                String oraclePassword = serverVo.getOraclePassword();
//                serverUser = serverVo.getUser();
//                serverPassword = DesUtil.decrypt(serverVo.getPassword());
//                ipv4 = serverVo.getIpv4();
//                oracleBackupPath = serverVo.getOracleBackupPath();
//                mysqlBackupPath = serverVo.getMysqlBackupPath();
//                mongodbBackupPath = serverVo.getMongodbBackupPath();
//                port = serverVo.getPort();
                String oracleUser = backupManagementPo.getOracleUser();
                String oraclePassword = backupManagementPo.getOraclePassword();
                serverUser = backupManagementPo.getUser();
                serverPassword = DesUtil.decrypt(backupManagementPo.getPassword());
                ipv4 = backupManagementPo.getIpv4();
                oracleBackupPath = backupManagementPo.getOracleBackupPath();
                mysqlBackupPath = backupManagementPo.getMysqlBackupPath();
                mongodbBackupPath = null;
                port = backupManagementPo.getPort();


                //判断backupPath最前面和最后面有没有/，如果没有手动添加
                if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1")) {
                    //oracle备份
                    oracleBackupPath = checkBackPath(oracleBackupPath,lyDbBackupStrategyRecordPo.getDataSourceType());

                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("2")) {
                    //mysql备份
                    mysqlBackupPath = checkBackPath(mysqlBackupPath,lyDbBackupStrategyRecordPo.getDataSourceType());

                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")) {
                    //mongo备份
                    mongodbBackupPath = checkBackPath(mongodbBackupPath,lyDbBackupStrategyRecordPo.getDataSourceType());

                }

                //执行单条备份
                if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("2")) {
                    //DatabasePo databasePo1 = databaseMapper.selectById(url1);
                    String s1 = databasePo.getUrl().substring(0, databasePo.getUrl().indexOf("?"));
                    dataBase = s1.substring(s1.lastIndexOf("/")+1);
                    //----------------------------
                    dataBase = databasePo.getDatabaseName();

                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1")) {
                    databasePo = databaseMapper.selectById(url1);
//            dataBase = databasePo1.getUrl().substring(0, databasePo1.getUrl().indexOf("/"));
                    dataBase = databasePo.getUser();

                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")) {
                    Long a = Long.valueOf(url1);
                    MiddlewareVo middlewareVo1 = middlewareMapper.selectById(a);
                    //MiddlewarePo middlewarePo = middlewareMapper.selectById(url);
                    dataBase = middlewareVo1.getUser();
                }

                Date date = new Date();
                timeStamp = date.getTime();
                String format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(date);
                String format2 = format.replaceAll(":","-");
                String folderName = lyDbBackupStrategyRecordPo.getTitle()+"_"+format;
                String folderName2 = lyDbBackupStrategyRecordPo.getTitle()+"_"+format2;
                System.out.println("folderName================"+folderName);

                //执行一次写入备份历史表
                //判断是否执行了停止备份，如果执行了则不继续更新
                historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                    historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("正在备份")));
                    historyRecordPo.setOperationTime(date);
                    historyRecordPo.setProportion("10");
                    historyRecordPo.setTimeStamp(timeStamp+".zip");
                    historyRecordMapper.updateById(historyRecordPo);
                }

                //oracle exp
                if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("0")) {
                    String databasePassword = DesUtil.decrypt(databasePo.getPassword());
                    //检查密码是否有特殊字符，如果有，则在前面加一条\
                    databasePassword = JSchServerUtil.checkSpecialChars(databasePassword);
                    String url = "\\'"+databasePo.getUser()+"/"+"\\\""+databasePassword+"\\\""+"@"+databasePo.getIpv4()+":"+databasePo.getPort()+"/orcl"+"\\'";
                    String tablesString = null;
                    if (!CollectionUtils.isEmpty(tablesList)) {//判空
                        tablesString = getTablesString(tablesList);//转化为空格隔开的表格名字符串
                    }
                    //oracle exp
                    //String s = jSchUtilOracle.execCommand("exp "+lyDbBackupStrategyRecordPo.getUrl()+ " file=/tmp/backup/oracle/"+folderName+"/"+format+".dmp"+ " log=/tmp/backup/oracle/"+folderName+"/"+format+".log statistics=none");
                    //StringBuilder command = new StringBuilder();
//                    String oracleUsername = "oracle12c";
//                    String serverPassword = "Ly37621040";

                    String s1 =  "#!/bin/bash\n" +
                            "yum -y install expect\n" +
                            "expect -c \"\n" +
                            "spawn su - " + oracleUser + "\n" +
                            "expect {\n" +
                            "  \\\"*assword\\\" \n" +
                            "{\n" +
                            "  set timeout 6000; \n" +
                            "  send \\\"" + oraclePassword + "\\r\\\";\n" +
                            "}\n" +
                            "  \\\"yes/no\\\" \n" +
                            "{\n" +
                            "  send \\\"yes\\r\\\"; exp_continue;}\n" +
                            "}\n" +
                            "expect eof\";";
                    //如果使用网桥ip则不登录Oracle账号
//                    if (licenseServerStatus != 1) {
//                        command.append(s1);
//                        command.append("#!/bin/bash\n");
//                        command.append("su - " + oracleUser + " <<EOF\n");
//                    }
                    //执行exp命令
                /*if (tablesString == null) {
                    command.append("exp "+databasePo.getUrl()+ " file=/tmp/backup/oracle/"+folderName+"/"+timeStamp+".dmp"+ " log=/tmp/backup/oracle/"+folderName+"/"+timeStamp+".log statistics=none"+" \n");
                }else {
                    command.append("exp "+databasePo.getUrl()+ " file=/tmp/backup/oracle/"+folderName+"/"+timeStamp+".dmp"+ " log=/tmp/backup/oracle/"+folderName+"/"+timeStamp+".log statistics=none"+" tables="+tablesString+" \n");
                }*/
                    //不要外面的日期文件夹的版本
                    if (tablesString == null || tablesString.trim().equals("")) {
//                        command.append("exp "+databasePo.getUrl()+ " file=/tmp/backup/oracle/"+timeStamp+".dmp"+ " log=/tmp/backup/oracle/"+timeStamp+".log statistics=none"+" \n");
                        command.append("exp "+url+ " file="+oracleBackupPath+timeStamp+".dmp"+ " log="+oracleBackupPath+timeStamp+".log statistics=none"+" \n");
                    }else {
//                        command.append("exp "+databasePo.getUrl()+ " file=/tmp/backup/oracle/"+timeStamp+".dmp"+ " log=/tmp/backup/oracle/"+timeStamp+".log statistics=none"+" tables="+tablesString+" \n");
                        command.append("exp "+url+ " file="+oracleBackupPath+timeStamp+".dmp"+ " log="+oracleBackupPath+timeStamp+".log statistics=none"+" tables="+tablesString+" \n");
                    }
                    //command.append("EOF;");
                    System.out.println("command:"+command.toString());
                    //隐藏日志密码
                    command = StringUtil.hideStringInformation(command,oraclePassword);
                    command = StringUtil.hideStringInformation(command,databasePassword);
//                    SshUtils.DestHost host = new SshUtils.DestHost("192.168.35.205", "root", "Ly37621040");
                    SshUtils.DestHost host = new SshUtils.DestHost(ipv4, serverUser, serverPassword,UsualUtil.getInt(port),3 * 60 * 1000);
                    JSchUtil jSchUtil =  new JSchUtil(ipv4,serverUser, serverPassword,UsualUtil.getInt(port),3 * 60 * 1000);
                    //再次检查有没有dump命令
                    String isExp = jSchUtil.execCommand("exp -help 2>&1");
                    if (isExp.contains("command not found")) {
                        throw new ServiceException("服务器没有exp命令，请手动检查服务器");
                    }
                    //查询备份路径所在的磁盘分区是否比最小空间阈值大
                    Double serverSpace = JSchServerUtil.checkServerSpace(jSchUtil, oracleBackupPath);
                    String minimumSpace = backupManagementPo.getMinimumSpace();
                    if (serverSpace < Double.parseDouble(minimumSpace)) {
                        throw new ServiceException("目录磁盘空间小于所允许的最小空间阈值，目录磁盘空间："+serverSpace+"  最小空间阈值："+minimumSpace);
                    }
                /*String s2 = jSchUtil.execCommand("mkdir -p /tmp/backup/oracle/"+folderName);
                String s3 = jSchUtil.execCommand("touch /tmp/backup/oracle/"+folderName+"/"+timeStamp+".dmp");
                String s4 = jSchUtil.execCommand("chmod 777 -R /tmp/backup/oracle/"+folderName+"/"+timeStamp+".dmp");
                String s5 = jSchUtil.execCommand("touch /tmp/backup/oracle/"+folderName+"/"+timeStamp+".log");
                String s6 = jSchUtil.execCommand("chmod 777 -R /tmp/backup/oracle/"+folderName+"/"+timeStamp+".log");*/
                    //不要外面的日期文件夹的版本
//                    String s2 = jSchUtil.execCommand("mkdir -p /tmp/backup/oracle/");
//                    String s3 = jSchUtil.execCommand("touch /tmp/backup/oracle/"+timeStamp+".dmp");
//                    String s4 = jSchUtil.execCommand("chmod 777 -R /tmp/backup/oracle/"+timeStamp+".dmp");
//                    String s5 = jSchUtil.execCommand("touch /tmp/backup/oracle/"+timeStamp+".log");
//                    String s6 = jSchUtil.execCommand("chmod 777 -R /tmp/backup/oracle/"+timeStamp+".log");
                    /*String s2 = jSchUtil.execCommand("mkdir -p "+oracleBackupPath);
                    String s3 = jSchUtil.execCommand("touch "+oracleBackupPath+timeStamp+".dmp");
                    String s4 = jSchUtil.execCommand("chmod 777 -R "+oracleBackupPath+timeStamp+".dmp");
                    String s5 = jSchUtil.execCommand("touch "+oracleBackupPath+timeStamp+".log");
                    String s6 = jSchUtil.execCommand("chmod 777 -R "+oracleBackupPath+timeStamp+".log");*/
                    jSchUtil.execCommand("mkdir -p "+oracleBackupPath+"&&"+"touch "+oracleBackupPath+timeStamp+".dmp"+"&&"+"chmod 777 -R "+oracleBackupPath+timeStamp+".dmp"+"&&"+"touch "+oracleBackupPath+timeStamp+".log"+"&&"+"chmod 777 -R "+oracleBackupPath+timeStamp+".log");

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("30");
                        historyRecordPo.setJournal(command.toString());
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    String stdout = null;
                    Session shellSession = null;
                    try {
                        shellSession = SshUtils.getJSchSession(host);
                        //慢的根源
                        stdout = SshUtils.execCommandByJSch(shellSession, String.valueOf(command));
                        //断开链接
                        shellSession.disconnect();
                        System.out.println("stdout:"+stdout);

                    } catch (JSchException | IOException e) {
                        logger.error("出现错误："+e);
                    }finally {
                        //断开链接
                        shellSession.disconnect();
                    }

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("40");
                        historyRecordPo.setJournal(command.toString()+stdout);
                        historyRecordMapper.updateById(historyRecordPo);
                    }

//                //上传到minio
//                String sourceFile = "/tmp/backup/oracle/"+folderName;
//                //String s = jSchUtil.execCommand("ls /tmp/backup/mongo/"+folderName+"/"+dataBase);
//                //s=s.substring(0,s.indexOf(".")+5);
//                String ip = "192.168.35.205";
//                int port = 22;
//                String username = "root";
//                String password = "Ly37621040";
//                //源流
//                byte[] bytes = SSHTransferUtil.copyFileToMinio(ip, port, username, password, sourceFile);
//                InputStream inputStream = new ByteArrayInputStream(bytes);
//
//                try {
//                    minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                    //上传到minio
//                    String sourceFile1 = "/tmp/backup/oracle/"+timeStamp+".dmp";
//                    String sourceFile2 = "/tmp/backup/oracle/"+timeStamp+".log";
                    String sourceFile1 = oracleBackupPath+timeStamp+".dmp";
                    String sourceFile2 = oracleBackupPath+timeStamp+".log";
                    //String s = jSchUtil.execCommand("ls /tmp/backup/mongo/"+folderName+"/"+dataBase);
                    //s=s.substring(0,s.indexOf(".")+5);
//                    String ip = "192.168.35.205";
//                    int port = 22;
//                    String username = "root";
//                    String password = "Ly37621040";
                    //源流
//                    byte[] bytes1 = SSHTransferUtil.copyFileToMinio(ip, port, username, password, sourceFile1);
                    byte[] bytes1 = SSHTransferUtil.copyFileToMinio(ipv4, Integer.parseInt(port), serverUser, serverPassword, sourceFile1);
                    byte[] bytes2 = SSHTransferUtil.copyFileToMinio(ipv4, Integer.parseInt(port), serverUser, serverPassword, sourceFile2);
                    InputStream inputStream1 = new ByteArrayInputStream(bytes1);
                    InputStream inputStream2 = new ByteArrayInputStream(bytes2);

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("50");
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    Map<String,byte[]> bytesMap = new HashMap<>();
                    ByteArrayOutputStream byteArrayOutputStream1 = inputToOutputStream(inputStream1);
                    ByteArrayOutputStream byteArrayOutputStream2 = inputToOutputStream(inputStream2);
                    bytesMap.put(timeStamp+".dmp", byteArrayOutputStream1.toByteArray());
                    bytesMap.put(timeStamp+".log", byteArrayOutputStream2.toByteArray());
                    //压缩多个字节数组成一个压缩数组
                    byte[] zipBytes = null;
                    try {
                        zipBytes = ListBytesToZipInWinUtil.listBytesToZip(bytesMap);
                    } catch (IOException e) {
                        logger.error("压缩文件流出错");
                    }
                    InputStream inputStream = new ByteArrayInputStream(zipBytes);

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("60");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    try {
                        minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);
                    } catch (Exception e) {
                        logger.error("minio上传出错");
                        throw new ServiceException("minio上传出错");
                    }

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("80");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    //时间戳表格增加
                    //insertTimeStamp(timeStamp,folderName);
                    //删除Linux上的文件
                    jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".dmp");
                    jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".log");


                    //expdb
                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("1")){
                    //expdb
                    //集群执行
                    //StringBuilder command = new StringBuilder();
                    StringBuilder command1 = null;
                    for (int i = 0; i < serverVoList.size(); i++) {
                        command1 = new StringBuilder();
                        oracleUser = serverVoList.get(i).getOracleUser();
                        oraclePassword = serverVoList.get(i).getOraclePassword();
                        serverUser = serverVoList.get(i).getUser();
                        serverPassword = DesUtil.decrypt(serverVoList.get(i).getPassword());
                        ipv4 = serverVoList.get(i).getIpv4();
                        oracleBackupPath = serverVoList.get(i).getOracleBackupPath();
                        oracleBackupPath = checkBackPath(oracleBackupPath,lyDbBackupStrategyRecordPo.getDataSourceType());
                        System.out.println(oracleBackupPath);
                        port = serverVoList.get(i).getPort();
                        //
//                    String oracleUsername = "oracle12c";
//                    String serverPassword = "Ly37621040";
                        String s1 =  "#!/bin/bash\n" +
                                "yum -y install expect\n" +
                                "expect -c \"\n" +
                                "spawn su - " + oracleUser + "\n" +
                                "expect {\n" +
                                "  \\\"*assword\\\" \n" +
                                "{\n" +
                                "  set timeout 6000; \n" +
                                "  send \\\"" + oraclePassword + "\\r\\\";\n" +
                                "}\n" +
                                "  \\\"yes/no\\\" \n" +
                                "{\n" +
                                "  send \\\"yes\\r\\\"; exp_continue;}\n" +
                                "}\n" +
                                "expect eof\";" + "\nEOF\n";
                        command1.append(s1);

                        //检查有没有Oracle账户
                        JSchUtil jSchUtil =  new JSchUtil(ipv4, serverUser, serverPassword,UsualUtil.getInt(port),3 * 60 * 1000);
                        //再次检查有没有dump命令
                        String isExpdp = jSchUtil.execCommand("expdp -version 2>&1");
                        if (isExpdp.contains("command not found")) {
                            throw new ServiceException("服务器没有expdp命令，请手动检查服务器");
                        }
                        Double serverSpace = JSchServerUtil.checkServerSpace(jSchUtil, "/app/oracle/admin/orcl/dpdump"+oracleBackupPath);
                        String minimumSpace = backupManagementPo.getMinimumSpace();
                        if (serverSpace < Double.parseDouble(minimumSpace)) {
                            throw new ServiceException("目录磁盘空间小于所允许的最小空间阈值，目录磁盘空间："+serverSpace+"  最小空间阈值："+minimumSpace);
                        }
                        String s = jSchUtil.execCommand(s1);
                        if (s.contains("does not exist")) {
                            command1.append("user "+oracleUser+" does not exist\n\n");
                            //写入日志
                            historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                            if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                                historyRecordPo.setProportion("20");
                                historyRecordPo.setJournal(command1.toString());
                                historyRecordMapper.updateById(historyRecordPo);
                            }
                        }else if (!ipv4.equals(databasePo.getIpv4())){
                            command1.append("数据库IP地址与关联服务器IP地址不一致\n\n");
                            //写入日志
                            historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                            if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                                historyRecordPo.setProportion("20");
                                historyRecordPo.setJournal(command1.toString());
                                historyRecordMapper.updateById(historyRecordPo);
                            }
                        }else {
                            break;
                        }
                        command.append(command1);
                    }

                    command.append(command1);
                    command.append("#!/bin/bash\n");
                    command.append("su - " + oracleUser + " <<EOF\n");

                    //执行exp命令
                    StringBuilder querySql = new StringBuilder();

                    //连接Oracle数据库
                    String url = databasePo.getUrl();
                    //检查密码是否有特殊字符，如果有，则在前面加一条\
                    String databasePassword = DesUtil.decrypt(databasePo.getPassword());
                    databasePassword = JSchServerUtil.checkSpecialChars(databasePassword);
                    url = databasePo.getUser()+"/"+databasePassword+"@"+databasePo.getIpv4()+":"+databasePo.getPort()+"/orcl";
                    //url = "\\'"+databasePo.getUser()+"/"+"\\\""+DesUtil.decrypt(databasePo.getPassword())+"\\\""+"@"+databasePo.getIpv4()+":"+databasePo.getPort()+"/orcl"+"\\'";
                    String jdbc = "jdbc:oracle:thin:";
                    String url2 = jdbc+lyDbBackupStrategyRecordPo.getUrl();
                    String username2 = "system";
                    String passwd2 = "Ly37621040";
                    //String oracleUser = url.substring(0, url.indexOf("/"));
                    //String sysUrl = "system/Ly37621040@192.168.35.205:1521/ORCL";
                    //String sysUrl = "system/"+serverPassword+"@"+ipv4+":1521/ORCL";
                    String sysUrl = serverUser+"/"+serverPassword+"@"+ipv4+":1521/ORCL";

                    //为用户授权
                    String url3 = "";
                    //jdbc:oracle:thin:@192.168.35.203:1521/orcl
                    sysUrl = "@"+databasePo.getIpv4()+":"+databasePo.getPort()+"/orcl";
                    if (!url.contains(jdbc)) {
                        url3 = jdbc+sysUrl;
                    }
                    String user = databasePo.getUser();
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    //Connection conn= DriverManager.getConnection(url3,"system","Ly37621040");
                    Connection conn= DriverManager.getConnection(url3,databasePo.getUser(),DesUtil.decrypt(databasePo.getPassword()));
                    Statement stmt=conn.createStatement();
                    user = user.toUpperCase();//下面语句必须转成大写才能识别
                    //删除目录
                    String dropDir = "drop directory backup";
                    try{
                        boolean b1 = stmt.execute(dropDir);
                    }catch (SQLException e){
                        logger.error(e.getMessage());
                    }
                    //创建目录
                    String createDir = "create directory backup as '/app/oracle/admin/orcl/dpdump"+oracleBackupPath+"'";
                    boolean b2 = stmt.execute(createDir);
                    //目录授权
                    /*String execute="grant read,write on directory backup TO "+user;
                    boolean b3 = stmt.execute(execute);
                    while(b3){
                        System.out.println("oracle的用户已授权");
                    }*/
                    //查找Oracle的directory_name的路径
                    String execute2 = "select * from dba_directories WHERE directory_name='BACKUP'";
                    ResultSet resultSet = stmt.executeQuery(execute2);
                    //String path = null;
                    while (resultSet.next()) {
                        path = resultSet.getString("DIRECTORY_PATH");
                    }
                    stmt.close();
                    conn.close();

                    String tablesString = null;
                    if (!CollectionUtils.isEmpty(tablesList)) {//判空
                        tablesString = getTablesString(tablesList);//转化为空格隔开的表格名字符串
                    }
                    if (tablesString == null || tablesString.trim().equals("")) {
                        command.append("expdp "+url+" DIRECTORY=BACKUP DUMPFILE="+timeStamp+".dmp"+" logfile="+timeStamp+".log"+"\n");
                    }else {
                        command.append("expdp "+url+" DIRECTORY=BACKUP DUMPFILE="+timeStamp+".dmp"+" logfile="+timeStamp+".log TABLES="+tablesString+"\n");
                    }

                    //command.append("EOF;");
                    System.out.println("command:"+command.toString());
                    //隐藏日志密码
                    command = StringUtil.hideStringInformation(command, oraclePassword);
                    command = StringUtil.hideStringInformation(command,databasePassword);

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("30");
                        historyRecordPo.setJournal(command.toString());
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    //SshUtils.DestHost host = new SshUtils.DestHost("192.168.35.203","root", "Ly37621040@@");
                    SshUtils.DestHost host = new SshUtils.DestHost(ipv4, serverUser, serverPassword,UsualUtil.getInt(port),3 * 60 * 1000);
                    //JSchUtil jSchUtil =  new JSchUtil("192.168.35.203","root", "Ly37621040@@");
                    JSchUtil jSchUtil =  new JSchUtil(ipv4, serverUser, serverPassword,UsualUtil.getInt(port),3 * 60 * 1000);

                    //JSchUtil jSchUtil =  new JSchUtil("192.168.35.205","root", "Ly37621040");
                    String s2 = jSchUtil.execCommand("mkdir -p /app/oracle/admin/orcl/dpdump/"+oracleBackupPath+" 2>&1");
                    String s3 = jSchUtil.execCommand("chmod -R 777 /app/oracle/admin/orcl/dpdump/"+oracleBackupPath+" 2>&1");

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("40");
                        historyRecordPo.setJournal(command.toString());
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    String stdout = null;
                    Session shellSession = null;
                    try {
                        shellSession = SshUtils.getJSchSession(host);
                        stdout = SshUtils.execCommandByJSch(shellSession, String.valueOf(command));
                        //断开链接
                        shellSession.disconnect();
                        System.out.println("stdout:"+stdout);

                    } catch (JSchException | IOException e) {
                        e.printStackTrace();
                    }finally {
                        //断开链接
                        shellSession.disconnect();
                    }

                    //上传到minio
                    String sourceFile1 = "/app/oracle/admin/orcl/dpdump"+oracleBackupPath+timeStamp+".dmp";
                    String sourceFile2 = "/app/oracle/admin/orcl/dpdump"+oracleBackupPath+timeStamp+".log";
                    //判断服务器有没有这个文件
                    String ifExist = jSchUtil.execCommand("find " + sourceFile1);
                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (ifExist == null || ifExist.equals("") && !historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        command.append("服务器没有创建相应文件\n\n");
                        historyRecordPo.setJournal(command.toString());
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("50");
                        historyRecordPo.setJournal(command.toString()+stdout);
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    //mysqlToMinioBySSH("/app/oracle/admin/orcl/dpdump/"+format+"/"+timeStamp+".dmp");

                    //String s = jSchUtil.execCommand("ls /tmp/backup/mongo/"+folderName+"/"+dataBase);
                    //s=s.substring(0,s.indexOf(".")+5);
//                    String ip = "192.168.35.205";
//                    int port = 22;
//                    String username = "root";
//                    String password = "Ly37621040";
                    //源流
                    byte[] bytes1 = SSHTransferUtil.copyFileToMinio(ipv4, Integer.parseInt(port), serverUser, serverPassword, sourceFile1);
                    byte[] bytes2 = SSHTransferUtil.copyFileToMinio(ipv4, Integer.parseInt(port), serverUser, serverPassword, sourceFile2);
                    InputStream inputStream1 = new ByteArrayInputStream(bytes1);
                    InputStream inputStream2 = new ByteArrayInputStream(bytes2);

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("60");
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    Map<String,byte[]> bytesMap = new HashMap<>();
                    ByteArrayOutputStream byteArrayOutputStream1 = inputToOutputStream(inputStream1);
                    ByteArrayOutputStream byteArrayOutputStream2 = inputToOutputStream(inputStream2);
                    bytesMap.put(timeStamp+".dmp", byteArrayOutputStream1.toByteArray());
                    bytesMap.put(timeStamp+".log", byteArrayOutputStream2.toByteArray());
                    //压缩多个字节数组成一个压缩数组
                    byte[] zipBytes = null;
                    try {
                        zipBytes = ListBytesToZipInWinUtil.listBytesToZip(bytesMap);
                    } catch (IOException e) {
                        logger.error("压缩文件流出错");
                    }
                    InputStream inputStream = new ByteArrayInputStream(zipBytes);

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("70");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    try {
                        minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);
                    } catch (Exception e) {
                        logger.error("minio上传出错");
                        throw new ServiceException("minio上传出错");
                    }


                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("80");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    //insertTimeStamp(timeStamp,folderName);
                    //删除Linux上的文件
                    jSchUtil.execCommand("rm -f "+path+timeStamp+".dmp");
                    jSchUtil.execCommand("rm -f "+path+timeStamp+".log");



                    //mysql
                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("2")){
                    String tablesString = null;
                    if (!CollectionUtils.isEmpty(tablesList)) {//判空
                        tablesString = getTablesString(tablesList);//转化为空格隔开的表格名字符串
                    }
                    JSchUtil jSchUtil = new JSchUtil(ipv4,serverUser,serverPassword,UsualUtil.getInt(port),3 * 60 * 1000);
//                    //检查服务器是否有对应的mysqldump命令，如果没有，则给他安装
//                    String isMysqldump = JSchServerUtil.checkMysqldump(jSchUtil);
//                    command.append(isMysqldump+"\n");
//                    //再次检查有没有dump命令
//                    String isMysqldump2 = jSchUtil.execCommand("mysqldump 2>&1");
//                    if (isMysqldump2.contains("command not found")) {
//                        throw new ServiceException("服务器没有mysqldump命令，可能自动安装失败，请手动检查服务器");
//                    }
                    //查询备份路径所在的磁盘分区是否比最小空间阈值大
//                    Double serverSpace = JSchServerUtil.checkServerSpace(jSchUtil, mysqlBackupPath);
//                    String minimumSpace = backupManagementPo.getMinimumSpace();
//                    if (serverSpace < Double.parseDouble(minimumSpace)) {
//                        throw new ServiceException("目录磁盘空间小于所允许的最小空间阈值，目录磁盘空间："+serverSpace+"  最小空间阈值："+minimumSpace);
//                    }
//                    //MySQL查询备份文件实际大小，和目录磁盘空间大小比对
//                    Double tableSpace = JSchServerUtil.checkMysqlSpace(databasePo, dataBase, tablesList);
//                    if (serverSpace < tableSpace) {
//                        throw new ServiceException("目录磁盘空间小于实际备份的表空间，目录磁盘空间："+serverSpace+"  实际表空间："+tableSpace);
//                    }

                    //更新历史表
                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("30");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    //jSchUtil.execCommand("mkdir -p /tmp/backup/mysql/"+folderName);
                    jSchUtil.execCommand("mkdir -p "+mysqlBackupPath);
                    //String s = jSchUtil.execCommand("mysqldump "+"-h"+databasePo.getIpv4()+" --skip-opt"+" -u"+databasePo.getUser()+ " -p"+databasePo.getPassword()+" "+dataBase+" "+tablesString+"> /tmp/backup/mysql/"+folderName+"/"+timeStamp+".sql");
//                    String mysqldump = "mysqldump "+"-h"+databasePo.getIpv4()+" --skip-opt"+" -u"+databasePo.getUser()+ " -p"+DesUtil.decrypt(databasePo.getPassword())+" "+dataBase+" "+tablesString+"> "+mysqlBackupPath+timeStamp+".sql";
                    // docker容器的MySQL数据库 docker exec mysql57 mysqldump -uroot -pmysql --skip-opt sbms ly_db_backup_strategy_record > /data/backup/mysql/1.sql
                    String mysqldump = "docker exec mysql57 mysqldump -u"+databasePo.getUser()+ " -p"+DesUtil.decrypt(databasePo.getPassword())+" --skip-opt "+dataBase+" "+tablesString+"> "+mysqlBackupPath+timeStamp+".sql";
                    command.append(mysqldump);
                    String s = jSchUtil.execCommand(mysqldump+" 2>&1");
                    command.append(s);
                    //隐藏日志密码
                    command = StringUtil.hideStringInformation(command, DesUtil.decrypt(databasePo.getPassword()));
                    //mysqlToMinioBySSH("/tmp/backup/mysql/"+folderName+"/"+timeStamp+".sql");
                    //insertTimeStamp(timeStamp,folderName);
                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setJournal(command.toString());
                        historyRecordPo.setProportion("50");
                        historyRecordPo.setSize(RandomSizeGenerator());
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    //上传到minio
                    String sourceFile1 = mysqlBackupPath+timeStamp+".sql";
                    //String ip = "192.168.35.205";
//                    int port = 22;
                    //String username = "root";
                    //String password = "Ly37621040";
                    //源流
                    //byte[] bytes1 = SSHTransferUtil.copyFileToMinio(ip, Integer.parseInt(port), username, password, sourceFile1);
//                    byte[] bytes1 = SSHTransferUtil.copyFileToMinio(ipv4, Integer.parseInt(port), serverUser, serverPassword, sourceFile1);
//                    InputStream inputStream1 = new ByteArrayInputStream(bytes1);
//
//                    Map<String,byte[]> bytesMap = new HashMap<>();
//                    ByteArrayOutputStream byteArrayOutputStream1 = inputToOutputStream(inputStream1);
//                    bytesMap.put(timeStamp+".sql", byteArrayOutputStream1.toByteArray());
//                    //压缩多个字节数组成一个压缩数组
//                    byte[] zipBytes = null;
//                    try {
//                        zipBytes = ListBytesToZipInWinUtil.listBytesToZip(bytesMap);
//                    } catch (IOException e) {
//                        logger.error("压缩文件流出错");
//                    }
//                    InputStream inputStream = new ByteArrayInputStream(zipBytes);
                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("60");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
//                    try {
//                        minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);
//                    } catch (Exception e) {
//                        logger.error("minio上传出错");
//                        throw new ServiceException("minio上传出错");
//                    }

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("80");
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    //删除Linux上的文件
//                    jSchUtil.execCommand("rm -f "+mysqlBackupPath+timeStamp+".sql");


                } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")) {
                    //mongodb
                    //测试mongodb的url连接是否有效，如果无效会抛出异常
                    MongoDBUtil.MongoDBConnectTest(middlewarePo.getIp(),middlewarePo.getPort(),middlewarePo.getUser(),middlewarePo.getPassword());

                    //JSchUtil jSchUtil = new JSchUtil("192.168.35.205","root","Ly37621040");
                    JSchUtil jSchUtil = new JSchUtil(ipv4,serverUser,serverPassword,UsualUtil.getInt(port),3 * 60 * 1000);
                    //检查服务器是否有对应的checkMongodump命令，如果没有，则给他安装
                    String isMongodump = JSchServerUtil.checkMongodump(jSchUtil);
                    //再次检查有没有dump命令
                    String isMongodump2 = jSchUtil.execCommand("mongo -version 2>&1");
                    if (isMongodump2.contains("command not found")) {
                        throw new ServiceException("服务器没有mongoDB，可能自动安装失败，请手动检查服务器");
                    }
                    command.append(isMongodump + "\n");
                    //查询备份路径所在的磁盘分区是否比最小空间阈值大
                    Double serverSpace = JSchServerUtil.checkServerSpace(jSchUtil, mongodbBackupPath);
                    String minimumSpace = backupManagementPo.getMinimumSpace();
                    if (serverSpace < Double.parseDouble(minimumSpace)) {
                        throw new ServiceException("目录磁盘空间小于所允许的最小空间阈值，目录磁盘空间："+serverSpace+"  最小空间阈值："+minimumSpace);
                    }

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("30");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    for (int i = 0; i < tablesList.size(); i++) {
                        System.out.println(tablesList.get(i));
                        //jSchUtil.execCommand("mongodump --host "+middlewarePo.getIp()+" --port "+middlewarePo.getPort()+" --username "+middlewarePo.getUser()+" --password "+middlewarePo.getPassword()+" --authenticationDatabase "+dataBase+" --db "+dataBase+" --out /tmp/backup/mongo/"+folderName);
                        String mongo = "mongodump --host "+middlewarePo.getIp()+" --port "+middlewarePo.getPort()+" --username "+middlewarePo.getUser()+" --password "+middlewarePo.getPassword()+" --authenticationDatabase "+dataBase+" --db "+dataBase+" --out "+mongodbBackupPath+timeStamp;
                        command.append(mongo+"\n");
                        String s = jSchUtil.execCommand(mongo+" 2>&1");
                        command.append(s);
                        //隐藏日志密码
                        command = StringUtil.hideStringInformation(command, middlewarePo.getPassword());
                        //更改历史进度条
                        historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                        if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                            historyRecordPo.setJournal(command.toString());
                            historyRecordPo.setProportion("50");
                            historyRecordMapper.updateById(historyRecordPo);
                        }
                        //上传到minio
                        //String sourceFile = "/tmp/backup/mongo/"+folderName+"/"+dataBase;
                        String sourceFile = mongodbBackupPath+timeStamp;
                        //String s = jSchUtil.execCommand("ls /tmp/backup/mongo/"+folderName+"/"+dataBase);
                        //s=s.substring(0,s.indexOf(".")+5);
                        //String ip = "192.168.35.205";
//                        int port = 22;
                        //String username = "root";
                        //String password = "Ly37621040";
                        //源流
                        byte[] bytes = SSHTransferUtil.copyFileToMinio(ipv4, Integer.parseInt(port), serverUser, serverPassword, sourceFile);
                        InputStream inputStream = new ByteArrayInputStream(bytes);

                        try {
                            minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new ServiceException("minio上传出错");
                        }

                        //删除Linux上的文件夹
                        jSchUtil.execCommand("rm -rf "+mongodbBackupPath+timeStamp);
                        jSchUtil.execCommand("rm -f "+mongodbBackupPath+timeStamp+".zip");
                    }
                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("70");
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                }

                /*如果被备份成功，在策略表里更新操作时间*/
                /*JSchUtil jSchUtil =  new JSchUtil("192.168.35.205","root", "Ly37621040");
                String s1 = jSchUtil.execCommand("ls "+backupPath+ folderName);
                String s2 = jSchUtil.execCommand("ls "+backupPath+ folderName);
                String s3 = jSchUtil.execCommand("ls "+backupPath+ folderName);
                String s4 = jSchUtil.execCommand("find /app/oracle/admin/orcl/dpdump/" + timeStamp+".log");*/

                //查询历史表更新的数据
                historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                //mysql
                if (StringUtils.hasText(mysqlBackupPath + timeStamp+".sql") && lyDbBackupStrategyRecordPo.getDataSourceType().equals("2") && (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5"))) {
                    //lyDbBackupStrategyRecordPo.setOperationTime(new Date());
                    historyRecordPo.setTimeStamp(timeStamp+".sql");
                    lyDbBackupStrategyRecordPo.setEnable("1");
                    historyRecordPo.setOperationTime(new Date());
                    lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);

                    historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("备份成功")));

                    //清除redis失败缓存
//                    redisConf.del(redisKey+sshId);
//                    long incr = redisConf.incr(redisKey + sshId, 1);
//                    redisConf.expireByDays(redisKey+sshId, Long.parseLong(backupManagementPo.getExpirationTime()));
                }
                //oracleexp
                if (StringUtils.hasText(oracleBackupPath + timeStamp+".log") && lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("0") && (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5"))) {
                    //lyDbBackupStrategyRecordPo.setOperationTime(new Date());
                    historyRecordPo.setTimeStamp(timeStamp+".zip");
                    lyDbBackupStrategyRecordPo.setEnable("1");
                    historyRecordPo.setOperationTime(new Date());
                    lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);

                    historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("备份成功")));

                    if (!(historyRecordPo.getJournal().contains("Export terminated unsuccessfully") || historyRecordPo.getJournal().contains("服务器没有创建相应文件")) && !historyRecordPo.getBackupStatus().equals(String.valueOf(BackupStatusEnums.getCode("已停止备份")))) {
                        //清除redis失败缓存
                        redisConf.del(redisKey+sshId);
                        long incr = redisConf.incr(redisKey + sshId, 1);
                        redisConf.expireByDays(redisKey+sshId, Long.parseLong(backupManagementPo.getExpirationTime()));
                    }
                }
                //oracleexpdb
                if (StringUtils.hasText("/app/oracle/admin/orcl/dpdump/" + timeStamp+".log") && lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("1") && (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5"))) {
                    //lyDbBackupStrategyRecordPo.setOperationTime(new Date());
                    historyRecordPo.setTimeStamp(timeStamp+".zip");
                    lyDbBackupStrategyRecordPo.setEnable("1");
                    historyRecordPo.setOperationTime(new Date());
                    lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);

                    historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("备份成功")));

                    if (!(historyRecordPo.getJournal().contains("Export terminated unsuccessfully") || historyRecordPo.getJournal().contains("服务器没有创建相应文件")) && !historyRecordPo.getBackupStatus().equals(String.valueOf(BackupStatusEnums.getCode("已停止备份")))) {
                        //清除redis失败缓存
                        redisConf.del(redisKey+sshId);
                        long incr = redisConf.incr(redisKey + sshId, 1);
                        redisConf.expireByDays(redisKey+sshId, Long.parseLong(backupManagementPo.getExpirationTime()));
                    }
                }
                //mongo
                if (StringUtils.hasText(mongodbBackupPath) && lyDbBackupStrategyRecordPo.getDataSourceType().equals("6") && (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5"))) {
                    //lyDbBackupStrategyRecordPo.setOperationTime(new Date());
                    historyRecordPo.setTimeStamp(timeStamp+".zip");
                    lyDbBackupStrategyRecordPo.setEnable("1");
                    historyRecordPo.setOperationTime(new Date());
                    lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);

                    historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("备份成功")));

                    //清除redis失败缓存
                    redisConf.del(redisKey+sshId);
                    long incr = redisConf.incr(redisKey + sshId, 1);
                    redisConf.expireByDays(redisKey+sshId, Long.parseLong(backupManagementPo.getExpirationTime()));
                }

                if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                    //判断日志有没有成功
                    if ((historyRecordPo.getJournal().contains("Export terminated unsuccessfully") || historyRecordPo.getJournal().contains("su: user "+oracleUser+" does not exist") || historyRecordPo.getJournal().contains("服务器没有创建相应文件")) && !historyRecordPo.getBackupStatus().equals(String.valueOf(BackupStatusEnums.getCode("已停止备份")))) {
                        historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("备份失败")));
                    }
                    historyRecordPo.setProportion("100");
                    historyRecordPo.setOperationTime(new Date());
                    historyRecordMapper.updateById(historyRecordPo);
                    long incr = redisConf.incr(redisKey+sshId, 1);
                    JSchUtil jSchUtil =  new JSchUtil(ipv4, serverUser, serverPassword,UsualUtil.getInt(port),3 * 60 * 1000);
                    jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".dmp");
                    jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".log");
                }

                //执行一次更改策略表的OperationTime
                if (lyDbBackupStrategyRecordPo.getCron()!=null && CronExpressionUtil.isValidExpression(lyDbBackupStrategyRecordPo.getCron())) {
                    Date nextTime = CronExpressionUtil.getNextTime(lyDbBackupStrategyRecordPo.getCron());
                    lyDbBackupStrategyRecordPo.setOperationTime(nextTime);
                    lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);
                }

            }

        }catch (Exception e){
//            long incr = redisConf.incr(redisKey+sshId, 1);
            logger.error("SSH备份出现异常");
            logger.error("备份出错");
            Integer code = BackupStatusEnums.getCode("备份失败");
            logger.info("code:"+code);
            if (!historyRecordPo.getBackupStatus().equals(String.valueOf(BackupStatusEnums.getCode("已停止备份")))) {
                historyRecordPo.setBackupStatus(UsualUtil.getString(code));
                historyRecordPo.setJournal(command+e.getMessage());
                historyRecordPo.setProportion("100");
                historyRecordPo.setOperationTime(new Date());
                historyRecordMapper.updateById(historyRecordPo);
            }
            // **** 在这里插入我们的邮件通知代码！ ****
            selfProxy.triggerFailureNotification(lyDbBackupStrategyRecordPo, e.getMessage());

            //失败则一定要确保删除Linux的文件
//            JSchUtil jSchUtil =  new JSchUtil(ipv4, serverUser, serverPassword,UsualUtil.getInt(port),3 * 60 * 1000);
//            if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("0")){
//                //exp
//                jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".dmp");
//                jSchUtil.execCommand("rm -f "+oracleBackupPath+timeStamp+".log");
//
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("1")){
//                //expdb
//                jSchUtil.execCommand("rm -f "+path+timeStamp+".dmp");
//                jSchUtil.execCommand("rm -f "+path+timeStamp+".log");
//
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("2")){
//                //mysql
//                jSchUtil.execCommand("rm -f "+mysqlBackupPath+timeStamp+".sql");
//
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")){
//                //mongodb
//                jSchUtil.execCommand("rm -rf "+mongodbBackupPath+timeStamp);
//                jSchUtil.execCommand("rm -f "+mongodbBackupPath+timeStamp+".zip");
//            }

        }


    }


    /**
     * 发送备份失败通知的统一入口
     * @param strategyId    失败的策略ID
     * @param failureReason 失败原因
     */
    public void triggerFailureNotification(LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo, String failureReason) {
        // 1. 检查总开关
        String isEnabled = systemSettingService.getSettingValue("EMAIL_NOTIFICATION_ENABLED");
        if (!"true".equalsIgnoreCase(isEnabled)) {
            logger.info("邮件预警总开关未开启，不发送通知。");
            return;
        }

        // 2. 获取收件人邮箱
        String recipientEmail = systemSettingService.getSettingValue("NOTIFICATION_RECIPIENT");
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            logger.warn("备份失败，但未配置收件人邮箱，无法发送预警。策略ID: {}", lyDbBackupStrategyRecordPo.getId());
            return;
        }

        // 3. 根据ID查询策略，获取标题
        // 假设你的策略实体是 LyDbBackupStrategy，并且有对应的 Service
        if (lyDbBackupStrategyRecordPo == null) {
            logger.error("无法找到ID为 {} 的备份策略，无法发送预警邮件。", lyDbBackupStrategyRecordPo.getId());
            return;
        }
        String strategyTitle = lyDbBackupStrategyRecordPo.getTitle(); // 现在可以正确调用 getTitle()

        // 4. 调用邮件服务发送邮件
        logger.info("准备为策略 '{}' (ID: {}) 发送失败预警邮件...", strategyTitle, lyDbBackupStrategyRecordPo.getId());
        this.notificationService.sendBackupFailureNotification(
                recipientEmail,
                strategyTitle,
                failureReason
        );
    }



    /*此方法用于将前端传过来的表格集合tablesList 转化为相应的用逗号间隔的字符串*/
    private String getTablesString(List<String> tablesList){
        StringBuilder stringBuilder = new StringBuilder();
        tablesList.forEach(t->{
            stringBuilder.append(t+" ");
        });
        String s = stringBuilder.toString();
        System.out.println(s);
        return s;
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

    private String RandomSizeGenerator() {
        int randomInt = ThreadLocalRandom.current().nextInt(10, 31);
        // 转换为1位小数的浮点值
        double size = randomInt / 10.0;
        // 格式化为字符串
        String s = size+"MB";
        return s;
    }



//    /**
//     *
//     * @param ip 服务器IP
//     * @param port 端口号
//     * @param username 服务器用户名
//     * @param password 服务器登录密码
//     * @param sourceFile 要下载的文件路径
//     */
//    private String ip = "192.168.35.205";
//    private int port = 22;
//    private String username = "root";
//    private String password = "Ly37621040";
//
//    /*mysql从服务器传输文件到minio*/
//    private void mysqlToMinioBySSH(String sourceFile){
//
//        String fileName = sourceFile.substring(sourceFile.lastIndexOf("/")+1);
//
//        byte[] bytes = SSHTransferUtil.copyFileToMinio(ip, port, username, password, sourceFile);
//        InputStream inputStream = new ByteArrayInputStream(bytes);
//
//        try {
//            minioClientUtils.uploadFile(minioConfig.getBucketName(),fileName,inputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    /*每上传一次minio，在时间戳表生成对应文件名记录*/
//    private void insertTimeStamp(Long timeStamp,String fileName){
//        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
//        Long timeStampId = IdWorker.getNextId();
//        timestampPo.setId(timeStampId);
//        timestampPo.setTimeStamp(String.valueOf(timeStamp));
//        timestampPo.setFileName(fileName);
//        timestampMapper.insert(timestampPo);
//    }






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


    public ByteArrayOutputStream inputToOutputStream(InputStream inputStream){
        ByteArrayOutputStream bos = null;
        try {
            //流先转化为byte数组存储
            BufferedInputStream bf = new BufferedInputStream(inputStream);
            bos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1 != (len = bf.read(buffer, 0, 1024))) {
                bos.write(buffer, 0, len);
            }
        }catch (Exception e){
            logger.error("流读取有误");

        }
        return bos;
    }

}












































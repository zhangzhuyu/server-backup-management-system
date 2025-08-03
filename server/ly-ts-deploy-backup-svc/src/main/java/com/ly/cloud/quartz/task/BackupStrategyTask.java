package com.ly.cloud.quartz.task;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.ly.cloud.backup.common.enums.BackupStatusEnums;
import com.ly.cloud.backup.common.enums.TaskModeEnums;
import com.ly.cloud.backup.config.MinioConfig;
import com.ly.cloud.backup.mapper.*;
import com.ly.cloud.backup.po.*;
import com.ly.cloud.backup.service.impl.DatabaseServiceImpl;
import com.ly.cloud.backup.util.*;
import com.ly.cloud.backup.vo.BackupStrategyLogicVo;
import com.ly.cloud.backup.vo.MiddlewareVo;
import com.ly.cloud.quartz.annotation.TaskNode;
import com.ly.cloud.quartz.util.BackupStrategyUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@TaskNode(taskName = "数据备份定时调度")
public class BackupStrategyTask implements Job {

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
    private MinioConfig minioConfig;

    @Autowired
    private MinioClientUtils minioClientUtils;

    @Autowired
    private BackupStrategyUtil backupStrategyUtil;

    private static final Logger logger = LoggerFactory.getLogger(BackupStrategyTask.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        DatabasePo databasePo = (DatabasePo) dataMap.get("databasePo");
        MiddlewarePo middlewarePo = (MiddlewarePo) dataMap.get("middlewarePo");
        LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo = (LyDbBackupStrategyRecordPo) dataMap.get("lyDbBackupStrategyRecordPo");
        LyDbBackupHistoryRecordPo historyRecordPo = (LyDbBackupHistoryRecordPo) dataMap.get("historyRecordPo");
        String dataBase = null;
        List<String> tablesList = (List<String>) dataMap.get("tablesList");

        BackupStrategyLogicVo backupStrategyLogicVo = new BackupStrategyLogicVo();
        backupStrategyLogicVo.setDatabasePo(databasePo);
        backupStrategyLogicVo.setMiddlewarePo(middlewarePo);
        backupStrategyLogicVo.setLyDbBackupStrategyRecordPo(lyDbBackupStrategyRecordPo);
        backupStrategyLogicVo.setHistoryRecordPo(historyRecordPo);
        backupStrategyLogicVo.setTablesList(tablesList);

        backupStrategyUtil.BackupStrategy(backupStrategyLogicVo);

//        //执行一次写入备份历史表
//        Runnable task = ()->{
//            historyRecordPo.setProportion("10");
//            historyRecordMapper.updateById(historyRecordPo);
//        };
//
//        //分开多次执行（如果不止一条）
//        String url1 = lyDbBackupStrategyRecordPo.getUrl();
//        String[] split1 = url1.split(",");
//        for (int j = 0; j < split1.length; j++) {
//            url1 = split1[j];
//            //执行单条备份
//            if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("2")) {
//                DatabasePo databasePo1 = databaseMapper.selectById(url1);
//                String s1 = databasePo1.getUrl().substring(0, databasePo1.getUrl().indexOf("?"));
//                dataBase = s1.substring(s1.lastIndexOf("/")+1);
//
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1")) {
//                DatabasePo databasePo1 = databaseMapper.selectById(url1);
////            dataBase = databasePo1.getUrl().substring(0, databasePo1.getUrl().indexOf("/"));
//                dataBase = databasePo1.getUser();
//
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")) {
//                Long a = Long.valueOf(url1);
//                MiddlewareVo middlewareVo = middlewareMapper.selectById(a);
//                //MiddlewarePo middlewarePo = middlewareMapper.selectById(url);
//                dataBase = middlewareVo.getUser();
//            }
//
//            Date date = new Date();
//            long timeStamp = date.getTime();
//            String format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(date);
//            String format2 = format.replaceAll(":","-");
//            String folderName = lyDbBackupStrategyRecordPo.getTitle()+"_"+format;
//            String folderName2 = lyDbBackupStrategyRecordPo.getTitle()+"_"+format2;
//            System.out.println("folderName================"+folderName);
//
//            if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("0")) {
//                String tablesString = null;
//                if (!CollectionUtils.isEmpty(tablesList)) {//判空
//                    tablesString = getTablesString(tablesList);//转化为空格隔开的表格名字符串
//                }
//                //oracle exp
//                //String s = jSchUtilOracle.execCommand("exp "+lyDbBackupStrategyRecordPo.getUrl()+ " file=/tmp/backup/oracle/"+folderName+"/"+format+".dmp"+ " log=/tmp/backup/oracle/"+folderName+"/"+format+".log statistics=none");
//                StringBuilder command = new StringBuilder();
//                String oracleUsername = "oracle12c";
//                String serverPassword = "Ly37621040";
//                String s1 =  "#!/bin/bash\n" +
//                        "yum -y install expect\n" +
//                        "expect -c \"\n" +
//                        "spawn su - " + oracleUsername + "\n" +
//                        "expect {\n" +
//                        "    \\\"*assword\\\" \n" +
//                        "                {\n" +
//                        "                    set timeout 300; \n" +
//                        "                    send \\\"" + serverPassword + "\\r\\\";\n" +
//                        "                }\n" +
//                        "    \\\"yes/no\\\" \n" +
//                        "                {\n" +
//                        "                    send \\\"yes\\r\\\"; exp_continue;}\n" +
//                        "                }\n" +
//                        "expect eof\";";
//                command.append(s1);
//                command.append("#!/bin/bash\n");
//                command.append("su - " + oracleUsername + " <<EOF\n");
//                //执行exp命令
//                if (tablesString == null) {
//                    command.append("exp "+databasePo.getUrl()+ " file=/tmp/backup/oracle/"+folderName+"/"+timeStamp+".dmp"+ " log=/tmp/backup/oracle/"+folderName+"/"+timeStamp+".log statistics=none"+" \n");
//                }else {
//                    command.append("exp "+databasePo.getUrl()+ " file=/tmp/backup/oracle/"+folderName+"/"+timeStamp+".dmp"+ " log=/tmp/backup/oracle/"+folderName+"/"+timeStamp+".log statistics=none"+" tables="+tablesString+" \n");
//                }
//                command.append("EOF;");
//                System.out.println("command:"+command.toString());
//                SshUtils.DestHost host = new SshUtils.DestHost("192.168.35.205", "root", "Ly37621040");
//                JSchUtil jSchUtil =  new JSchUtil("192.168.35.205","root", "Ly37621040");
//                String s2 = jSchUtil.execCommand("mkdir -p /tmp/backup/oracle/"+folderName);
//                String s3 = jSchUtil.execCommand("touch /tmp/backup/oracle/"+folderName+"/"+timeStamp+".dmp");
//                String s4 = jSchUtil.execCommand("chmod 777 -R /tmp/backup/oracle/"+folderName+"/"+timeStamp+".dmp");
//                String s5 = jSchUtil.execCommand("touch /tmp/backup/oracle/"+folderName+"/"+timeStamp+".log");
//                String s6 = jSchUtil.execCommand("chmod 777 -R /tmp/backup/oracle/"+folderName+"/"+timeStamp+".log");
//                Runnable task2 = ()->{
//                    historyRecordPo.setProportion("30");
//                    historyRecordMapper.updateById(historyRecordPo);
//                };
//                try {
//                    Session shellSession = SshUtils.getJSchSession(host);
//                    String stdout = SshUtils.execCommandByJSch(shellSession, String.valueOf(command));
//                    //断开链接
//                    shellSession.disconnect();
//                    System.out.println("stdout:"+stdout);
//
//                } catch (JSchException | IOException e) {
//                    e.printStackTrace();
//                }
//                Runnable task3 = ()->{
//                    historyRecordPo.setProportion("50");
//                    historyRecordMapper.updateById(historyRecordPo);
//                };
//
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
//
//                Runnable task4 = ()->{
//                    historyRecordPo.setProportion("80");
//                    historyRecordMapper.updateById(historyRecordPo);
//                };
//                //时间戳表格增加
//                //insertTimeStamp(timeStamp,folderName);
//
//
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("1")){
//                //oracle expdb
//                StringBuilder command = new StringBuilder();
//                String oracleUsername = "oracle12c";
//                String serverPassword = "Ly37621040";
//                String s1 =  "#!/bin/bash\n" +
//                        "yum -y install expect\n" +
//                        "expect -c \"\n" +
//                        "spawn su - " + oracleUsername + "\n" +
//                        "expect {\n" +
//                        "    \\\"*assword\\\" \n" +
//                        "                {\n" +
//                        "                    set timeout 300; \n" +
//                        "                    send \\\"" + serverPassword + "\\r\\\";\n" +
//                        "                }\n" +
//                        "    \\\"yes/no\\\" \n" +
//                        "                {\n" +
//                        "                    send \\\"yes\\r\\\"; exp_continue;}\n" +
//                        "                }\n" +
//                        "expect eof\";";
//                command.append(s1);
//                command.append("#!/bin/bash\n");
//                command.append("su - " + oracleUsername + " <<EOF\n");
//                //执行exp命令
//                StringBuilder querySql = new StringBuilder();
//
//                //连接Oracle数据库
//                String url = databasePo.getUrl();
//            String jdbc = "jdbc:oracle:thin:";
//            String url2 = jdbc+lyDbBackupStrategyRecordPo.getUrl();
//            String username2 = "system";
//            String passwd2 = "Ly37621040";
//            String oracleUser = url.substring(0, url.indexOf("/"));
//            /*try {
//                Connection conn = DriverManager.getConnection(url2,username2,passwd2);
//                Statement stmt = conn.createStatement();
//                //连接并执行数据库sql语句
//                boolean execute = stmt.execute("create directory "+format+" as '/app/oracle/admin/orcl/dpdump/"+format+"'");
//                boolean execute1 = stmt.execute("grant read,write on directory "+format+" TO LY_DG");
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//                String tablesString = null;
//                if (!CollectionUtils.isEmpty(tablesList)) {//判空
//                    tablesString = getTablesString(tablesList);//转化为空格隔开的表格名字符串
//                }
//                if (tablesString == null) {
//                    command.append("expdp "+url+" DIRECTORY="+format+" DUMPFILE="+timeStamp+".dmp"+" logfile="+timeStamp+".log\n");
//                    //command.append("expdp "+url+" DIRECTORY=DATA_PUMP_DIR DUMPFILE="+timeStamp+".dmp"+" logfile="+timeStamp+".log"+"\n");
//                }else {
//                    command.append("expdp "+url+" DIRECTORY="+format+" DUMPFILE="+timeStamp+".dmp"+" logfile="+timeStamp+".log TABLES="+tablesString+"\n");
//                    //command.append("expdp "+url+" DIRECTORY=DATA_PUMP_DIR DUMPFILE="+timeStamp+".dmp"+" logfile="+timeStamp+".log TABLES="+tablesString+"\n");
//                }*/
//
//                String tablesString = null;
//                if (!CollectionUtils.isEmpty(tablesList)) {//判空
//                    tablesString = getTablesString(tablesList);//转化为空格隔开的表格名字符串
//                }
//                if (tablesString == null) {
//                    command.append("expdp "+url+" DIRECTORY=DATA_PUMP_DIR DUMPFILE="+timeStamp+".dmp"+" logfile="+timeStamp+".log"+"\n");
//                }else {
//                    command.append("expdp "+url+" DIRECTORY=DATA_PUMP_DIR DUMPFILE="+timeStamp+".dmp"+" logfile="+timeStamp+".log TABLES="+tablesString+"\n");
//                }
//
//                command.append("EOF;");
//                System.out.println("command:"+command.toString());
//
//                Runnable task1 = ()->{
//                    historyRecordPo.setProportion("20");
//                    historyRecordMapper.updateById(historyRecordPo);
//                };
//
//                SshUtils.DestHost host = new SshUtils.DestHost("192.168.35.205", "root", "Ly37621040");
//
//                /*JSchUtil jSchUtil =  new JSchUtil("192.168.35.205","root", "Ly37621040");
//                String s2 = jSchUtil.execCommand("mkdir -p /app/oracle/admin/orcl/dpdump/"+format+" 2>&1");
//                String s3 = jSchUtil.execCommand("chmod -R 777 /app/oracle/admin/orcl/dpdump/"+format+" 2>&1");*/
//
//                try {
//                    Session shellSession = SshUtils.getJSchSession(host);
//                    String stdout = SshUtils.execCommandByJSch(shellSession, String.valueOf(command));
//                    //断开链接
//                    shellSession.disconnect();
//                    System.out.println("stdout:"+stdout);
//
//                } catch (JSchException | IOException e) {
//                    e.printStackTrace();
//                }
//                Runnable task2 = ()->{
//                    historyRecordPo.setProportion("30");
//                    historyRecordMapper.updateById(historyRecordPo);
//                };
//                //mysqlToMinioBySSH("/app/oracle/admin/orcl/dpdump/"+format+"/"+timeStamp+".dmp");
//
//                //上传到minio
//                String sourceFile1 = "/app/oracle/admin/orcl/dpdump/"+timeStamp+".dmp";
//                String sourceFile2 = "/app/oracle/admin/orcl/dpdump/"+timeStamp+".log";
//                //String s = jSchUtil.execCommand("ls /tmp/backup/mongo/"+folderName+"/"+dataBase);
//                //s=s.substring(0,s.indexOf(".")+5);
//                String ip = "192.168.35.205";
//                int port = 22;
//                String username = "root";
//                String password = "Ly37621040";
//                //源流
//                byte[] bytes1 = SSHTransferUtil.copyFileToMinio(ip, port, username, password, sourceFile1);
//                byte[] bytes2 = SSHTransferUtil.copyFileToMinio(ip, port, username, password, sourceFile2);
//                InputStream inputStream1 = new ByteArrayInputStream(bytes1);
//                InputStream inputStream2 = new ByteArrayInputStream(bytes2);
//
//                Runnable task3 = ()->{
//                    historyRecordPo.setProportion("40");
//                    historyRecordMapper.updateById(historyRecordPo);
//                };
//
//                Map<String,byte[]> bytesMap = new HashMap<>();
//                ByteArrayOutputStream byteArrayOutputStream1 = inputToOutputStream(inputStream1);
//                ByteArrayOutputStream byteArrayOutputStream2 = inputToOutputStream(inputStream2);
//                bytesMap.put(timeStamp+".dmp", byteArrayOutputStream1.toByteArray());
//                bytesMap.put(timeStamp+".log", byteArrayOutputStream2.toByteArray());
//                //压缩多个字节数组成一个压缩数组
//                byte[] zipBytes = null;
//                try {
//                    zipBytes = ListBytesToZipInWinUtil.listBytesToZip(bytesMap);
//                } catch (IOException e) {
//                    logger.error("压缩文件流出错");
//                }
//                InputStream inputStream = new ByteArrayInputStream(zipBytes);
//
//                Runnable task5 = ()->{
//                    historyRecordPo.setProportion("50");
//                    historyRecordMapper.updateById(historyRecordPo);
//                };
//                try {
//                    minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);
//                } catch (Exception e) {
//                    logger.error("minio上传出错");
//                }
//
//
//                Runnable task6 = ()->{
//                    historyRecordPo.setProportion("80");
//                    historyRecordMapper.updateById(historyRecordPo);
//                };
//                //insertTimeStamp(timeStamp,folderName);
//
//
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("2")){
//                //mysql
//                String tablesString = null;
//                if (!CollectionUtils.isEmpty(tablesList)) {//判空
//                    tablesString = getTablesString(tablesList);//转化为空格隔开的表格名字符串
//                }
//                JSchUtil jSchUtil = new JSchUtil("192.168.35.205","root","Ly37621040");
//                jSchUtil.execCommand("mkdir -p /tmp/backup/mysql/"+folderName);
//                String s = jSchUtil.execCommand("mysqldump "+"-h"+databasePo.getIpv4()+" --skip-opt"+" -u"+databasePo.getUser()+ " -p"+databasePo.getPassword()+" "+dataBase+" "+tablesString+"> /tmp/backup/mysql/"+folderName+"/"+timeStamp+".sql");
//                mysqlToMinioBySSH("/tmp/backup/mysql/"+folderName+"/"+timeStamp+".sql");
//                //insertTimeStamp(timeStamp,folderName);
//
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")) {
//                //mongodb
//                JSchUtil jSchUtil = new JSchUtil("192.168.35.205","root","Ly37621040");
//                for (int i = 0; i < tablesList.size(); i++) {
//                    System.out.println(tablesList.get(i));
//                    jSchUtil.execCommand("mongodump --host "+middlewarePo.getIp()+" --port "+middlewarePo.getPort()+" --username "+middlewarePo.getUser()+" --password "+middlewarePo.getPassword()+" --authenticationDatabase "+dataBase+" --db "+dataBase+" --out /tmp/backup/mongo/"+folderName);
//                    //上传到minio
//                    String sourceFile = "/tmp/backup/mongo/"+folderName+"/"+dataBase;
//                    //String s = jSchUtil.execCommand("ls /tmp/backup/mongo/"+folderName+"/"+dataBase);
//                    //s=s.substring(0,s.indexOf(".")+5);
//                    String ip = "192.168.35.205";
//                    int port = 22;
//                    String username = "root";
//                    String password = "Ly37621040";
//                    //源流
//                    byte[] bytes = SSHTransferUtil.copyFileToMinio(ip, port, username, password, sourceFile);
//                    InputStream inputStream = new ByteArrayInputStream(bytes);
//
//                    try {
//                        minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    //mysqlToMinioBySSH("/tmp/backup/mongo/"+folderName+"/"+dataBase+"/"+s);
//                    //insertTimeStamp(timeStamp,folderName);
//                }
//
//            }
//
//            /*如果被备份成功，在策略表里更新操作时间*/
//            JSchUtil jSchUtil =  new JSchUtil("192.168.35.205","root", "Ly37621040");
//            String s1 = jSchUtil.execCommand("ls /tmp/backup/oracle/" + folderName);
//            String s2 = jSchUtil.execCommand("ls /tmp/backup/mysql/" + folderName);
//            String s3 = jSchUtil.execCommand("ls /tmp/backup/mongo/" + folderName);
//            String s4 = jSchUtil.execCommand("find /app/oracle/admin/orcl/dpdump/" + timeStamp+".log");
//
//            if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("2")) {
//                historyRecordPo.setTimeStamp(String.valueOf(timeStamp+".sql"));
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("1")) {
//                historyRecordPo.setTimeStamp(String.valueOf(timeStamp+".zip"));
//            } else if (lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")) {
//                historyRecordPo.setTimeStamp(String.valueOf(timeStamp+".zip"));
//            }
//            //historyRecordPo.setStrategyId(String.valueOf(lyDbBackupStrategyRecordPo.getId()));
//
//            historyRecordPo.setBackupStatus(BackupStatusEnums.getValue(0));
//
//
//            //mysql
//            if (StringUtils.hasText(s1+s2+s3) && lyDbBackupStrategyRecordPo.getDataSourceType().equals("2")) {
//                lyDbBackupStrategyRecordPo.setOperationTime(new Date());
//                lyDbBackupStrategyRecordPo.setEnable("1");
//                lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);
//
//                historyRecordPo.setBackupStatus(BackupStatusEnums.getValue(1));
//            }
//            //oracleexp
//            if (StringUtils.hasText(s1+s2+s3) && lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("0")) {
//                lyDbBackupStrategyRecordPo.setOperationTime(new Date());
//                lyDbBackupStrategyRecordPo.setEnable("1");
//                lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);
//
//                historyRecordPo.setBackupStatus(BackupStatusEnums.getValue(1));
//            }
//            //oracleexpdb
//            if (StringUtils.hasText("/app/oracle/admin/orcl/dpdump/" + timeStamp+".log") && lyDbBackupStrategyRecordPo.getDataSourceType().equals("1") && lyDbBackupStrategyRecordPo.getExpdb().equals("1")) {
//                lyDbBackupStrategyRecordPo.setOperationTime(new Date());
//                lyDbBackupStrategyRecordPo.setEnable("1");
//                lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);
//
//                historyRecordPo.setBackupStatus(BackupStatusEnums.getValue(1));
//            }
//            //mongo
//            if (StringUtils.hasText(s1+s2+s3) && lyDbBackupStrategyRecordPo.getDataSourceType().equals("6")) {
//                lyDbBackupStrategyRecordPo.setOperationTime(new Date());
//                lyDbBackupStrategyRecordPo.setEnable("1");
//                lyDbBackupStrategyRecordMapper.updateById(lyDbBackupStrategyRecordPo);
//
//                historyRecordPo.setBackupStatus(BackupStatusEnums.getValue(1));
//            }
//
//            Runnable task7 = ()->{
//                historyRecordPo.setProportion("100");
//                historyRecordMapper.updateById(historyRecordPo);
//            };
//
//        }


    }


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        //getTablesString(list);
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



    /**
     *
     * @param ip 服务器IP
     * @param port 端口号
     * @param username 服务器用户名
     * @param password 服务器登录密码
     * @param sourceFile 要下载的文件路径
     */
    private String ip = "192.168.35.205";
    private int port = 22;
    private String username = "root";
    private String password = "Ly37621040";

    /*mysql从服务器传输文件到minio*/
    private void mysqlToMinioBySSH(String sourceFile){

        String fileName = sourceFile.substring(sourceFile.lastIndexOf("/")+1);

        byte[] bytes = new byte[0];
        try {
            bytes = SSHTransferUtil.copyFileToMinio(ip, port, username, password, sourceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream inputStream = new ByteArrayInputStream(bytes);

        try {
            minioClientUtils.uploadFile(minioConfig.getBucketName(),fileName,inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*每上传一次minio，在时间戳表生成对应文件名记录*/
    private void insertTimeStamp(Long timeStamp,String fileName){
        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
        Long timeStampId = IdWorker.getNextId();
        timestampPo.setId(timeStampId);
        timestampPo.setTimeStamp(String.valueOf(timeStamp));
        timestampPo.setFileName(fileName);
        timestampMapper.insert(timestampPo);
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



































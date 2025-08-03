package com.ly.cloud.quartz.task;

import com.ly.cloud.backup.common.enums.BackupMethodEnums;
import com.ly.cloud.backup.common.enums.BackupStatusEnums;
import com.ly.cloud.backup.common.enums.TaskModeEnums;
import com.ly.cloud.backup.config.MinioConfig;
import com.ly.cloud.backup.dto.HttpResponseDto;
import com.ly.cloud.backup.mapper.*;
import com.ly.cloud.backup.po.*;
import com.ly.cloud.backup.service.impl.DatabaseServiceImpl;
import com.ly.cloud.backup.util.*;
import com.ly.cloud.backup.vo.BackupStrategyLogicVo;
import com.ly.cloud.quartz.util.CatalogueBackupUtil;
import jcifs.smb.SmbFile;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CatalogueBackupTask implements Job {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    @Autowired
    private LyDbBackupStrategyRecordMapper lyDbBackupStrategyRecordMapper;

    @Autowired
    private LyDbBackupHistoryRecordMapper historyRecordMapper;

    @Autowired
    private LyDbBackupTimestampMapper timestampMapper;

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private MiddlewareMapper middlewareMapper;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClientUtils minioClientUtils;

    @Autowired
    private CatalogueBackupUtil catalogueBackupUtil;


    public static void main(String[] args) {
        Long ruleId = IdWorker.getNextId();
        System.out.println(ruleId);
        long time = new Date().getTime();
        System.out.println(time);
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        DatabasePo databasePo = (DatabasePo) dataMap.get("databasePo");
        MiddlewarePo middlewarePo = (MiddlewarePo) dataMap.get("middlewarePo");
        ServerPo serverPo = (ServerPo) dataMap.get("serverPo");
        LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo = (LyDbBackupStrategyRecordPo) dataMap.get("lyDbBackupStrategyRecordPo");
        LyDbBackupHistoryRecordPo historyRecordPo = (LyDbBackupHistoryRecordPo) dataMap.get("historyRecordPo");

        if(historyRecordPo!=null && historyRecordPo.getAuthDeptId()==null){ //2023-12-01增加部门id
            historyRecordPo.setAuthDeptId(lyDbBackupStrategyRecordPo.getAuthDeptId());
        }
        String dataBase = null;
        List<String> tablesList = (List<String>) dataMap.get("tablesList");

        BackupStrategyLogicVo backupStrategyLogicVo = new BackupStrategyLogicVo();
        backupStrategyLogicVo.setDatabasePo(databasePo);
        backupStrategyLogicVo.setMiddlewarePo(middlewarePo);
        backupStrategyLogicVo.setServerPo(serverPo);
        backupStrategyLogicVo.setLyDbBackupStrategyRecordPo(lyDbBackupStrategyRecordPo);
        backupStrategyLogicVo.setHistoryRecordPo(historyRecordPo);
        backupStrategyLogicVo.setTablesList(tablesList);

        catalogueBackupUtil.CatalogueBackupStrategy(backupStrategyLogicVo);



        /*try {
            JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
            LyDbBackupStrategyRecordPo recordPo = (LyDbBackupStrategyRecordPo) dataMap.get("recordPo");

            catalogueBackupUtil.CatalogueBackupStrategy(recordPo);


            *//*判断是哪种备份方式，开始备份*//*
            String format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
            String folderName = recordPo.getTitle()+"_"+format;

            //timeNameId 时间戳表格主键
            //Long timeStampId = IdWorker.getNextId();
            // 获取当前日期对应的时间戳
            long timeStamp = new Date().getTime();
            int add = 1;


            //http
            if (recordPo.getBackupMethod().equals(BackupMethodEnums.getCode("http"))) {

                String uri = recordPo.getBackupTarget();

                //分开多次执行（如果不止一条）
                String[] split1 = uri.split(",");
                for (int j = 0; j < split1.length; j++) {
                    //timeNameId 时间戳表格主键
                    Long timeStampId = IdWorker.getNextId();
                    String timeStamp1 = timeStamp+"("+add+")";
                    add++;
                    uri = split1[j];
                    String saveDirPath = "F:\\photo";
                    String fileName = folderName+".png";
                    //获取流
                    HttpResponseDto httpResponseDto = HttpClientToInterface.getHttpResponse(uri);
                    byte[] bytes = getZipByte(httpResponseDto.getBytes(), httpResponseDto.getDecodedUrl());
                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    //从响应对象获取流，用流的方式下载文件到本地
                    //HttpClientToInterface.StreamDownload(inputStream,saveDirPath,fileName);
                    //将文件上传至minio
                    try {
                        minioClientUtils.uploadFile(minioConfig.getBucketName(), timeStamp1+".zip", inputStream);
                        //每上传一次minio，在时间戳表生成对应文件名记录
                        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
                        timestampPo.setId(timeStampId);
                        timestampPo.setTimeStamp(timeStamp1);
                        timestampPo.setFileName(httpResponseDto.getDecodedUrl());
                        timestampMapper.insert(timestampPo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                //ssh
            } else if (recordPo.getBackupMethod().equals(BackupMethodEnums.getCode("ssh"))) {
                int port = 22;
                String url = recordPo.getUrl();
                ServerPo serverPo = serverMapper.selectById(url);
                String password = "";
                try {
                    password = Sm4Util.sm4EcbDecrypt(serverPo.getPassword());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String sourceFile = recordPo.getBackupTarget();//可以文件，也可以文件夹(分类讨论)
                //String fileName = "SSHTest.zip";//压缩包的命名，扩展名一定是zip
                //String name = "SSHTest.zip";//里面文件的命名

                //分开多次执行（如果不止一条）
                String[] split1 = sourceFile.split(",");
                for (int j = 0; j < split1.length; j++) {
                    //timeNameId 时间戳表格主键
                    Long timeStampId = IdWorker.getNextId();
                    String timeStamp1 = timeStamp+"("+add+")";
                    add++;
                    sourceFile = split1[j];

                    //源流
                    byte[] bytes = SSHTransferUtil.copyFileToMinio(serverPo.getIpv4(), Integer.parseInt(serverPo.getPort()), serverPo.getUser(), password, sourceFile);

                    //分类讨论，有可能是文件夹有可能是文件
                    String sourceFileSuffix = sourceFile.substring(sourceFile.lastIndexOf("/")+1);
                    boolean contains = sourceFileSuffix.contains(".");
                    if (contains){
                        //压缩流
                        bytes = getZipByte(bytes,sourceFileSuffix);
                    }

                    InputStream inputStream = new ByteArrayInputStream(bytes);

                    try {
                        boolean b = sourceFileSuffix.contains(".");
                        if (contains){
                            sourceFileSuffix = sourceFileSuffix.substring(0, sourceFileSuffix.lastIndexOf("."));
                        }
                        minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp1+".zip",inputStream);

                        //每上传一次minio，在时间戳表生成对应文件名记录
                        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
                        timestampPo.setId(timeStampId);
                        timestampPo.setTimeStamp(timeStamp1);
                        timestampPo.setFileName(sourceFileSuffix+".zip");
                        timestampMapper.insert(timestampPo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //ftp
            } else if (recordPo.getBackupMethod().equals(BackupMethodEnums.getCode("ftp"))) {
                String url = recordPo.getUrl();
                MiddlewarePo middlewarePo = middlewareMapper.selectById(url);
                String ftpIp = middlewarePo.getIp();
                int ftpPort = 21;//只能使用21端口
                String ftpUserName = middlewarePo.getUser();
                String ftpPassWord = middlewarePo.getPassword();
                //ftb保存到本机中转的路径
                //String localPath = "E:";//最后设置为 C盘
                String sourceFile = recordPo.getBackupTarget();

                //分开多次执行（如果不止一条）
                String[] split1 = sourceFile.split(",");
                for (int j = 0; j < split1.length; j++) {
                    //timeNameId 时间戳表格主键
                    Long timeStampId = IdWorker.getNextId();
                    String timeStamp1 = timeStamp+"("+add+")";
                    add++;
                    sourceFile = split1[j];
                    // zipFilePath = localPath+sourceFile.substring(0,sourceFile.lastIndexOf("/"))+sourceFile.substring(sourceFile.lastIndexOf("/"))+".zip";
                    String sourceFilePrefix = sourceFile.substring(0,sourceFile.lastIndexOf("/"));
                    String sourceFileSuffix = sourceFile.substring(sourceFile.lastIndexOf("/")+1);
                    boolean contains = sourceFileSuffix.contains(".");

                    //判断是文件还是文件夹
                    byte[] bytes = null;
                    if (contains) {//contains为true，是文件
                        //连接ftp服务器layer length loan lounge machine majority manufacture marsh maturity media medication medicine mineral minute
                        FtpUtil2.connectServer(ftpIp,ftpPort,ftpUserName,ftpPassWord,sourceFilePrefix);
                        //获取文件的流
                        bytes = FtpUtil2.downloadMinio(sourceFileSuffix);
                        bytes = getZipByte(bytes,sourceFileSuffix);

                    }else {//contains为false，是文件夹
                        try {
                            //连接ftp服务器
                            FtpUtil2.loginFtp3(ftpIp,ftpPort,ftpUserName,ftpPassWord);
                            //连接ftp服务器
                            FtpUtil2.connectServer(ftpIp,ftpPort,ftpUserName,ftpPassWord,sourceFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bytes = FtpUtil2.downloadMinioMoreFile(sourceFile);
                    }

                    //将文件上传至minio
                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    try {
                        minioClientUtils.uploadFile(minioConfig.getBucketName(), timeStamp1+".zip", inputStream);
                        //每上传一次minio，在时间戳表生成对应文件名记录
                        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
                        timestampPo.setId(timeStampId);
                        timestampPo.setTimeStamp(timeStamp1);
                        timestampPo.setFileName(sourceFileSuffix.contains(".")?sourceFileSuffix.substring(0,sourceFileSuffix.lastIndexOf("."))+".zip":sourceFileSuffix+".zip");
                        timestampMapper.insert(timestampPo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //cifs
            }else if (recordPo.getBackupMethod().equals(BackupMethodEnums.getCode("cifs"))) {
                String sourceFile = recordPo.getBackupTarget();

                //分开多次执行（如果不止一条）
                String[] split1 = sourceFile.split(",");
                for (int j = 0; j < split1.length; j++) {
                    //timeNameId 时间戳表格主键
                    Long timeStampId = IdWorker.getNextId();
                    String timeStamp1 = timeStamp+"("+add+")";
                    add++;

                    sourceFile = split1[j];
                    String sourceFile2 = sourceFile;
                    if (sourceFile.substring(sourceFile.length() - 1).equals("/")) {
                        sourceFile2 = sourceFile.substring(0,sourceFile.length() - 1);
                    }
                    String sourceFilePrefix = sourceFile2.substring(0,sourceFile2.lastIndexOf("/"));
                    String sourceFileSuffix = sourceFile2.substring(sourceFile2.lastIndexOf("/")+1);
                    String zipSourceFileSuffix = sourceFileSuffix+".zip";

                    boolean file = true;
                    try {
                        SmbFile remoteFile = new SmbFile(sourceFile);
                        file = remoteFile.isFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    byte[] bytes = null;
                    if (file) {//是文件
                        bytes = CIFSUtil.getRemoteFile(sourceFile);
                        bytes = getZipByte(bytes, sourceFileSuffix);

                    }else {//是文件夹

                        if (!sourceFile.substring(sourceFile.length() - 1).equals("/")) {
                            sourceFile = sourceFile+"/";
                        }
                        bytes = CIFSUtil.getRemoteMoreFile(sourceFile);

                    }

                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    try {
                        minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp1+".zip",inputStream);
                        //每上传一次minio，在时间戳表生成对应文件名记录
                        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
                        timestampPo.setId(timeStampId);
                        timestampPo.setTimeStamp(timeStamp1);
                        timestampPo.setFileName(zipSourceFileSuffix);
                        timestampMapper.insert(timestampPo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            //执行一次写入备份历史表
            LyDbBackupHistoryRecordPo historyRecordPo = new LyDbBackupHistoryRecordPo();
            historyRecordPo.setTitle(recordPo.getTitle());
            historyRecordPo.setBackupStrategyType(TaskModeEnums.getValue(Integer.valueOf(recordPo.getTaskMode())));
            historyRecordPo.setOperationTime(new Date());
            historyRecordPo.setBackupStatus(BackupStatusEnums.getValue(0));
            *//*如果被备份成功，在策略表里更新操作时间*//*
            recordPo.setOperationTime(new Date());
            lyDbBackupStrategyRecordMapper.updateById(recordPo);

            historyRecordPo.setBackupStatus(BackupStatusEnums.getValue(1));
            int insert = historyRecordMapper.insert(historyRecordPo);

        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }*/

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
    private List<String> addFtpFileList(List<String> sourceFileList, String localSourceFile){
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


}








































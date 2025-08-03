package com.ly.cloud.quartz.util;

import com.jcraft.jsch.*;
import com.ly.cloud.backup.common.enums.BackupMethodEnums;
import com.ly.cloud.backup.common.enums.BackupStatusEnums;
import com.ly.cloud.backup.common.enums.TaskModeEnums;
import com.ly.cloud.backup.config.MinioConfig;
import com.ly.cloud.backup.config.RedisConf;
import com.ly.cloud.backup.dto.HttpResponseDto;
import com.ly.cloud.backup.exception.ServiceException;
import com.ly.cloud.backup.mapper.*;
import com.ly.cloud.backup.po.*;
import com.ly.cloud.backup.util.*;
import com.ly.cloud.backup.vo.BackupStrategyLogicVo;
import com.ly.cloud.backup.vo.SelectVo;
import com.ly.cloud.quartz.task.BackupStrategyTask;
import com.xkzhangsan.time.cron.CronExpressionUtil;
import jcifs.smb.SmbFile;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ly.cloud.backup.common.constant.RedisConstants.REDIS_KEY_PREFIX_TC_BACKUP_EXPIRATION_TIMES;

@Component
public class CatalogueBackupUtil {

    private static final Logger logger = LoggerFactory.getLogger(BackupStrategyTask.class);

    private ConcurrentHashMap<String, BackupProgressEntity> progress = new ConcurrentHashMap<>(2);

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
    private BackupManagementMapper backupManagementMapper;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClientUtils minioClientUtils;

    @Autowired
    public RedisConf redisConf;

    String redisKey = REDIS_KEY_PREFIX_TC_BACKUP_EXPIRATION_TIMES;

    public void CatalogueBackupStrategy(BackupStrategyLogicVo backupStrategyLogicVo){

        MiddlewarePo middlewarePo = backupStrategyLogicVo.getMiddlewarePo();
        ServerPo serverPo = backupStrategyLogicVo.getServerPo();
        LyDbBackupStrategyRecordPo recordPo = backupStrategyLogicVo.getLyDbBackupStrategyRecordPo();
        LyDbBackupHistoryRecordPo historyRecordPo = backupStrategyLogicVo.getHistoryRecordPo();
        List<String> backupTargetList = backupStrategyLogicVo.getBackupTargetList();
        BackupManagementPo backupManagementPo = null;
//        if (recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("ssh")))) {
//            backupManagementPo = backupManagementMapper.selectById(2002);
//        } else if (recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("ftp")))) {
//            backupManagementPo = backupManagementMapper.selectById(2003);
//        }else if (recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("cifs")))) {
//            backupManagementPo = backupManagementMapper.selectById(2004);
//        }
        backupManagementPo = backupManagementMapper.selectById(recordPo.getLoginUrl());
        //单独判断ssh的备份方式是否过期或超出备份失败次数，如果失败直接返回不执行任何操作
        String sshId = null;
        if (!recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("http"))) && (recordPo.getUrl()!=null || !recordPo.getUrl().equals(""))) {
            sshId = recordPo.getUrl().substring(0,recordPo.getUrl().length()-1);
        }

        try {
            /*判断是哪种备份方式，开始备份*/
            String sourceFile = recordPo.getBackupTarget();//可以文件，也可以文件夹(分类讨论)
            String[] split1 = sourceFile.split(";");
            for (int i = 0; i <split1.length; i++) {
                //timeNameId 时间戳表格主键
                //Long timeStampId = IdWorker.getNextId();
                // 获取当前日期对应的时间戳
                long timeStamp = new Date().getTime();
                int add = 1;
                //执行一次写入备份历史表
                if(historyRecordPo==null){//2023-12-01 增加部门id
                    historyRecordPo = new LyDbBackupHistoryRecordPo();
                    historyRecordPo.setAuthDeptId(recordPo.getAuthDeptId());
                }
                historyRecordPo.setId(IdWorker.getNextId());
                historyRecordPo.setTitle(recordPo.getTitle());
                historyRecordPo.setBackupStrategyType(String.valueOf(TaskModeEnums.getCode(TaskModeEnums.getValue(Integer.valueOf(recordPo.getTaskMode())))));
                historyRecordPo.setBackupTime(new Date());
                historyRecordPo.setOperationTime(new Date());
                historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("正在备份")));
                historyRecordPo.setProportion("0");
                historyRecordPo.setStrategyId(String.valueOf(recordPo.getId()));
                historyRecordPo.setOperatorId(recordPo.getOperatorId());
                historyRecordPo.setBackupWay(recordPo.getBackupWay());
                historyRecordPo.setTotalMethod(recordPo.getTotalMethod());
                historyRecordPo.setUrl(recordPo.getUrl());
                historyRecordPo.setBackupMethod(recordPo.getBackupMethod());
                historyRecordPo.setBackupTarget(recordPo.getBackupTarget());
                historyRecordPo.setOperatingCycle(recordPo.getOperatingCycle());
                historyRecordPo.setRunTime(recordPo.getRunTime());
                historyRecordPo.setTaskMode(recordPo.getTaskMode());
                historyRecordMapper.insert(historyRecordPo);

                //执行一次更改策略表
                if (recordPo.getCron()!=null && CronExpressionUtil.isValidExpression(recordPo.getCron())) {
                    Date nextTime = CronExpressionUtil.getNextTime(recordPo.getCron());
                    recordPo.setOperationTime(nextTime);
                    lyDbBackupStrategyRecordMapper.updateById(recordPo);

                } else if (recordPo.getCron() == null) {
                    recordPo.setOperationTime(new Date());
                    lyDbBackupStrategyRecordMapper.updateById(recordPo);
                }

                Date date = new Date();
                timeStamp = date.getTime();
                String format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(date);
                String format2 = format.replaceAll(":","-");
                String folderName = recordPo.getTitle()+"_"+format;
                String folderName2 = recordPo.getTitle()+"_"+format2;
                System.out.println("folderName================"+folderName);

//                if (backupManagementPo!=null && (backupManagementPo.getConnectionFailTimes() == null || backupManagementPo.getConnectionFailTimes().equals(""))) {
//                    backupManagementPo.setConnectionFailTimes("9999");
//                }
//                if (backupManagementPo!=null && (backupManagementPo.getExpirationTime() == null || backupManagementPo.getExpirationTime().equals(""))) {
//                    backupManagementPo.setExpirationTime("9999");
//                }
//                if (!recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("http")))) {
//                    Integer times = redisConf.getByBound(redisKey + sshId);
//                    if (times == null) {
//                        redisConf.incr(redisKey+sshId,1);
//                        redisConf.expireByDays(redisKey+sshId, Long.parseLong(backupManagementPo.getExpirationTime()));
//                    }else if (times >= Integer.parseInt(backupManagementPo.getConnectionFailTimes())+1) {
//                        throw new ServiceException("服务器连接次数超过阈值，请先测试服务器连接是否正常");
//                    }
//                }

                //http
                if (recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("http")))) {
                    //执行一次写入备份历史表
                    //判断是否执行了停止备份，如果执行了则不继续更新
                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("正在备份")));
                        historyRecordPo.setOperationTime(date);
                        historyRecordPo.setProportion("20");
                        historyRecordPo.setTimeStamp(timeStamp+".zip");
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    //String uri = recordPo.getBackupTarget();
                    //分开多次执行（如果不止一条）
                    //String[] split1 = uri.split(",");
                    //timeNameId 时间戳表格主键
                    //Long timeStampId = IdWorker.getNextId();
                    //String timeStamp1 = timeStamp+"("+add+")";
                    //add++;
                    String uri = split1[i];
                    String saveDirPath = "F:\\photo";
                    String fileName = folderName+".png";
                    //获取流
                    HttpResponseDto httpResponseDto = HttpClientToInterface.getHttpResponse(uri);
                    //数据有可能在response里面
                    if (httpResponseDto == null) {
                        //throw new ServiceException("url非法");
                        httpResponseDto = HttpClientToInterface.getHttpFile(uri);
                    }

                    byte[] bytes = getZipByte(httpResponseDto.getBytes(), httpResponseDto.getDecodedUrl());
                    InputStream inputStream = new ByteArrayInputStream(bytes);

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("50");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    //从响应对象获取流，用流的方式下载文件到本地
                    //HttpClientToInterface.StreamDownload(inputStream,saveDirPath,fileName);
                    //将文件上传至minio
                    minioClientUtils.uploadFile(minioConfig.getBucketName(), timeStamp+".zip", inputStream);

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("80");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    /*try {
                        minioClientUtils.uploadFile(minioConfig.getBucketName(), timeStamp1+".zip", inputStream);
                        //每上传一次minio，在时间戳表生成对应文件名记录
                        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
                        timestampPo.setId(timeStampId);
                        timestampPo.setTimeStamp(timeStamp1);
                        timestampPo.setFileName(httpResponseDto.getDecodedUrl());
                        timestampMapper.insert(timestampPo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    //ssh
                } else if (recordPo.getBackupMethod().equals("6")) {
                    int port = 22;
                    String url = recordPo.getUrl();
                    //ServerPo serverPo = serverMapper.selectById(url);
                    String password = "";

                    //执行一次写入备份历史表
                    //判断是否执行了停止备份，如果执行了则不继续更新
                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("正在备份")));
                        historyRecordPo.setOperationTime(date);
                        historyRecordPo.setProportion("20");
                        historyRecordPo.setTimeStamp(timeStamp+".zip");
                        historyRecordPo.setSize(RandomSizeGenerator());
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    if (serverPo.getPassword() == null) {
                        throw new ServiceException("服务器密码不能为空");
                    }
                    password = Sm4Util.sm4EcbDecrypt(serverPo.getPassword());

                    //String sourceFile = recordPo.getBackupTarget();//可以文件，也可以文件夹(分类讨论)
                    //String fileName = "SSHTest.zip";//压缩包的命名，扩展名一定是zip
                    //String name = "SSHTest.zip";//里面文件的命名

                    //分开多次执行（如果不止一条）
                    //String[] split1 = sourceFile.split(",");
                    //timeNameId 时间戳表格主键
                    //Long timeStampId = IdWorker.getNextId();
                    //String timeStamp1 = timeStamp+"("+add+")";
                    //add++;
                    sourceFile = split1[i];
                    if (sourceFile.equals("/")) {
                        throw new ServiceException("服务器不可备份根目录");
                    }
                    //确保backupTarget前面有 “/”
                    if (sourceFile.charAt(0) != '/') {
                        sourceFile = "/" + sourceFile;
                    }


                    Path tempFilePath = null; // 用于存储临时下载或压缩的文件
                    Session sourceSession = null;
                    ChannelSftp sourceChannelSftp = null;
                    Session destSession = null;
                    ChannelSftp destChannelSftp = null;
                    String finalDestFileName = timeStamp+".zip";

                    try {
                        // 1. 连接源服务器并准备临时文件
                        sourceSession = connectSession(serverPo.getUser(),password,serverPo.getIpv4(), Integer.parseInt(serverPo.getPort()));
                        sourceChannelSftp = (ChannelSftp) sourceSession.openChannel("sftp");
                        sourceChannelSftp.connect();

                        String sourceRemotePath = recordPo.getBackupTarget();
                        sourceRemotePath = sourceRemotePath.substring(0, sourceRemotePath.length() - 1);
                        SftpATTRS sourceAttrs = sourceChannelSftp.lstat(sourceRemotePath);
                        String baseName = Paths.get(sourceRemotePath).getFileName().toString(); // 获取原始文件名或目录名

                        if (sourceAttrs.isDir()) {
                            // 源是目录 -> 下载并压缩到临时文件
                            tempFilePath = Files.createTempFile("transfer-", ".zip");
                            try (FileOutputStream fos = new FileOutputStream(tempFilePath.toFile());
                                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                                addDirectoryToZip(sourceChannelSftp, sourceRemotePath, zos, "");
                            }
                            baseName += ".zip"; // 上传时使用 zip 文件名
                        } else {
                            // 源是文件 -> 直接下载到临时文件
                            tempFilePath = Files.createTempFile("transfer-", "-" + baseName);
                            try (OutputStream os = Files.newOutputStream(tempFilePath)) {
                                sourceChannelSftp.get(sourceRemotePath, os);
                            }
                        }
                        disconnectSftp(sourceChannelSftp, sourceSession); // 完成源操作，断开连接
                        sourceChannelSftp = null;
                        sourceSession = null;


                        // 2. 连接目标服务器并上传临时文件
                        destSession = connectSession(backupManagementPo.getUser(),Sm4Util.sm4EcbDecrypt(backupManagementPo.getPassword()),backupManagementPo.getIpv4(), Integer.parseInt(backupManagementPo.getPort()));
                        destChannelSftp = (ChannelSftp) destSession.openChannel("sftp");
                        destChannelSftp.connect();

                        String destRemoteBaseDir = "/data/backup/ssh"; // 目标服务器的基础路径
                        // 确保目标基础目录存在
                        ensureRemoteDirectoryExists(destChannelSftp, destRemoteBaseDir);

                        // 目标文件路径 = 目标基础路径 + 源文件/压缩包名
                        String destinationRemotePath = combineUnixPaths(destRemoteBaseDir, finalDestFileName);

                        destChannelSftp.put(tempFilePath.toString(), destinationRemotePath);

                    } catch (SftpException e) {
                        throw new RuntimeException("SFTP error: " + e.getMessage(), e);
                    } catch (JSchException e) {
                        throw new RuntimeException("Connection error: " + e.getMessage(), e);
                    } catch (IOException e) {
                        throw new RuntimeException("IO error: " + e.getMessage(), e);
                    } finally {
                        // 清理连接和临时文件
                        disconnectSftp(sourceChannelSftp, sourceSession); // 再次尝试关闭，以防中途失败
                        disconnectSftp(destChannelSftp, destSession);
                        if (tempFilePath != null) {
                            try {
                                Files.deleteIfExists(tempFilePath);
                            } catch (IOException e) {
                                logger.error("Failed to delete temporary file: {}", tempFilePath, e);
                            }
                        }
                    }

//                    //检查ssh是否连接得上(复用测试连接代码)
//                    JSchUtil jSchUtil = new JSchUtil(serverPo.getIpv4(),serverPo.getUser(),password,UsualUtil.getInt(serverPo.getPort()),10 * 60 * 1000);
//                    boolean b = jSchUtil.loginSftp();
//                    if (!b) {
//                        throw new ServiceException("服务器无法连接:"+serverPo.getIpv4());
//                    }
//                    String s = jSchUtil.execCommand("ls -F " + sourceFile+" 2>&1");
//                    if (s.contains("No such file or directory")) {
//                        throw new ServiceException("文件或文件夹不存在:"+sourceFile);
//                    } else if (s.equals(sourceFile + "\n")) {
//                        System.out.println("这是个文件");
//                    }else {
//                        System.out.println("这是个文件夹");
//                    }
//
//                    //源流
//                    //在这个copyFileToMinio方法中，如果是文件夹已经被压缩了，如果是文件则没有被压缩
//                    byte[] bytes = SSHTransferUtil.copyFileToMinio(serverPo.getIpv4(), Integer.parseInt(serverPo.getPort()), serverPo.getUser(), password, sourceFile);
//
//                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
//                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
//                        historyRecordPo.setProportion("50");
//                        historyRecordMapper.updateById(historyRecordPo);
//                    }
//
//                    //分类讨论，有可能是文件夹有可能是文件
//                    //如果是文件，则需要压缩
//                    String sourceFileSuffix = sourceFile.substring(sourceFile.lastIndexOf("/")+1);
//                    boolean contains = sourceFileSuffix.contains(".");
//                    if (s.equals(sourceFile + "\n")){
//                        //压缩流
//                        bytes = getZipByte(bytes,sourceFileSuffix);
//                    }
//
//                    InputStream inputStream = new ByteArrayInputStream(bytes);
//
//                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
//                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
//                        historyRecordPo.setProportion("80");
//                        historyRecordMapper.updateById(historyRecordPo);
//                    }
//
//                    minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);

                    /*try {
                        boolean b = sourceFileSuffix.contains(".");
                        if (contains){
                            sourceFileSuffix = sourceFileSuffix.substring(0, sourceFileSuffix.lastIndexOf("."));
                        }
                        minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);
                        //每上传一次minio，在时间戳表生成对应文件名记录
                        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
                        timestampPo.setId(timeStampId);
                        timestampPo.setTimeStamp(timeStamp);
                        timestampPo.setFileName(sourceFileSuffix+".zip");
                        timestampMapper.insert(timestampPo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    //ftp
                } else if (recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("ftp")))) {
                    //执行一次写入备份历史表
                    //判断是否执行了停止备份，如果执行了则不继续更新
                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("正在备份")));
                        historyRecordPo.setOperationTime(date);
                        historyRecordPo.setProportion("20");
                        historyRecordPo.setTimeStamp(timeStamp+".zip");
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    String url = recordPo.getUrl();
                    //MiddlewarePo middlewarePo = middlewareMapper.selectById(url);
                    String ftpIp = middlewarePo.getIp();
                    //int ftpPort = 21;//只能使用21端口
                    int ftpPort = Integer.parseInt(middlewarePo.getPort());
                    String ftpUserName = middlewarePo.getUser();
                    String ftpPassWord = middlewarePo.getPassword();
                    if (ftpPassWord == null) {
                        throw new ServiceException("服务器密码不能为空");
                    }
                    //ftb保存到本机中转的路径
                    //String localPath = "E:";//最后设置为 C盘
                    //String sourceFile = recordPo.getBackupTarget();

                    //分开多次执行（如果不止一条）
                    //String[] split1 = sourceFile.split(",");
                    //timeNameId 时间戳表格主键
                    //Long timeStampId = IdWorker.getNextId();
                    //String timeStamp1 = timeStamp+"("+add+")";
                    add++;

                    sourceFile = split1[i];
                    if (sourceFile.equals("/")) {
                        throw new ServiceException("服务器不可备份根目录");
                    }
                    //确保backupTarget前面有 “/”
                    if (sourceFile.charAt(0) != '/') {
                        sourceFile = "/" + sourceFile;
                    }
                    // zipFilePath = localPath+sourceFile.substring(0,sourceFile.lastIndexOf("/"))+sourceFile.substring(sourceFile.lastIndexOf("/"))+".zip";
                    String sourceFilePrefix = sourceFile.substring(0,sourceFile.lastIndexOf("/"));
                    String sourceFileSuffix = sourceFile.substring(sourceFile.lastIndexOf("/")+1);
                    boolean contains = sourceFileSuffix.contains(".");

                    //没有点的有可能是文件
                    //连接ftp服务器
                    FtpUtil2.loginFtp3(ftpIp,ftpPort,ftpUserName,ftpPassWord);
                    //连接ftp服务器
                    FtpUtil2.connectServer(ftpIp,ftpPort,ftpUserName,ftpPassWord,sourceFile);
                    String fileOrDirectory = FtpUtil2.isFileOrDirectory(sourceFile);

                    //判断是文件还是文件夹
                    byte[] bytes = null;
                    if (fileOrDirectory.equals("1")) {//contains为true，是文件
                        //连接ftp服务器layer length loan lounge machine majority manufacture marsh maturity media medication medicine mineral minute
                        FtpUtil2.connectServer(ftpIp,ftpPort,ftpUserName,ftpPassWord,sourceFilePrefix);
                        //获取文件的流
                        bytes = FtpUtil2.downloadMinio(sourceFileSuffix);
                        if (bytes == null) {
                            throw new ServiceException(sourceFile+":该文件不存在或者为无法下载的文件");
                        }
                        bytes = getZipByte(bytes,sourceFileSuffix);

                    }else if (fileOrDirectory.equals("2")){//contains为false，是文件夹
                        //连接ftp服务器
                        //FtpUtil2.loginFtp3(ftpIp,ftpPort,ftpUserName,ftpPassWord);
                        //连接ftp服务器
                        //FtpUtil2.connectServer(ftpIp,ftpPort,ftpUserName,ftpPassWord,sourceFile);
                        boolean b = FtpUtil2.dirIsExist(sourceFile);
                        if (!b) {
                            throw new ServiceException(sourceFile+":该文件夹不存在");
                        }
                        bytes = FtpUtil2.downloadMinioMoreFile(sourceFile);
                    }

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("50");
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    //将文件上传至minio
                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    minioClientUtils.uploadFile(minioConfig.getBucketName(), timeStamp+".zip", inputStream);

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("80");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    /*try {
                        minioClientUtils.uploadFile(minioConfig.getBucketName(), timeStamp1+".zip", inputStream);
                        //每上传一次minio，在时间戳表生成对应文件名记录
                        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
                        timestampPo.setId(timeStampId);
                        timestampPo.setTimeStamp(timeStamp1);
                        timestampPo.setFileName(sourceFileSuffix.contains(".")?sourceFileSuffix.substring(0,sourceFileSuffix.lastIndexOf("."))+".zip":sourceFileSuffix+".zip");
                        timestampMapper.insert(timestampPo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    //cifs
                }else if (recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("cifs")))) {
                    String url = recordPo.getUrl();
                    String ip = middlewarePo.getIp();
                    int port = Integer.parseInt(middlewarePo.getPort());
                    String userName = middlewarePo.getUser();
                    String passWord = middlewarePo.getPassword();
                    if (passWord == null) {
                        throw new ServiceException("服务器密码不能为空");
                    }
                    sourceFile = split1[i];
                    if (sourceFile.equals("/")) {
                        throw new ServiceException("服务器不可备份根目录");
                    }
                    if (sourceFile.charAt(0) != '/') {
                        sourceFile = "/" + sourceFile;
                    }
                    //"smb://Lenovo:a0000@172.16.39.138/photo/cifs/1.jpg"
                    if (passWord != null && !passWord.equals("")) {
                        sourceFile = "smb://" + userName + ":" + passWord + "@" + ip + sourceFile;
                    }else {
                        sourceFile = "smb://" + ip + sourceFile;
                    }
                    String sourceFile2 = sourceFile;
                    if (sourceFile.substring(sourceFile.length() - 1).equals("/")) {
                        sourceFile2 = sourceFile.substring(0,sourceFile.length() - 1);
                    }
                    String sourceFilePrefix = sourceFile2.substring(0,sourceFile2.lastIndexOf("/"));
                    String sourceFileSuffix = sourceFile2.substring(sourceFile2.lastIndexOf("/")+1);
                    String zipSourceFileSuffix = sourceFileSuffix+".zip";

                    //执行一次写入备份历史表
                    //判断是否执行了停止备份，如果执行了则不继续更新
                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("正在备份")));
                        historyRecordPo.setOperationTime(date);
                        historyRecordPo.setProportion("20");
                        historyRecordPo.setTimeStamp(timeStamp+".zip");
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    boolean file = true;
                    SmbFile remoteFile = new SmbFile(sourceFile);
                    file = remoteFile.isFile();

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("40");
                        historyRecordMapper.updateById(historyRecordPo);
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

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("60");
                        historyRecordMapper.updateById(historyRecordPo);
                    }

                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp+".zip",inputStream);

                    historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                    if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                        historyRecordPo.setProportion("80");
                        historyRecordMapper.updateById(historyRecordPo);
                    }
                    /*try {
                        minioClientUtils.uploadFile(minioConfig.getBucketName(),timeStamp1+".zip",inputStream);
                        //每上传一次minio，在时间戳表生成对应文件名记录
                        LyDbBackupTimestampPo timestampPo = new LyDbBackupTimestampPo();
                        timestampPo.setId(timeStampId);
                        timestampPo.setTimeStamp(timeStamp1);
                        timestampPo.setFileName(zipSourceFileSuffix);
                        timestampMapper.insert(timestampPo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                }

                //清除redis失败缓存
//                if (!recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("http")))) {
//                    redisConf.del(redisKey+sshId);
//                    long incr = redisConf.incr(redisKey + sshId, 1);
//                    redisConf.expireByDays(redisKey+sshId, Long.parseLong(backupManagementPo.getExpirationTime()));
//                }

                //执行一次写入备份历史表
                //LyDbBackupHistoryRecordPo historyRecordPo = new LyDbBackupHistoryRecordPo();
            /*historyRecordPo.setTitle(recordPo.getTitle());
            historyRecordPo.setBackupStrategyType(TaskModeEnums.getValue(Integer.valueOf(recordPo.getTaskMode())));
            historyRecordPo.setOperationTime(new Date());
            historyRecordPo.setBackupStatus(BackupStatusEnums.getValue(0));
            *//*如果被备份成功，在策略表里更新操作时间*//*
            recordPo.setOperationTime(new Date());
            lyDbBackupStrategyRecordMapper.updateById(recordPo);

            historyRecordPo.setBackupStatus(BackupStatusEnums.getValue(1));
            int insert = historyRecordMapper.insert(historyRecordPo);*/

                historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
                if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("5")) {
                    historyRecordPo.setBackupStatus(String.valueOf(BackupStatusEnums.getCode("备份成功")));
                    historyRecordPo.setProportion("100");
                    historyRecordPo.setTimeStamp(timeStamp+".zip");
                    historyRecordMapper.updateById(historyRecordPo);
                }
            }

        }catch (Exception e){
            if (!recordPo.getBackupMethod().equals(String.valueOf(BackupMethodEnums.getCode("http")))) {
                //如果是ssh，redis计算失败次数，超出失败次数则要求去资源管理修改账号密码
//                long incr = redisConf.incr(redisKey+sshId, 1);
//                logger.error("SSH备份出现异常，redis已记录:"+redisKey+sshId+":"+incr);
            }
            logger.error("备份出错");
            logger.error(e.getMessage());
            Integer code = BackupStatusEnums.getCode("备份失败");
            logger.info("code:"+code);
            historyRecordPo = historyRecordMapper.selectById(historyRecordPo);
            if (!historyRecordPo.getBackupStatus().equals(String.valueOf(BackupStatusEnums.getCode("已停止备份")))) {
                historyRecordPo.setBackupStatus(UsualUtil.getString(code));
                historyRecordPo.setJournal(e.getMessage());
                historyRecordPo.setProportion("100");
                historyRecordMapper.updateById(historyRecordPo);
            }
        }

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


    // --- 辅助方法 ---

    /**
     * 建立 JSch Session 连接
     */
    private Session connectSession(String user,String password,String ip,int port) throws JSchException {
        JSch jsch = new JSch();
        Session session;

        try {
            session = jsch.getSession(user, ip, port);
            // 如果没有使用密钥，则使用密码认证
            session.setPassword(password);
            // 重要：禁用严格的主机密钥检查（生产环境应考虑导入 known_hosts）
            session.setConfig("StrictHostKeyChecking", "no");
            // 可以设置连接超时
            // session.setTimeout(60000); // 60秒
            session.connect();
            return session;
        } catch (JSchException e) {
            throw e;
        }
    }

    /**
     * 断开 SFTP Channel 和 Session
     */
    private void disconnectSftp(ChannelSftp channelSftp, Session session) {
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * 递归地将远程目录添加到 ZipOutputStream
     * @param channelSftp SFTP channel
     * @param remoteDirPath 远程目录的绝对路径
     * @param zos ZipOutputStream
     * @param basePathInZip Zip 文件内的相对路径前缀
     */
    private void addDirectoryToZip(ChannelSftp channelSftp, String remoteDirPath, ZipOutputStream zos, String basePathInZip) throws SftpException, IOException {
        // 确保基础路径以 '/' 结尾，方便拼接
        if (!basePathInZip.isEmpty() && !basePathInZip.endsWith("/")) {
            basePathInZip += "/";
        }

        @SuppressWarnings("unchecked") // JSch ls 返回原始 Vector
        Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(remoteDirPath);

        if (entries.isEmpty() && !basePathInZip.isEmpty()) {
            // 处理空目录，创建一个条目
            ZipEntry zipEntry = new ZipEntry(basePathInZip); // basePathInZip 就是目录名
            zos.putNextEntry(zipEntry);
            zos.closeEntry();
            return; // 空目录处理完毕
        }


        for (ChannelSftp.LsEntry entry : entries) {
            String entryName = entry.getFilename();
            // 跳过 "." 和 ".." 目录
            if (entryName.equals(".") || entryName.equals("..")) {
                continue;
            }

            String fullRemotePath = combineUnixPaths(remoteDirPath, entryName);
            String entryPathInZip = basePathInZip + entryName;

            if (entry.getAttrs().isDir()) {
                // 如果是目录，递归调用
                // 为目录本身创建一个条目（以 / 结尾）
                ZipEntry dirEntry = new ZipEntry(entryPathInZip + "/");
                zos.putNextEntry(dirEntry);
                zos.closeEntry();
                addDirectoryToZip(channelSftp, fullRemotePath, zos, entryPathInZip);
            } else {
                // 如果是文件，添加到 Zip
                ZipEntry zipEntry = new ZipEntry(entryPathInZip);
                // 可选：设置文件时间等属性
                // zipEntry.setTime(entry.getAttrs().getMTime() * 1000L);
                zos.putNextEntry(zipEntry);
                try (InputStream fis = channelSftp.get(fullRemotePath)) {
                    StreamUtils.copy(fis, zos); // 从 SFTP 流复制到 Zip 流
                }
                zos.closeEntry(); // 完成当前文件的写入
            }
        }
    }

    /**
     * 确保远程目录存在，如果不存在则尝试创建 (类似 mkdir -p)
     */
    private void ensureRemoteDirectoryExists(ChannelSftp channel, String remoteDirPath) throws SftpException {
        String[] folders = remoteDirPath.split("/");
        String currentPath = "";
        if (remoteDirPath.startsWith("/")) {
            currentPath = "/"; // 处理根目录开头的路径
        }

        for (String folder : folders) {
            if (folder.isEmpty()) continue;
            currentPath = combineUnixPaths(currentPath, folder);
            try {
                SftpATTRS attrs = channel.lstat(currentPath);
                if (!attrs.isDir()) {
                    throw new RuntimeException();
                }
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                    // 目录不存在，创建它
                    channel.mkdir(currentPath);
                } else {
                    // 其他错误
                    throw e;
                }
            }
        }
    }

    /**
     * 安全地组合两个 Unix 路径部分
     */
    private String combineUnixPaths(String part1, String part2) {
        String path1 = part1.endsWith("/") ? part1.substring(0, part1.length() - 1) : part1;
        String path2 = part2.startsWith("/") ? part2.substring(1) : part2;
        if (path1.isEmpty()) {
            return path2.isEmpty() ? "/" : "/" + path2; // 处理根目录或只有第二部分的情况
        }
        if (path2.isEmpty()) {
            return path1.isEmpty() ? "/" : path1; // 处理只有第一部分的情况
        }

        // 确保最终结果以 / 开头（如果是根目录）或不以 / 开头（如果part1不是根）
        String combined = path1 + "/" + path2;
        if (!part1.equals("/") && combined.startsWith("//")) {
            return combined.substring(1); // 防止双斜杠，除非是根目录
        }
        return combined;

    }

    private String RandomSizeGenerator() {
        int randomInt = ThreadLocalRandom.current().nextInt(10, 31);
        // 转换为1位小数的浮点值
        double size = randomInt / 10.0;
        // 格式化为字符串
        String s = size+"MB";
        return s;
    }



}

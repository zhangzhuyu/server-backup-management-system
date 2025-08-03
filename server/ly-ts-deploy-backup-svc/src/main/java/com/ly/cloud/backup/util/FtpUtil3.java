package com.ly.cloud.backup.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


/**
 * @Author: zhangzhuyu
 * @Description:
 */
public class FtpUtil3 {

    private static final Logger logger = LoggerFactory.getLogger(FtpUtil3.class);
    private static FTPClient ftpClient;

    /*登录ftp服务器*/
    public static void login(String ftpIp,Integer ftpPort,String ftpUserName,String ftpPassWord) throws IOException{
        ftpClient = new FTPClient();
        ftpClient.connect(ftpIp,ftpPort);
        ftpClient.login(ftpUserName, ftpPassWord);
        //设置编码
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.setControlEncoding("GBK");
        ftpClient.enterLocalPassiveMode();
        ftpClient.setBufferSize(8096);
    }


    /**
     * 下载任务，递归调用，循环下载所有目录下的文件
     * @param sourceFile
     * @throws IOException
     */
    public static void doDownload(String localPath,String sourceFile) throws IOException{
        //创建本地目录
        makeDirs(localPath,sourceFile);
        //切换工作目录
        ftpClient.changeWorkingDirectory(new String(sourceFile.getBytes(), StandardCharsets.ISO_8859_1));
        //获取目录下的文件列表
        String[] fileNames = ftpClient.listNames();
        //循环下载FTP目录下的文件
        for(int i = 0;i<fileNames.length;i++){
            if(isDirectory(sourceFile+"/"+fileNames[i])){
                //递归调用
                doDownload(localPath,sourceFile+"/"+fileNames[i]);
            }else{
                //下载单个文件
                downloadFile(localPath,sourceFile+"/"+fileNames[i]);
            }
        }
    }

    /*下载单个文件*/
    public static void downloadFile(String localPath,String sourceFile) throws IOException{
        File file = new File(localPath + sourceFile);
        OutputStream os = new FileOutputStream(file);
        //如果文件名中含有中文，retrieveFile文件时会找不到FTP上的文件，导致保存在本地的是一个空文件，所以也要转换一下
        ftpClient.retrieveFile(new String(file.getName().getBytes("GBK"), StandardCharsets.ISO_8859_1), os);
        os.close();
    }


    /*判断给定的路径是文件还是文件夹*/
    public static boolean isDirectory(String sourceFile) throws IOException{
        //如果是文件，就会返回false
        //如果文件夹或文件名中含有中文，这里要转换一下，不然会返回false
        return ftpClient.changeWorkingDirectory(new String(sourceFile.getBytes("GBK"), StandardCharsets.ISO_8859_1));
    }

    /*判断本地路径是否存在，不存在就创建路径*/
    public static void makeDirs(String localPath,String sourceFile){
        File localFile = new File(localPath+sourceFile);
        if(!localFile.exists()){
            localFile.mkdirs();
        }
    }



}





































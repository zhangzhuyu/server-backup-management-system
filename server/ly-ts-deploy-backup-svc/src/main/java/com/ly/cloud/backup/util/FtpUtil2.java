package com.ly.cloud.backup.util;

import com.google.common.primitives.Bytes;
import com.ly.cloud.backup.exception.ServiceException;
import com.ly.cloud.backup.vo.SelectVo;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpProtocolException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangzhuyu
 * @Date: 2023/4/4 9:04
 * @Description
 */
public class FtpUtil2 {
    private static FtpClient ftpClient;

    /*FtpUtil(){
         *//*
        使用默认的端口号、用户名、密码以及根目录连接FTP服务器
         *//*
//        FTP服务器的 ftp://192.168.35.171:2221/  用户名lysk密码ly37621040
        this.connectServer("192.168.35.171", 2221, "lysk", "ly37621040", "/test/");
    }*/

    public static void connectServer(String ip, int port, String user, String password, String path) {
        try {
            /* ******连接服务器的两种方法*******/
            ftpClient = FtpClient.create();
            try {
                SocketAddress addr = new InetSocketAddress(ip, port);
                ftpClient.connect(addr);
                ftpClient.login(user, password.toCharArray());


                System.out.println("login success!");
                if (path.length() != 0) {
                    //把远程系统上的目录切换到参数path所指定的目录
                    ftpClient.changeDirectory(path);
                }
            } catch (FtpProtocolException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /**
     * 关闭连接
     */
    public static void closeConnect() {
        try {
            ftpClient.close();
            System.out.println("disconnect success");
        } catch (IOException ex) {
            System.out.println("not disconnect");
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /**
     * 上传文件
     * @param localFile 本地文件
     * @param remoteFile 远程文件
     */
    public static void upload(String localFile, String remoteFile) {
        File file_in = new File(localFile);
        try(OutputStream os = ftpClient.putFileStream(remoteFile);
            FileInputStream is = new FileInputStream(file_in)) {
            byte[] bytes = new byte[1024];
            int c;
            while ((c = is.read(bytes)) != -1) {
                os.write(bytes, 0, c);
            }
            System.out.println("upload success");
        } catch (IOException ex) {
            System.out.println("not upload");
            ex.printStackTrace();
        } catch (FtpProtocolException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件。获取远程机器上的文件filename，借助TelnetInputStream把该文件传送到本地。
     * @param remoteFile 远程文件路径(服务器端)
     * @param localFile 本地文件路径(客户端)
     */
    public static void download(String remoteFile, String localFile) {
        File file_in = new File(localFile);
        try{

            InputStream is = ftpClient.getFileStream(remoteFile);
            FileOutputStream os = new FileOutputStream(file_in);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = is.read(bytes)) != -1) {
                os.write(bytes, 0, c);
            }
            System.out.println("download success");
        } catch (IOException ex) {
            System.out.println("not download");
            ex.printStackTrace();
        }catch (FtpProtocolException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件到minio。获取远程机器上的文件filename，借助TelnetInputStream把该文件传送到minio。
     * 返回一个流
     * @param remoteFile 远程文件路径(服务器端)
     */
    public static byte[] downloadMinio(String remoteFile) {
        try {
            InputStream inputStream = ftpClient.getFileStream(remoteFile);

            //流先转化为byte数组存储
            try (
                    BufferedInputStream bf = new BufferedInputStream(inputStream);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ) {
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = bf.read(buffer, 0, 1024))) {
                    bos.write(buffer, 0, len);
                }

                return bos.toByteArray();

            } catch (Exception e) {
                throw new RuntimeException("文件下载失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage()+"  该文件不存在或者为无法下载的文件");
        }finally {
            closeConnect();
        }

    }


    /**
     * 下载文件到minio。获取远程机器上的文件filename，借助TelnetInputStream把该文件传送到minio。（支持文件夹传输）
     * 返回一个流
     * @param sourceFile 远程文件路径(服务器端)
     */
    public static byte[] downloadMinioMoreFile(String sourceFile) {

        //检测文件或文件夹存不存在
        //boolean b = FtpUtil2.dirIsExist(sourceFile);
        /*if (!b) {
            throw new ServiceException(sourceFile+":文件或文件夹不存在!");
        }*/

        String sourceFilePrefix = sourceFile.substring(0,sourceFile.lastIndexOf("/"));
        String sourceFileSuffix = sourceFile.substring(sourceFile.lastIndexOf("/")+1);

        try {
            //递归获取目录里所有文件名字  prefix一直为 ""
            List<String> catalogueList = new ArrayList<>();
            //获取文件名字集合
            List<String> catalogue = getCatalogue(catalogueList, sourceFile,"");

            Map<String,byte[]> bytesMap = new HashMap<>();
            for (int i = 0; i < catalogue.size(); i++) {
                //ftp文件不可以是中文，否则会直接报错
                //为不可下载的文件时，则捕获
                InputStream inputStream = null;
                try{
                    inputStream = ftpClient.getFileStream(catalogue.get(i));
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    continue;
                }
                //流先转化为byte数组存储
                BufferedInputStream bf = new BufferedInputStream(inputStream);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = bf.read(buffer, 0, 1024))) {
                    bos.write(buffer, 0, len);
                }
                //循环放入集合
                bytesMap.put(catalogue.get(i), bos.toByteArray());
            }

            //压缩多个字节数组成一个压缩数组
            byte[] zipBytes = ListBytesToZipInWinUtil.listBytesToZip(bytesMap);
            return zipBytes;

        } catch (Exception e) {
            System.out.println("e"+e);
            throw new ServiceException(e.getMessage());
        }finally {
            closeConnect();
            try {
                ftpClient3.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //递归，目录里面还有目录可以递归查询
    private static List<String> getCatalogue(List<String> catalogueList,String sourceFile,String prefix) throws IOException {
        ftpClient3.changeWorkingDirectory(new String(sourceFile.getBytes(), StandardCharsets.ISO_8859_1));
        //获取目录里文件名字，判断列表里是文件还是文件夹 （map里为true是文件，为false是文件夹）
        FTPFile[] ftpFiles = ftpClient3.listFiles();
        for (int i = 0; i < ftpFiles.length; i++) {
            String name = ftpFiles[i].getName();
            boolean file = ftpFiles[i].isFile();
            //boolean directory = ftpFiles[i].isDirectory();
            if (file) {
                catalogueList.add(prefix+name);
            }/*else if (directory){
                //必须要一个新的变量，否则会影响上面add的路径
                String prefix2 = prefix+name+"/";
                sourceFile = sourceFile+"/"+name;
                getCatalogue(catalogueList,sourceFile,prefix2);
            }*/
        }

        return catalogueList;
    }

    //判断文件和文件夹存不存在
    public static boolean dirIsExist(String path) {
        try {
            Iterator<FtpDirEntry> files = ftpClient.listFiles(path);
            if (files.hasNext()) {
                System.out.println("文件存在");
                return true;
            } else {
                System.out.println("文件不存在");
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //返回1为文件，2为文件夹
    public static String isFileOrDirectory(String sourceFile) throws IOException {
        ftpClient3.changeWorkingDirectory(new String(sourceFile.getBytes(), StandardCharsets.ISO_8859_1));
        //获取目录里文件名字，判断列表里是文件还是文件夹 （map里为true是文件，为false是文件夹）
        FTPFile[] ftpFiles = ftpClient3.listFiles();
        int i = ftpFiles.length;
        String name = ftpFiles[0].getName();
        boolean file = ftpFiles[0].isFile();
        boolean directory = ftpFiles[0].isDirectory();
        if (file && i==1) {
            return "1";
        } else {
            return "2";
        }
    }







    /*--------------------------------------------------------------------------------------------*/
    /*这些是属于FtpUtil3工具类的登录方式*/
    private static FTPClient ftpClient3;

    /*登录ftp服务器*/
    public static void loginFtp3(String ftpIp,Integer ftpPort,String ftpUserName,String ftpPassWord) throws IOException{
        ftpClient3 = new FTPClient();
        ftpClient3.connect(ftpIp,ftpPort);
        ftpClient3.login(ftpUserName, ftpPassWord);
        //设置编码
        ftpClient3.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient3.setControlEncoding("GBK");
        ftpClient3.enterLocalPassiveMode();
        ftpClient3.setBufferSize(8096);




    }


}
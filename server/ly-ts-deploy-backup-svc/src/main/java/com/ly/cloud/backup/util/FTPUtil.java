package com.ly.cloud.backup.util;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author SYC
 * @Date: 2023/4/4 9:04
 * @Description
 */
public class FTPUtil {
    private FtpClient ftpClient;

    FTPUtil(){
         /*
        使用默认的端口号、用户名、密码以及根目录连接FTP服务器
         */
//        FTP服务器的 ftp://192.168.35.171:2221/  用户名lysk密码ly37621040
        this.connectServer("192.168.35.171", 2221, "lysk", "ly37621040", "/test/");
    }

    public void connectServer(String ip, int port, String user, String password, String path) {
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
    public void closeConnect() {
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
    public void upload(String localFile, String remoteFile) {
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
    public void download(String remoteFile, String localFile) {
        File file_in = new File(localFile);
        try(InputStream is = ftpClient.getFileStream(remoteFile);
            FileOutputStream os = new FileOutputStream(file_in)){

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

    public static void main(String agrs[]) {
        FTPUtil fu = new FTPUtil();

        //下载测试
//        String filepath[] = {"aa.xlsx","bb.xlsx"};
//        String localfilepath[] = {"E:/lalala/aa.xlsx","E:/lalala/bb.xlsx"};
//        for (int i = 0; i < filepath.length; i++) {
//            fu.download(filepath[i], localfilepath[i]);
//        }

        fu.download("metricbeat.yml", "C:\\Users\\SYC\\Desktop\\fsdownload/metricbeat.yml");

        //上传测试
//     * @param localFile 本地文件
//     * @param remoteFile 远程文件
        String localfile = "C:\\Users\\SYC\\Desktop\\fsdownload/metricbeat.yml";
        String remotefile = "metricbeat.yml";                //上传
        fu.upload(localfile, remotefile);
        fu.closeConnect();

    }


}
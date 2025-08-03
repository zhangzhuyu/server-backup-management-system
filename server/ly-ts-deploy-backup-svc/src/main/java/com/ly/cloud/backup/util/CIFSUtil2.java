package com.ly.cloud.backup.util;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.io.*;

public class CIFSUtil2 {

    public static void main(String[] args) {
        getRemoteFile();
    }

    public static void getRemoteFile() {
        InputStream in = null;
        try {
            // 创建远程文件对象
            // smb://ip地址/共享的路径/...
            // smb://用户名:密码@ip地址/共享的路径/...
            String remoteUrl = "smb://Lenovo:a0000@172.16.39.138/photo/cifs/";
//            String remoteUrl = "smb://July-1:a0000@172.16.24.174/test/";
            SmbFile remoteFile = new SmbFile(remoteUrl);
            remoteFile.connect();//尝试连接
            if (remoteFile.exists()) {
                // 获取共享文件夹中文件列表
                SmbFile[] smbFiles = remoteFile.listFiles();
                for (SmbFile smbFile : smbFiles) {
                    createFile(smbFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void createFile(SmbFile remoteFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File localFile = new File("f:/file/" + remoteFile.getName());
            in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] buffer = new byte[4096];
            //读取长度
            int len = 0;
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}








































package com.ly.cloud.backup.util;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CIFSUtil {

    public static void main(String[] args) {
        // 创建远程文件对象
        // smb://ip地址/共享的路径/...
        // smb://用户名:密码@ip地址/共享的路径/...
        String remoteUrl = "smb://Lenovo:a0000@172.16.39.138/photo";
        //文件名
        String fileName = "CIFSTest.png";

        getRemoteFileToLocal(remoteUrl);
    }

    //将共享文件下载到本地目录
    public static void getRemoteFileToLocal(String remoteUrl) {
        InputStream in = null;
        try {
            // 创建远程文件对象
            // smb://ip地址/共享的路径/...
            // smb://用户名:密码@ip地址/共享的路径/...
            //String remoteUrl = "smb://Lenovo:a0000@192.168.49.91/photo/";
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




    public static byte[] getRemoteFile(String sourceFile) {
        try {
            // 创建远程文件对象
            // smb://ip地址/共享的路径/...
            // smb://用户名:密码@ip地址/共享的路径/...
            //String remoteUrl = "smb://Lenovo:a0000@192.168.49.91/photo/";
            SmbFile remoteFile = new SmbFile(sourceFile);
            remoteFile.connect();//尝试连接
            if (remoteFile.exists()) {
                InputStream in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
                //return in;

                //流先转化为byte数组存储
                try (
                        BufferedInputStream bf = new BufferedInputStream(in);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //获取cifs共享文件的多个文件（文件夹）
    public static byte[] getRemoteMoreFile(String sourceFile) {
        try {
            // 创建远程文件对象
            // smb://ip地址/共享的路径/...
            // smb://用户名:密码@ip地址/共享的路径/...
            //String remoteUrl = "smb://Lenovo:a0000@192.168.49.91/photo/";
            SmbFile remoteFile = new SmbFile(sourceFile);
            remoteFile.connect();//尝试连接
            if (remoteFile.exists() && remoteFile.isDirectory()) {

                SmbFile[] smbFiles = remoteFile.listFiles();
                Map<String,byte[]> bytesMap = new HashMap<>();
                //生成字节码的map集合
                Map<String, byte[]> catalogue = getCatalogue(bytesMap,smbFiles,sourceFile);
                //全部压缩成字节数组
                byte[] zipBytes = ListBytesToZipInWinUtil.listBytesToZip(bytesMap);

                return zipBytes;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //递归，目录里面还有目录可以递归查询
    private static Map<String,byte[]> getCatalogue(Map<String,byte[]> bytesMap, SmbFile[] smbFiles,String sourceFile) throws Exception{
        //遍历smbFiles元素
        for (int i = 0; i < smbFiles.length; i++) {
            if (smbFiles[i].isFile()) {
                //获取文件名
                String smbFilesPath = smbFiles[i].getPath();
                String title = smbFilesPath.replaceFirst(sourceFile, "");
                //获取流
                InputStream inputStream = smbFiles[i].getInputStream();
                //流先转化为byte数组存储
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = inputStream.read(buffer, 0, 1024))) {
                    bos.write(buffer, 0, len);
                }
                byte[] bytes = bos.toByteArray();
                //put进入集合
                bytesMap.put(title,bytes);

            }else if (smbFiles[i].isDirectory()){
                SmbFile[] smbFiles1 = smbFiles[i].listFiles();
                getCatalogue(bytesMap,smbFiles1,sourceFile);
            }
        }

        return bytesMap;
    }

}


































package com.ly.cloud.backup.util;

import ch.ethz.ssh2.*;
import com.ly.cloud.backup.exception.ServiceException;
import com.ly.cloud.backup.service.impl.DatabaseServiceImpl;
import com.mysql.cj.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class SSHTransferUtil {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    public static void main(String[] args) {
        String ip = "192.168.35.213";
        int port = 22;
        String username = "root";
        String password = "Ly37621040";
        String sourceFile = "/root/test.png";
        String targetFile = "F:\\file";
        String targetFileName = "test.png";
        boolean b = copyFile(ip, port, username, password, sourceFile, targetFile, targetFileName);
        System.out.println(b);
    }

    //远程下载服务器文件到本地主机
    public static boolean copyFile(String ip,int port,String userName,String password,String sourceFile,String targetFile,String targetFileName){
        boolean bool = false;
        Connection conn = null;
        Session session = null;
        try {
            if (StringUtils.isNullOrEmpty(ip) || StringUtils.isNullOrEmpty(userName) || StringUtils.isNullOrEmpty(password) ||
                    StringUtils.isNullOrEmpty(sourceFile) || StringUtils.isNullOrEmpty(targetFile)){
                return bool;
            }
            conn = new Connection(ip,port);
            conn.connect();
            boolean isAuth = conn.authenticateWithPassword(userName,password);
            if (!isAuth){
                log.info("算法主机连接失败");
                return bool;
            }
            //执行命令
            session = conn.openSession();

            //执行命令并打印执行结果
            session.execCommand("df -h");
            InputStream staout = new StreamGobbler(session.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(staout));
            String line = null;
            while ((line = br.readLine()) != null){
                System.out.println(line);
            }
            br.close();

            //下载文件到本地
            SCPClient scpClient = conn.createSCPClient();
            SCPInputStream scpis = scpClient.get(sourceFile);

            //判断指定目录是否存在，不存在则先创建目录
            File file = new File(targetFile);
            if (!file.exists())
                file.mkdirs();

            FileOutputStream fos = new FileOutputStream(targetFile + "\\" +targetFileName);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = scpis.read(buffer)) != -1){
                fos.write(buffer,0,len);
            }
            fos.close();
            bool = true;
            //SFTP
            /*SFTPv3Client sftPClient = new SFTPv3Client(conn);
            sftPClient.createFile("");
            sftPClient.close();*/
        }catch (Exception e){
            e.printStackTrace();
            log.info(e.getMessage());
            log.info("保存失败：" + sourceFile);
        }finally {
            if (null != session){
                session.close();
            }
            if (null != conn) {
                conn.close();
            }
        }

        return bool;
    }

    //远程下载服务器文件转化为流
    public static byte[] copyFileToMinio(String ip,int port,String userName,String password,String sourceFile) throws Exception{
        Connection conn = null;
        Session session = null;
        if (StringUtils.isNullOrEmpty(ip) || StringUtils.isNullOrEmpty(userName) || StringUtils.isNullOrEmpty(password) ||
                StringUtils.isNullOrEmpty(sourceFile)){
            return null;
        }
        conn = new Connection(ip,port);
        conn.connect();
        boolean isAuth = conn.authenticateWithPassword(userName,password);
        if (!isAuth){
            log.info("算法主机连接失败");
            return null;
        }
        //执行命令
        session = conn.openSession();

        //执行命令并打印执行结果
        session.execCommand("df -h");
        InputStream staout = new StreamGobbler(session.getStdout());
        BufferedReader br = new BufferedReader(new InputStreamReader(staout));
        String line = null;
        while ((line = br.readLine()) != null){
            System.out.println(line);
        }
        br.close();

        //文件流
        SCPClient scpClient = conn.createSCPClient();

        //分类讨论，有可能是文件夹有可能是文件
        String sourceFileSuffix = sourceFile.substring(sourceFile.lastIndexOf("/")+1);
        String sourceFilePrefix = sourceFile.substring(0,sourceFile.lastIndexOf("/"));
        boolean contains = sourceFileSuffix.contains(".");
        SCPInputStream scpis = null;

        JSchUtil jSchUtil =  new JSchUtil(ip,userName,password,port,10 * 60 * 1000);
        String s = jSchUtil.execCommand("ls -F " + sourceFile+" 2>&1");
        if (s.contains("No such file or directory")) {
            throw new ServiceException("文件或文件夹不存在:"+sourceFile);
        } else if (s.equals(sourceFile + "\n")) {//是文件
            System.out.println("这是个文件");
            scpis = scpClient.get(sourceFile);
        }else {//是文件夹
            System.out.println("这是个文件夹");
            jSchUtil.execCommand("cd "+sourceFilePrefix+"&&"+"zip -r "+sourceFileSuffix+".zip "+sourceFileSuffix);
            //压缩文件夹
            scpis = scpClient.get(sourceFilePrefix+"/"+sourceFileSuffix+".zip");
        }
//        if (contains) {//contains为true，是文件
//            //JSchUtil jSchUtil =  new JSchUtil(ip,userName,password,port,10 * 60 * 1000);
//            //判断Linux中的文件或文件夹有没有超出规定大小
//            /*int i = determineFileSize(jSchUtil, sourceFilePrefix, sourceFileSuffix);
//            //如果i==0，说明超出大小
//            if (i == 0) {
//                return null;
//            }*/
//            scpis = scpClient.get(sourceFile);
//
//        }else {//是文件夹
//            //连接服务器，利用服务器命令查看服务器内容
//            //JSchUtil jSchUtil =  new JSchUtil(ip,userName,password,port,10 * 60 * 1000);
//            //判断Linux中的文件或文件夹有没有超出规定大小
//            /*int i = determineFileSize(jSchUtil, sourceFilePrefix, sourceFileSuffix);
//            if (i == 0) {
//                return null;
//            }*/
//            jSchUtil.execCommand("cd "+sourceFilePrefix+"&&"+"zip -r "+sourceFileSuffix+".zip "+sourceFileSuffix);
//            //压缩文件夹
//            scpis = scpClient.get(sourceFilePrefix+"/"+sourceFileSuffix+".zip");
//
//        }

        //流先转化为byte数组存储
        try (
                BufferedInputStream bf = new BufferedInputStream(scpis);
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


    /**
     * 源流 转换成ZIP流
     *
     * @param sourceData 源流
     * @param name 文件命名
     * @return byte[]
     */
    public static byte[] getZipByte(byte[] sourceData, String name) {
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


    /*判断Linux中的文件或文件夹有没有超出规定大小*/
    private static int determineFileSize(JSchUtil jSchUtil,String sourceFilePrefix,String sourceFileSuffix){
        String log1 = jSchUtil.execCommand("cd "+sourceFilePrefix+"&&du -sh "+sourceFileSuffix+" 2>&1");
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(log1) || log1.contains("du:")) {
            logger.error(sourceFilePrefix+"/"+sourceFileSuffix+":"+"文件或目录不存在！");
            throw new ServiceException(sourceFilePrefix+"/"+sourceFileSuffix+":"+"文件或目录不存在！");
            //return 0;
        }
        String[] split = log1.split("\\s+");
        for (int i = 0; i < split.length; i++) {
            //循环为奇数时跳过本次循环
            if (i % 2 != 0) {
                continue;
            }
            if (split[i].contains("G") || split[i].contains("g")) {
                logger.error("想要下载的文件或文件目录总大小超过500MB，停止下载！");
                throw new ServiceException("想要下载的文件或文件目录总大小超过500MB，停止下载！");
                //return 0;
            } else if (split[i].contains("M") || split[i].contains("m")) {
                double totalNum = Double.parseDouble(split[i].split("M")[0]);
                if (totalNum > 500) {
                    logger.error("想要下载的文件或文件目录总大小超过500MB，停止下载！");
                    throw new ServiceException("想要下载的文件或文件目录总大小超过500MB，停止下载！");
                    //return 0;
                }
            }
        }
        System.out.println("文件或文件夹大小符合要求");
        return 1;

    }
}


































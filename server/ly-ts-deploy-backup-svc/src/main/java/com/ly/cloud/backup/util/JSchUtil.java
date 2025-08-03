package com.ly.cloud.backup.util;

import com.jcraft.jsch.*;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ly.cloud.backup.vo.ServerVo;

/**
 * JSch 工具类
 * JSch API地址 http://epaul.github.io/jsch-documentation/javadoc/com/jcraft/jsch/package-summary.html
 *
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2022-03-07
 * @version: 1.0
 */
public class JSchUtil {

    private static final Logger logger = LoggerFactory.getLogger(JSchUtil.class);

    private String host; // 服务器地址
    private String username; // 登录用户名
    private String password; // 登录密码
    private int port = 22; // 登录端口
    private int timeout = 3 * 60 * 1000; // 连接超时时间，单位毫秒

    private Session session = null; // ssh session
    private ChannelExec channelExec = null; // 用于执行命令,非交互的，一次通道执行一条命令
    private ChannelSftp channelSftp = null; // 用于上传下载文件

    public JSchUtil(String host, String username, String password, int port, int timeout) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.timeout = timeout;
    }

    public JSchUtil(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

//    /**
//     * 注意放弃该方法，直接在执行命令时登录，Exec模式连接时，一次会话只能执行一次命令，然后就会断开连接，得再次登录才能再执行命令
//     * 登录Exec 服务器
//     *
//     * @return
//     */
//    public boolean loginExec() {
//        try {
//            JSch jSch = new JSch();
//            session = jSch.getSession(username, host, port);
//            if (password != null) {
//                session.setPassword(password);
//            }
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking", "no");
//            session.setConfig(config);
//            session.setTimeout(timeout);
//            session.connect();
//
//            channelExec = (ChannelExec) session.openChannel("exec");
////            channelExec.connect();
//            return true;
//        } catch (Exception e) {
//            logger.error("sftp 服务器登录失败！ " + e);
//            return false;
//        }
//    }

    /**
     * 登录sftp 服务器
     *
     * @return
     */
    public boolean loginSftpThrow() throws JSchException {
            JSch jSch = new JSch();
            session = jSch.getSession(username, host, port);
            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(timeout);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            return true;
    }

    /**
     * 登录sftp 服务器
     *
     * @return
     */
    public boolean loginSftp() {
        try {
            JSch jSch = new JSch();
            session = jSch.getSession(username, host, port);
            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(timeout);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            System.out.println(host+":登录成功");
            return true;
        } catch (Exception e) {
            logger.error(host+":sftp 服务器登录失败！ " + e);
            throw new RuntimeException(host+":sftp 服务器登录失败！");
            //throw new RuntimeException(e);
        }
    }


    /**
     * 退出Exec 服务器
     */
    public void logoutExec() {
        if (channelExec != null) {
            channelExec.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * 退出Sftp 服务器
     */
    public void logoutSftp() {
        if (channelSftp != null) {
            channelSftp.quit();
            channelSftp.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * 通过 channelExec 执行shell命令
     * 注意：多个命令用 && 或者 || 连接，&&代表 上一条命令成功才继续执行，||代表 上一条命令失败才继续执行
     * 例如: cd /etc && ll || ls -l
     *
     * @param cmd 命令行
     * @return
     */
    public String execCommand(String cmd) {
        JSch jSch = new JSch();
        Session session = null;
        ChannelExec channelExec = null;
        BufferedReader inputStreamReader = null;
        BufferedReader errInputStreamReader = null;
        StringBuilder runLog = new StringBuilder("");
        StringBuilder errLog = new StringBuilder("");
        try {
            // 1. 获取 ssh session
            session = jSch.getSession(username, host, port);
            session.setPassword(password);
            session.setTimeout(timeout);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();  // 获取到 ssh session
            // 2. 通过 exec 方式执行 shell 命令
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(cmd);
            channelExec.connect();  // 执行命令
            logger.info(host+":登录成功！");
            // 3. 获取标准输入流
            inputStreamReader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            // 4. 获取标准错误输入流
            errInputStreamReader = new BufferedReader(new InputStreamReader(channelExec.getErrStream()));
            // 5. 记录命令执行 log
            String line = null;
            while ((line = inputStreamReader.readLine()) != null) {
                runLog.append(line).append("\n");
            }
//            // 7. 输出 shell 命令执行日志
//            System.out.println("exitStatus=" + channelExec.getExitStatus() + ", openChannel.isClosed="
//                    + channelExec.isClosed());
//            System.out.println("命令执行完成，执行日志如下:");
//            System.out.println(runLog.toString());
//            System.out.println("命令执行完成，执行错误日志如下:");
//            System.out.println(errLog.toString());
//            System.out.println("============================输出结束============================");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(host+"：连接服务器失败！");
        } finally {
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (errInputStreamReader != null) {
                    errInputStreamReader.close();
                }
                if (channelExec != null) {
                    channelExec.disconnect();
                }
                if (session != null) {
                    session.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return runLog.toString();
    }


    /**
     * 上传文件
     *
     * @param pathName 服务器目录
     * @param fileName 服务器上保存的文件名
     * @param is       输入文件流
     * @return
     */
    public boolean uploadFile(String pathName, String fileName, InputStream is) {
        if (!changeDir(pathName)) {
            return false;
        }
        try {
            channelSftp.put(is, fileName, ChannelSftp.OVERWRITE);
            if (!existFile(fileName)) {
                return false;
            }
            return true;
        } catch (SftpException e) {
            logger.error("上传文件失败!", e);
            return false;
        } finally {
            //changeDir(currentDir());
        }
    }

    /**
     * 上传文件
     *
     * @param pathName  服务器目录
     * @param fileName  服务器上保存的文件名
     * @param localFile 本地文件
     * @return
     */
    public boolean uploadFile(String pathName, String fileName, String localFile) {
        if (!changeDir(pathName)) {
            logger.error(pathName + "路径改变失败!");
            return false;
        }
        try {
            channelSftp.put(localFile, fileName, ChannelSftp.OVERWRITE);
            if (!existFile(fileName)) {
                logger.error(fileName + "文件不存在");
                return false;
            }
            return true;
        } catch (SftpException e) {
            logger.error("上传文件失败!", e);
            return false;
        } finally {
            changeDir(currentDir());
        }
    }

    /**
     * 上传文件,字节流方式
     *
     * @param pathName    服务器目录
     * @param fileName    服务器上保存的文件名
     * @param inputStream 文件输出流
     * @return
     */
    public boolean uploadFileByte(String pathName, String fileName, InputStream inputStream) {
        if (!changeDir(pathName)) {
            logger.error(pathName + "路径改变失败!");
            return false;
        }
        try {
            channelSftp.put(inputStream, fileName, ChannelSftp.OVERWRITE);
            if (!existFile(fileName)) {
                logger.error(fileName + "文件不存在");
                return false;
            }
            return true;
        } catch (SftpException e) {
            logger.error("上传文件失败!", e);
            return false;
        } finally {
            changeDir(currentDir());
        }
    }

    /**
     * 上传文件
     *
     * @param dest      服务器上保存的路径
     * @param localFile 本地文件
     * @return
     */
    public boolean uploadFile(String dest, String localFile) {
        try {
            channelSftp.put(localFile, dest, ChannelSftp.OVERWRITE);
            return true;
        } catch (Exception e) {
            logger.error("上传文件失败!", e);
            return false;
        } finally {
            changeDir(currentDir());
        }
    }

    public String[] lsGrep(String path) throws Exception {
        Vector<ChannelSftp.LsEntry> list = null;
        try {
            //ls方法会返回两个特殊的目录，当前目录(.)和父目录(..)
            list = channelSftp.ls(path);
        } catch (SftpException e) {
            return new String[0];
        }
        List<String> resultList = new ArrayList<String>();
        for (ChannelSftp.LsEntry entry : list) {
            resultList.add(entry.getFilename());
        }
        return resultList.toArray(new String[0]);
    }

    /**
     * 下载文件
     *
     * @param remotePath 需下载的文件路径
     * @param fileName   文件名称
     * @param localPath  本地保存路径
     * @return
     */
    public boolean downloadFile(String remotePath, String fileName, String localPath) {
        if (!changeDir(remotePath)) {
            return false;
        }
        String localFilePath = localPath + File.separator + fileName;
        try {
            channelSftp.get(fileName, localFilePath);
            File localFile = new File(localFilePath);
            if (!localFile.exists()) {
                return false;
            }
            return true;
        } catch (SftpException e) {
            logger.error("文件下载失败!", e);
            return false;
        } finally {
            changeDir(currentDir());
        }
    }

    /**
     * 下载文件,Byte格式
     *
     * @param remotePath 需下载的文件路径
     * @param fileName   文件名称
     *                   //     * @param localPath  本地保存路径
     * @return
     */
    public byte[] downloadFileByte(String remotePath, String fileName) {

        if (!changeDir(remotePath)) {
            throw new RuntimeException("下载路径不存在");
        }
        try (
                InputStream inputStream = channelSftp.get(fileName);
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
            logger.error("文件下载失败!", e);
            throw new RuntimeException("文件下载失败!");
        } finally {
            changeDir(currentDir());
        }
    }

    /**
     * 获取服务器的文件 内容
     *
     * @param filePath:文件路径
     * @return
     */
    public String getFileContent(String filePath, String fileName) {
        StringBuilder fileContent = new StringBuilder("");
        try {
            String read;
            InputStream inputStream = channelSftp.get(filePath + "/" + fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((read = bf.readLine()) != null) {
                fileContent.append(read).append("\n");
            }
            inputStream.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.out.println("-----------------获取sftp服务器上文件内容出错--------------");
            throw new RuntimeException("获取sftp服务器上文件内容出错");
        }
        return fileContent.toString();
    }

    /**
     * 获取服务器的 xml类型文件
     *
     * @param filePath:文件路径
     * @return
     */
    public String getFkbwFileXml(String filePath) {
        String xmlResultStr = "";
        try {
            String read;
            InputStream inputStream = channelSftp.get(filePath);
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((read = bf.readLine()) != null) {
                xmlResultStr = xmlResultStr + read;
            }
            inputStream.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.out.println("-----------------获取sftp服务器上报文出错--------------");
        }
        xmlResultStr = formatXml(xmlResultStr);
        return xmlResultStr;
    }

    public String formatXml(String unformattedXml) {
        try {
            final Document document = parseXmlFile(unformattedXml);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件夹
     *
     * @param dirName 文件夹名
     * @return
     */
    public boolean delDir(String dirName) {
        if (!changeDir(dirName)) {
            return false;
        }
        Vector<ChannelSftp.LsEntry> lsEntries = null;
        try {
            lsEntries = channelSftp.ls(channelSftp.pwd());
        } catch (SftpException e) {
            return false;
        }
        for (ChannelSftp.LsEntry lsEntry : lsEntries) {
            String fileName = lsEntry.getFilename();
            if (!fileName.equals(".") && !fileName.equals("..")) {
                if (lsEntry.getAttrs().isDir()) {
                    delDir(fileName);
                } else {
                    delFile(fileName);
                }
            }
        }
        if (!changeToParentDir()) {
            return false;
        }
        try {
            channelSftp.rmdir(dirName);
            return true;
        } catch (SftpException e) {
            logger.error("删除文件夹失败!", e);
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public boolean delFile(String fileName) {
        if (fileName == null || fileName.trim().equals("")) {
            return false;
        }
        try {
            channelSftp.rm(fileName);
            return true;
        } catch (SftpException e) {
            logger.error("删除文件失败!", e);
            return false;
        }
    }

    /**
     * 创建目录
     *
     * @param dirName
     * @return
     */
    public boolean makeDir(String dirName) {
        try {
            channelSftp.mkdir(dirName);
            return true;
        } catch (SftpException e) {
            logger.error("创建目录失败!", e);
            return false;
        }
    }

    /**
     * 获取当前工作目录
     *
     * @return
     */
    public String currentDir() {
        try {
            return channelSftp.pwd();
        } catch (SftpException e) {
            logger.error("获取当前工作目录失败!", e);
            return homeDir();
        }
    }

    /**
     * 获取根目录
     *
     * @return
     */
    public String homeDir() {
        try {
            return channelSftp.getHome();
        } catch (SftpException e) {
            return "/";
        }
    }

    /**
     * 切换工作目录
     *
     * @param pathName 路径
     * @return
     */
    public boolean changeDir(String pathName) {
        if (pathName == null || pathName.trim().equals("")) {
            return false;
        }
        try {
            channelSftp.cd(pathName.replaceAll("\\\\", "/"));
            return true;
        } catch (SftpException e) {
            logger.error("改变路径失败!", e);
            return false;
        }
    }

    /**
     * 切换到上一级目录
     *
     * @return
     */
    public boolean changeToParentDir() {
        return changeDir("..");
    }

    /**
     * 切换到根目录
     *
     * @return
     */
    public boolean changetoHomeDir() {
        String homeDir = null;
        try {
            homeDir = channelSftp.getHome();
        } catch (SftpException e) {
            return false;
        }
        return changeDir(homeDir);
    }

    /**
     * 查询当前目录是否存在文件夹或文件
     *
     * @param name
     * @return
     */
    public boolean exist(String name) {
        return exist(ls(), name);
    }

    /**
     * 查询指定目录是否存在文件夹或文件
     *
     * @param name
     * @param path
     * @return
     */
    public boolean exist(String path, String name) {
        return exist(ls(path), name);
    }

    /**
     * 查询当前目录是否存在文件
     *
     * @param name
     * @return
     */
    public boolean existFile(String name) {
        return exist(lsFiles(), name);
    }

    /**
     * 查询指定目录下是否存在文件
     *
     * @param path 目录
     * @param name 文件
     * @return
     */
    public boolean existFile(String path, String name) {
        return exist(lsFiles(path), name);
    }

    /**
     * 查询当前目录是否存在文件夹
     *
     * @param name
     * @return
     */
    public boolean existDir(String name) {
        return exist(lsDirs(), name);
    }

    /**
     * 查询指定目录下是否存在文件
     *
     * @param path 目录
     * @param name 文件
     * @return
     */
    public boolean existDir(String path, String name) {
        return exist(lsDirs(path), name);
    }

    /**
     * 获取当前目录下文件名称列表
     *
     * @return String[]
     */
    public String[] lsFiles() {
        return list(Filter.FILE);
    }

    /**
     * 指定目录下文件名称列表
     *
     * @param pathName
     * @return
     */
    public String[] lsFiles(String pathName) {
        String currentDir = currentDir();
        if (!changeDir(pathName)) {
            return new String[0];
        }
        String[] result = list(Filter.FILE);
        if (!changeDir(currentDir)) {
            return new String[0];
        }
        return result;
    }

    /**
     * 当前目录下文件夹名称列表
     */
    public String[] lsDirs() {
        return list(Filter.DIR);
    }

    /**
     * 指定目录下文件夹名称列表
     */
    public String[] lsDirs(String pathName) {
        if (!changeDir(pathName)) {
            return new String[0];
        }
        String[] result = list(Filter.DIR);
        if (!changeDir(currentDir())) {
            return new String[0];
        }
        return result;
    }

    /**
     * 当前目录下文件夹及文件名称列表
     *
     * @return
     */
    public String[] ls() {
        return list(Filter.ALL);
    }

    /**
     * 指定目录下文件夹及文件名称列表
     *
     * @param pathName
     * @return
     */
    public String[] ls(String pathName) {
        String currentDir = currentDir();
        if (!changeDir(pathName)) {
            return new String[0];
        }
        String[] result = list(Filter.ALL);
        if (!changeDir(currentDir)) {
            return new String[0];
        }
        return result;
    }

    /**
     * 列出当前目录下的文件及文件夹
     *
     * @param filter
     * @return
     */
    @SuppressWarnings("unchecked")
    public String[] list(Filter filter) {
        Vector<ChannelSftp.LsEntry> list = null;
        try {
            //ls方法会返回两个特殊的目录，当前目录(.)和父目录(..)
            list = channelSftp.ls(channelSftp.pwd());
        } catch (SftpException e) {
            return new String[0];
        }
        List<String> resultList = new ArrayList<String>();
        for (ChannelSftp.LsEntry entry : list) {
            if (filter(entry, filter)) {
                resultList.add(entry.getFilename());
            }
        }
        return resultList.toArray(new String[0]);
    }

    /**
     * 判断是否是过滤条件
     *
     * @param filter
     * @return
     */
    private boolean filter(ChannelSftp.LsEntry lsEntry, Filter filter) {
        String fileName = lsEntry.getFilename();
        if (filter.equals(Filter.ALL)) {
            return !fileName.equals(".") && !fileName.equals("..");
        } else if (filter.equals(Filter.FILE)) {
            return !fileName.equals(".") && !fileName.equals("..") && !lsEntry.getAttrs().isDir();
        } else if (filter.equals(Filter.DIR)) {
            return !fileName.equals(".") && !fileName.equals("..") && lsEntry.getAttrs().isDir();
        }
        return false;
    }

    /**
     * 枚举 用于过滤文件及文件夹
     */
    public enum Filter {
        ALL, //文件及文件夹
        FILE, //文件
        DIR //文件夹
    }


    /**
     * 判断字符串是否存在数组中
     *
     * @param strArr
     * @param str
     * @return
     */
    private boolean exist(String[] strArr, String str) {
        if (strArr == null || strArr.length == 0) {
            return false;
        }
        if (str == null || str.trim().equals("")) {
            return true;
        }
        for (String s : strArr) {
            if (s.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean rename(String oldPath, String newPath) {
        try {
            channelSftp.rename(oldPath, newPath);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    /**
     * 获取文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public InputStream getFile(String path) throws Exception {
        return channelSftp.get(path);
    }

    /**
     * 获取文件字节
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public byte[] getBytes(String filePath) throws Exception {
        InputStream is = channelSftp.get(filePath);
        byte[] bizBuffer = new byte[(int) getFileSize(filePath)];
        byte[] bizBuf = new byte[1024];
        int bizN = 0;
        int bizIndex = 0;
        while ((bizN = is.read(bizBuf)) != -1) {
            System.arraycopy(bizBuf, 0, bizBuffer, bizIndex, bizN);
            bizIndex += bizN;
        }
        return bizBuffer;
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     * @throws Exception
     */
    public long getFileSize(String path) throws Exception {
        return channelSftp.stat(path).getSize();
    }

    /**
     * 获取文件属性
     *
     * @param path
     * @return
     * @throws Exception
     */
    public SftpATTRS getFileAttrs(String path) throws Exception {
        return channelSftp.lstat(path);
    }

    /**
     * 设置文件修改时间
     *
     * @param path
     * @throws Exception
     */
    public void setMtime(String path, int mtime) throws Exception {
        channelSftp.setMtime(path, mtime);
    }

    /**
     * 获取主机配置的参数（cpu、ram、系统盘）
     */
    public static List<Map<String, Double>> getServerConf(ServerVo serverVo) {
        List<Map<String, Double>> resultList = new ArrayList<>();
        JSchUtil jSchUtil = null;
        serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
        try {
            jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
            // 获取服务器 cpu使用率，内存使用率，硬盘使用率
            Map<String, Double> utilizationCpuRate = jSchUtil.getRAMUtilizationRate();
            double cpuUtilizationRate = jSchUtil.getCPUUtilizationRate();
            double totalCpu = jSchUtil.getCPUUPhysicalAuditing();
            utilizationCpuRate.put("totalCpu", totalCpu);
            utilizationCpuRate.put("usedSumCpu", cpuUtilizationRate);
            utilizationCpuRate.put("utilizationRateCpu", cpuUtilizationRate);
            Map<String, Double> utilizationRateRAM = jSchUtil.getRAMUtilizationRate();
            Map<String, Double> hardDiskUtilizationRate = jSchUtil.getSysHardDiskUtilizationRate();
            resultList.add(0, utilizationCpuRate);
            resultList.add(1, utilizationRateRAM);
            resultList.add(2, hardDiskUtilizationRate);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(),e);
        } finally {
            // 退出Exec方式登录
            if (jSchUtil != null){
                jSchUtil.logoutExec();
            }
        }
        return resultList;
    }

    /**
     * 获取CPU物理核数总数
     */
    public int getCPUUPhysicalAuditing() {
        try {
            String CPUcmd = "cat /proc/cpuinfo| grep \"physical id\"| sort| uniq| wc -l & cat /proc/cpuinfo| grep \"cpu cores\"| uniq 2>&1";
            String log = this.execCommand(CPUcmd);

            String regEx = "\\d+";
            Matcher m = Pattern.compile(regEx).matcher(log);
            int i = 0;
            int physicalAuditingCPU = 0; // 物理核数总数
            while (m.find()) {
                if (i == 0) {
                    physicalAuditingCPU = Integer.parseInt(m.group());
                } else {
                    physicalAuditingCPU *= Integer.parseInt(m.group());
                }
                i++;
            }
            return physicalAuditingCPU;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            this.logoutExec();
        }
        return -1;
    }

    /**
     * 获取CPU使用率
     */
    public double getCPUUtilizationRate() {
        try {
            String CPUcmd = "top -bn 1 -i -c";
            String[] log = this.execCommand(CPUcmd).split("\\n");

            String regEx = "\\d*\\.\\d*\\s*id";
            String s = log[2];
            Matcher m = Pattern.compile(regEx).matcher(s);
            double utilizationRateCPU = 0; // 使用率
            while (m.find()) {
                BigDecimal b = new BigDecimal(100 - Double.parseDouble(m.group().split("\\s?id")[0]));
                utilizationRateCPU = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            return utilizationRateCPU;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            this.logoutExec();
        }
        return -1;
    }

    /**
     * 获取RAM(内存)使用率
     */
    public Map<String, Double> getRAMUtilizationRate() {
        try {
            String RAMcmd = "free -m";
            String[] log = this.execCommand(RAMcmd).split("\\n");

            String regEx = "\\d+";
            String s = log[1];
            Matcher m = Pattern.compile(regEx).matcher(s);
            double utilizationRateRAM = 0; // 使用率
            List<Double> ss = new ArrayList<>();
            while (m.find()) {
                ss.add(Double.parseDouble(m.group()));
            }

            BigDecimal b = new BigDecimal(((ss.get(0) - ss.get(2) - ss.get(4)) / ss.get(0)) * 100);
            utilizationRateRAM = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            b = new BigDecimal(ss.get(0) / 1024);
            ss.set(0, b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            b = new BigDecimal(ss.get(2) / 1024);
            ss.set(2, b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            b = new BigDecimal(ss.get(4) / 1024);
            ss.set(4, b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

            Map<String, Double> map = new HashMap<>(3);
            map.put("totalRAM", ss.get(0)); // 内存总大小 GB
            map.put("usedSumRAM", (ss.get(0) - ss.get(2) - ss.get(4)));  // 已使用内存 GB
            map.put("utilizationRateRAM", utilizationRateRAM); // 内存使用率
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            this.logoutExec();
        }
        return null;
    }

    /**
     * 获取HardDisk(硬盘)使用率
     */
    public Map<String, Double> getHardDiskUtilizationRate() {
        try {
            String HardDiskCmd = "df";
            String log = this.execCommand(HardDiskCmd).replaceAll("\\n", ",");
            String regEx = "(\\d|\\s)\\d+\\s";
            Matcher m = Pattern.compile(regEx).matcher(log);
            double utilizationRateHardDisk = 0; // 使用率
            double total = 0; // 总数
            double usedSum = 0; // 已使用总和
            int i = 0;
            while (m.find()) {
                if (i % 3 == 0) {
                    total += Double.parseDouble(m.group());
                } else if ((i - 1) % 3 == 0) {
                    usedSum += Double.parseDouble(m.group());
                }
                i++;
            }

            BigDecimal b = new BigDecimal((usedSum / total) * 100);
            utilizationRateHardDisk = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            b = new BigDecimal(usedSum / 1024 / 1024);
            usedSum = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
            b = new BigDecimal(total / 1024 / 1024);
            total = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();

            Map<String, Double> map = new HashMap<>(3);
            map.put("totalHardDisk", total);  // 硬盘总大小 GB
            map.put("usedSumHardDisk", usedSum); // 已使用硬盘大小 GB
            map.put("utilizationRateHardDisk", utilizationRateHardDisk); // 硬盘使用率
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            this.logoutExec();
        }
        return null;
    }

    /**
     * 获取系统盘HardDisk(硬盘)使用率
     */
    public Map<String, Double> getSysHardDiskUtilizationRate() {
        try {
            String HardDiskCmd = "df -h";
            String[] log = this.execCommand(HardDiskCmd).split("\\n");
            String[] sysLog = new String[4]; // 系统盘参数
            for (int i = 0; i < log.length; i++) {
                String[] row = log[i].split("\\s+");
                if (row[5].equals("/")) {
                    sysLog = row;
                    break;
                }
            }
            double utilizationRateHardDisk = Double.parseDouble(sysLog[4].split("%")[0]); // 系统硬盘使用率
            double total = Double.parseDouble(sysLog[1].split("G")[0]); // 系统硬盘总数
            double usedSum = Double.parseDouble(sysLog[2].split("G")[0]); // 已使用系统硬盘总和

            Map<String, Double> map = new HashMap<>(3);
            map.put("totalHardDisk", total);  // 系统硬盘总大小 GB
            map.put("usedSumHardDisk", usedSum); // 已使用系统硬盘大小 GB
            map.put("utilizationRateHardDisk", utilizationRateHardDisk); // 系统硬盘使用率
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            this.logoutExec();
        }
        return null;
    }


    /**
     * 通过 SSH 连接到远程主机，执行 df 命令获取磁盘空间信息。
     * @param host 远程主机 IP
     * @param port SSH 端口 (通常是 22)
     * @param user 用户名
     * @param password 密码
     * @param path 要查询的路径 (例如 "/")
     * @return 包含 "total" 和 "used" 磁盘空间 (单位: KB) 的 Map，如果失败则返回 null
     * @throws JSchException 如果连接或执行失败
     */
    public static Map<String, Long> getRemoteDiskSpace(String host, int port, String user, String password, String path) throws Exception {
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;

        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            // !! 重要：在生产环境中，不建议禁用严格主机密钥检查 !!
            // 为了演示方便，我们禁用它，避免因 host key 变化导致连接失败
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect(30000); // 30秒连接超时

            // 执行命令 df -Pk [path]，例如 "df -Pk /"
            // -P: 使用 POSIX 标准格式输出，确保输出格式稳定，不会因路径名太长而换行
            // -k: 以 1KB 为单位输出，方便计算
            String command = "df -Pk " + path;
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err); // 将错误流打印到后端控制台

            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect(5000); // 5秒命令执行超时

            String line;
            // 跳过第一行标题
            reader.readLine();

            // 读取第二行数据
            if ((line = reader.readLine()) != null) {
                // 使用一个或多个空格作为分隔符
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 4) {
                    Map<String, Long> spaceInfo = new HashMap<>();
                    // parts[1] 是总容量(1K-blocks), parts[2] 是已用容量(Used)
                    spaceInfo.put("total", Long.parseLong(parts[1]));
                    spaceInfo.put("used", Long.parseLong(parts[2]));
                    return spaceInfo;
                }
            }
            return null; // 如果没有解析到数据
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

}

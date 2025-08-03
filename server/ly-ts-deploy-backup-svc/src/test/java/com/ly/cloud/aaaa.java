package com.ly.cloud;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ibm.db2.jcc.am.da;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.ly.cloud.backup.common.enums.BackupWayEnums;
import com.ly.cloud.backup.config.RedisConf;
import com.ly.cloud.backup.dto.HttpResponseDto;
import com.ly.cloud.backup.dto.LyDbBackupStrategyRecordDto;
import com.ly.cloud.backup.exception.ServiceException;
import com.ly.cloud.backup.mapper.*;
import com.ly.cloud.backup.po.*;
import com.ly.cloud.backup.service.LyDbBackupHistoryRecordService;
import com.ly.cloud.backup.service.impl.LyDbBackupHistoryRecordServiceImpl;
import com.ly.cloud.backup.util.*;
import com.ly.cloud.backup.vo.MiddlewareVo;
import com.ly.cloud.backup.vo.ServerVo;
import com.ly.cloud.quartz.util.JSchConnectionPoolUtil;
import com.ly.cloud.quartz.util.JSchServerUtil;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.xkzhangsan.time.cron.CronExpressionUtil;
import javafx.application.Application;
import jcifs.smb.SmbFile;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpProtocolException;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//@SpringBootTest
//@RunWith(SpringRunner.class)
public class aaaa {

    @Resource
    private LyDbBackupStrategyRecordMapper lyDbBackupStrategyRecordMapper;

    @Resource
    private LyDbBackupHistoryRecordMapper historyRecordMapper;

    @Resource
    private LyDbBackupHistoryRecordService historyRecordService;

    @Autowired
    private BackupManagementMapper backupManagementMapper;

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private DatabaseMapper databaseMapper;

    @Autowired
    private LySmSystemLicenseServerMapper lySmSystemLicenseServerMapper;

    private static FtpClient ftpClient;

    @Autowired
    public RedisConf redisConf;

    /*public static void main(String[] args) {

        //最好使用下面这个，上面那个超时时间不定，所以可能会导致卡住的情况
        testUrlWithTimeOut("https://blog.csdn.net/weixin_42602900/article/details/126027739", 2000);
    }


    public static void testUrlWithTimeOut(String urlString,int timeOutMillSeconds){
        long lo = System.currentTimeMillis();
        URL url;
        try {
            url = new URL(urlString);
            URLConnection co =  url.openConnection();
            co.setConnectTimeout(timeOutMillSeconds);
            co.connect();
            System.out.println("连接可用");
        } catch (Exception e1) {
            System.out.println("连接打不开!");
            url = null;
        }
        System.out.println(System.currentTimeMillis()-lo+"ms");
    }*/



    //////////////////////////////////////////////////////////////////////////////////////////


    public static void main(String[] args) {
        String uri = "http://www.gdcmxy.edu.cn/img/2023/gdcmxy/file/20231016/20231016084151_4578.docx";
        String uri1 = "http://192.168.35.210:1400/apps/xml/export?appIdListStr=ly-bd-data-governance";
        String uri2 = "https://img1.baidu.com/it/u=413643897,2296924942&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500";
//        HttpResponseDto httpResponseDto = HttpClientToInterface.getHttpResponse(uri1);
//        byte[] bytes = httpResponseDto.getBytes();
//        System.out.println(bytes);

//        HttpResponseDto httpFile = HttpClientToInterface.getHttpFile(uri);
    }


    @Test
    public void sss(){
        String sourceFile = "/tmp/zzy";
        String sourceFileSuffix = sourceFile.substring(sourceFile.lastIndexOf("/")+1);
        String sourceFilePrefix = sourceFile.substring(0,sourceFile.lastIndexOf("/"));

        JSchUtil jSchUtil =  new JSchUtil("192.168.35.213","root","Ly37621040");
        jSchUtil.execCommand("cd "+sourceFilePrefix+"&&"+"zip -r "+sourceFileSuffix+".zip "+sourceFileSuffix);
    }



    @Test
    public void duTest(){
        JSchUtil jSchUtil =  new JSchUtil("192.168.35.213","root","Ly37621040");
        String du = jSchUtil.execCommand("cd /tmp&&du -sh * 2>&1");
        System.out.println(du);
        System.out.println("-------------------------");

        DetermineFileSize(jSchUtil,"/tmp","*");
    }


    private void DetermineFileSize(JSchUtil jSchUtil,String sourceFilePrefix,String sourceFileSuffix){
        String log1 = jSchUtil.execCommand("cd "+sourceFilePrefix+"&&du -sh "+sourceFileSuffix+" 2>&1");
        if (StringUtils.isEmpty(log1) || log1.contains("du:")) {
            throw new ServiceException("没有那个文件或目录！");
        }
        String[] split = log1.split("\\s+");
        for (int i = 0; i < split.length; i++) {
            //循环为奇数时跳过本次循环
            if (i % 2 != 0) {
                continue;
            }
            if (split[i].contains("G") || split[i].contains("g")) {
                throw new ServiceException("想要下载的文件或文件目录总大小超过500MB，停止下载！");
            } else if (split[i].contains("M") || split[i].contains("m")) {
                double totalNum = Double.parseDouble(split[i].split("M")[0]);
                if (totalNum > 500) {
                    throw new ServiceException("想要下载的文件或文件目录总大小超过500MB，停止下载！");
                }
            }
        }
        System.out.println("文件或文件夹大小符合要求");

    }



    @Test
    public void ccTest() throws UnsupportedEncodingException {
        String name = "%E5%BA%94%E7%94%A8%E8%8F%9C%E5%8D%95%E6%9D%83%E9%99%90%E8%B5%84%E6%BA%90.xml";
        String decodedUrl = URLDecoder.decode(name, "UTF-8");
        System.out.println("解密后：" + decodedUrl);
    }


    @Test
    public void ddTest(){
        String url = "jdbc:oracle:thin:system/Ly37621040@192.168.35.205:1521/orcl";
        String user = "system";
        String password = "Ly37621040";
        String dataBase = "";
        boolean contains = url.contains("jdbc:oracle:thgg");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn= DriverManager.getConnection(url,user,password);
            Statement stmt=conn.createStatement();
            String query="select TABLE_NAME from all_tables where OWNER = "+"'"+dataBase+"'";
            ResultSet rs=stmt.executeQuery(query);

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void eeTest(){
        String url = "mongodb://admin:37621040@192.168.35.152:27017";
        String database = "tc";
        List<String> list = new ArrayList<>();
        MongoClientURI uri = new MongoClientURI(url);
        MongoClient mongoClient = new MongoClient(uri);
        String connectPoint = mongoClient.getConnectPoint();
        MongoDatabase mongoDatabase = null;

        mongoDatabase = mongoClient.getDatabase(database);

        MongoIterable<String> colls = mongoDatabase.listCollectionNames();
        for (String s : colls) {
            System.out.println(s);
            list.add(s);
        }
    }


    @Test
    public void qqTest() throws Exception{
        String url = "mongodb://admi:37621040@192.168.35.152:27017";
        try {
            MongoClientURI uri = new MongoClientURI(url);
            MongoClient mongoClient = new MongoClient(uri);
            String connectPoint = mongoClient.getConnectPoint();
            System.out.println(connectPoint);

        }catch (Exception e){
            System.out.println("fuck!");

        }
    }


    @Test
    public void ssTest() throws Exception{
        String s = "eyJzY2hlbWFOYW1lIjoiIiwiYnJva2VyTGlzdCI6IjE5Mi4xNjguMzUuMjEwOjE5MDkzIn0=";
        Base64.Decoder decoder = Base64.getDecoder();
        String s1 = new String(decoder.decode(s));
        JSONObject jsonObject = JSONObject.parseObject(s1);
        String ip = (String) jsonObject.get("brokerList");
        System.out.println(ip);

    }


    @Test
    public void wwTest(){
        List<String> list = new ArrayList<>();
        String url = "jdbc:mysql://192.168.35.210:3706/taier?characterEncoding=UTF-8&useSSL=false";
        String user = "root";
        String passWord = "123456";
        Connection conn=null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");  //注册数据库驱动
            //String url = "jdbc:mysql://192.168.35.202:3306/tc?characterEncoding=UTF-8&useSSL=false";  //定义连接数据库的url
            conn = DriverManager.getConnection(url,user,passWord);  //获取数据库连接
            String sql = "SELECT data_json from taier.datasource_info where id=2;";
            // 3.创建/获取sql语句对象
            Statement st = conn.createStatement();
            // 4.执行sql   executeQuery() 查询
            ResultSet rs = st.executeQuery(sql);
            for (;rs.next();) {
                String tableName = rs.getString("data_json");
                System.out.println(tableName);
                list.add(tableName);
            }
            // 5.释放资源
            rs.close();
            st.close();

            String dataJson = list.get(0);
            Base64.Decoder decoder = Base64.getDecoder();
            String s1 = new String(decoder.decode(dataJson));
            JSONObject jsonObject = JSONObject.parseObject(s1);
            String jdbcUrl = (String) jsonObject.get("jdbcUrl");
            String username = (String) jsonObject.get("username");
            String password = (String) jsonObject.get("password");
            System.out.println(jdbcUrl+"\t"+username+"\t"+password);


            conn = DriverManager.getConnection(jdbcUrl,username,password);  //获取数据库连接
            String s2 = jdbcUrl.substring(jdbcUrl.lastIndexOf("/")+1);
            String sql3 = "SELECT table_name FROM information_schema.tables WHERE table_schema="+"'"+s2+"'";
            // 3.创建/获取sql语句对象
            Statement st2 = conn.createStatement();
            // 4.执行sql   executeQuery() 查询
            ResultSet rs2 = st2.executeQuery(sql3);
            for (;rs2.next();) {
                String tableName = rs2.getString("table_name");
                System.out.println(tableName);
                list.add(tableName);
            }
            // 5.释放资源
            rs.close();
            st.close();
            conn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Test
    public void aass(){
        String s = "system/Ly37621040@192.168.35.205:1521/ORCL";
        s = s.substring(0, s.indexOf("/"));
        System.out.println(s);
    }


    @Test
    public void aasss(){
        String s = "9016005654fd00370fc44947e3622002";
        try {
            String s1 = Sm4Util.sm4EcbDecrypt(s);
            System.out.println(s1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Test
    public void dddd(){
        LyDbBackupStrategyRecordDto strategyRecordDto = new LyDbBackupStrategyRecordDto();
        LyDbBackupStrategyRecordPo strategyRecordPo = new LyDbBackupStrategyRecordPo();

        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        strategyRecordDto.setTables(list);

        BeanUtils.copyProperties(strategyRecordDto,strategyRecordPo);
        String s = "";
        List<String> tables = strategyRecordDto.getTables();
        for (int i = 0; i < tables.size(); i++) {
            if (i != tables.size() - 1) {
                s = s+tables.get(i)+",";
            }else {
                s = s+tables.get(i);
            }
        }
        strategyRecordPo.setTables(s);
        String e = strategyRecordPo.getTables();
        System.out.println(e);

    }*/


    @Test
    public void ssss(){
        String tables = "aa,ss,ee,rr,tt,yty,uu";
        String[] split = tables.split(",");
        List<String> tablesList = new ArrayList<>();
        for (int j = 0; j < split.length; j++) {
            tablesList.add(split[j]);
        }
        tablesList.forEach(System.out::println);
    }


    @Test
    public void sssss(){
        String password = null;
        try {
            password = Sm4Util.sm4EcbDecrypt("9016005654fd00370fc44947e3622002");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(password);
    }


    @Test
    public void ssssss(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,+10);
        System.out.println(calendar.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String format = simpleDateFormat.format(calendar.getTime());
        String[] split = format.split("-");
        System.out.println(split[5]+" "+split[4]+" "+split[3]+" "+split[2]+" "+split[1]+" "+"? "+split[0]);


        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        System.out.println(second+5+" "+minute+" "+hour+" "+day+" "+month+" "+"? "+year);
    }


    @Test
    public void ssssssss(){
        String s = "qqq,ddd,fff,";
        String[] split = s.split(",");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(split[i]);
        }

        System.out.println(list);
    }


    @Test
    public void wwww(){
        Integer i = BackupWayEnums.getCode("数据库备份");
        String s = i.toString();
        System.out.println(s);
        LyDbBackupStrategyRecordPo strategyRecordPo = lyDbBackupStrategyRecordMapper.selectById("1675701325107265536");
        System.out.println(strategyRecordPo);
    }


    @Test
    public void wwwww(){
        String timeStampName = "1221324324.zip";
        String substring = timeStampName.substring(0, timeStampName.indexOf("."));
        System.out.println(substring);
    }


    @Test
    public void tttt(){
        // 两个线程的线程池
        ExecutorService executor = Executors.newFixedThreadPool(2);
// jdk1.8之前的实现方式
        CompletableFuture<String> future =
                CompletableFuture.supplyAsync(
                        new Supplier<String>() {
                            @Override
                            public String get() {
                                System.out.println("开始执行任务!");
                                try {
                                    // 模拟耗时操作
                                    Thread.sleep(20000);
                                    System.out.println("我是一个特别耗时的任务");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return "耗时任务结束完毕!";
                            }
                        },
                        executor);

// 采用lambada的实现方式
        future.thenAccept(e -> System.out.println(e + " ok"));

        System.out.println("不等上面了，我先跑了"); //
    }



    @Test
    public void dddd(){
        Date date = new Date();
        String format = new SimpleDateFormat("yyyy-MM-dd").format(date);
        QueryWrapper<LyDbBackupHistoryRecordPo> wrapper = new QueryWrapper<>();
        wrapper.like("backup_time",format);
        wrapper.orderByDesc("backup_time");

        List<LyDbBackupHistoryRecordPo> pos = historyRecordMapper.selectList(wrapper);
        //List<LyDbBackupHistoryRecordPo> pos = historyRecordService.list(wrapper);
        //转化
        List<Long> collect = pos.stream().filter(s -> s.getBackupStatus().equals("1") || s.getBackupStatus().equals("2") || s.getBackupStatus().equals("3")).map(LyDbBackupHistoryRecordPo::getId).collect(Collectors.toList());
        System.out.println(collect);
    }


    @Test
    public void ddddd(){
        boolean b = CronExpressionUtil.isValidExpression("0 0 23 ? * 1");
        String n = CronExpressionUtil.getNextTimeStr("0 0 23 ? * 1");
        System.out.println(b);
        System.out.println(n);
    }


    @Test
    public void ssssa(){
        BackupManagementPo backupManagementPo = backupManagementMapper.selectOne(new LambdaQueryWrapper<BackupManagementPo>().eq(BackupManagementPo::getTotalMethod, 2));
        Long serverId = backupManagementPo.getServerId();
        ServerVo serverVo = serverMapper.selectById(serverId);
        String oracleUser = serverVo.getOracleUser();
        String oraclePassword = serverVo.getOraclePassword();
        String password = serverVo.getPassword();
        String encrypt = DesUtil.decrypt(password);
    }


    @Test
    public void aaqq(){
        DatabasePo databasePo = databaseMapper.selectOne(new LambdaQueryWrapper<DatabasePo>()
                .eq(DatabasePo::getId,"1633011146795551634,1671005413076844545,"));
        System.out.println(databasePo);
    }


    @Test
    public void aaaqq(){
        String cron = "0 25 17 * * ?";
        String minute = cron.split(" ")[1];
        int minuteNum = Integer.parseInt(minute) + 1;
        cron = cron.replaceAll(minute, String.valueOf(minuteNum));
        System.out.println(minute);
        System.out.println(cron);
    }


    @Test
    public void aaaqsq(){
        String password = DesUtil.decrypt("9016005654fd00370fc44947e3622002");
        System.out.println(password);

    }


    @Test
    public void aaaqasq(){
        try{
            int i = 0;
            int j = 0;
            if (i == 0) {
                throw new ServiceException("iiii");
            }
            if (j==0) {
                throw new RuntimeException("jjj");
            }
        }catch (ServiceException e){
            System.out.println("iiiiiii");
            throw new ServiceException(e.getMessage());
        } catch (Exception e){
            System.out.println("jjjjjj");
            throw new RuntimeException(e.getMessage());
        }

    }


    @Test
    public void aaaqassq(){
        String a = "\\'";
        String s = "\\\"";
        System.out.println(s);

    }


    @Test
    public void aaaqasszq(){
        //oracle exp
        //String url = "'"+"ly_cloud_xtgl"+"/"+"\""+"ly$_cloud_xtgl"+"\""+"@"+"192.168.35.203"+":"+"1521"+"/orcl"+"'";
        String url = " \'ly_cloud_xtgl/ly$_cloud_xtgl\\@192.168.35.203:1521/orcl\' ";
        StringBuilder command = new StringBuilder();

        String s1 =  "#!/bin/bash\n" +
                "yum -y install expect\n" +
                "expect -c \"\n" +
                "spawn su - " + "oracle12c" + "\n" +
                "expect {\n" +
                "  \\\"*assword\\\" \n" +
                "{\n" +
                "  set timeout 300; \n" +
                "  send \\\"" + "Ly37621040" + "\\r\\\";\n" +
                "}\n" +
                "  \\\"yes/no\\\" \n" +
                "{\n" +
                "  send \\\"yes\\r\\\"; exp_continue;}\n" +
                "}\n" +
                "expect eof\";";
        command.append(s1);
        command.append("#!/bin/bash\n");
        command.append("su - " + "oracle12c" + " <<EOF\n");

        command.append("exp "+url+ " file=/tmp/backup/test.dmp"+ " log=/tmp/backup/test.log statistics=none"+" \n");
        System.out.println("command:"+command.toString());

        SshUtils.DestHost host = new SshUtils.DestHost("192.168.35.205", "root", "Ly37621040");
        JSchUtil jSchUtil =  new JSchUtil("192.168.35.205", "root", "Ly37621040");

        String s2 = jSchUtil.execCommand("mkdir -p "+"/tmp/backup/");
        String s3 = jSchUtil.execCommand("touch "+"/tmp/backup/"+"test"+".dmp");
        String s4 = jSchUtil.execCommand("chmod 777 -R "+"/tmp/backup/"+"test"+".dmp");
        String s5 = jSchUtil.execCommand("touch "+"/tmp/backup/"+"test"+".log");
        String s6 = jSchUtil.execCommand("chmod 777 -R "+"/tmp/backup/"+"test"+".log");

        String stdout = null;
        try {
            Session shellSession = SshUtils.getJSchSession(host);
            stdout = SshUtils.execCommandByJSch(shellSession, String.valueOf(command));
            //断开链接
            shellSession.disconnect();
            System.out.println("stdout:"+stdout);

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }

    }




    @Test
    public void aaaqaassq(){
        String s = lyDbBackupStrategyRecordMapper.selectStrategyRecordById("1692346737914273792");
        if (s == null) {
            System.out.println("s == null");
        }else {
            System.out.println(s);
        }

    }



    @Test
    public void aaaqaaszsq(){
        //这个特殊符号不知道怎么连，还有这个连接的三个参数含义
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //Connection conn= DriverManager.getConnection("jdbc:oracle:thin:root/Ly37621040@192.168.35.205:1521/ORCL","system","Ly37621040");
            //jdbc:oracle:thin:@192.168.35.203:1521:orcl
            Connection conn= DriverManager.getConnection("jdbc:oracle:thin:@192.168.35.203:1521/orcl","ly_test_backup","ly_test_backup");
            Statement stmt=conn.createStatement();

            String execute2 = "select * from dba_directories WHERE directory_name='DATA_PUMP_DIR'";
            ResultSet resultSet = stmt.executeQuery(execute2);
            String path = null;
            while (resultSet.next()) {
                path = resultSet.getString("DIRECTORY_PATH");
            }
            stmt.close();
            conn.close();

            System.out.println(path);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void aaaqasassq(){
        JSchUtil jSchUtil =  new JSchUtil("192.168.35.205","root", "Ly37621040");
        String s = jSchUtil.execCommand("df -h /data/backup/");
        System.out.println(s);

        int n = s.length()-s.replaceAll("G", "").length();
        System.out.println("字符串中字符G有"+n+"个");
        if (n < 3) {
            throw new ServiceException();
        }

        boolean g = s.contains("G");
        StringBuffer s1 = new StringBuffer(s);
        s1 = s1.reverse();
        int i = s1.indexOf("G");
        int i1 = s1.indexOf("G",i+1);
        String substring = s1.substring(i, i1);
        substring = substring.trim();
        substring = substring.substring(1);
        StringBuffer s2 = new StringBuffer(substring);
        s2 = s2.reverse();
        System.out.println(s2);
    }



    @Test
    public void aaaqasawssq(){
        String sourceFile = "/aaa/bbb/cc.log";
        String sourceFilePrefix = sourceFile.substring(0,sourceFile.lastIndexOf("/"));
        String sourceFileSuffix = sourceFile.substring(sourceFile.lastIndexOf("/")+1);
        System.out.println(sourceFilePrefix);
        System.out.println(sourceFileSuffix);
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


    //判断文件和文件夹存不存在
    public boolean dirIsExist(String path) {
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


    @Test
    public void aaaqasaawssq(){
        String sourceFile = "smb://Lenovo:a0000@192.168.49.91/photo/cifs/aaa.txt";
        String sourceFile2 = sourceFile;
        if (sourceFile.substring(sourceFile.length() - 1).equals("/")) {
            sourceFile2 = sourceFile.substring(0,sourceFile.length() - 1);
        }
        String sourceFilePrefix = sourceFile2.substring(0,sourceFile2.lastIndexOf("/"));
        String sourceFileSuffix = sourceFile2.substring(sourceFile2.lastIndexOf("/")+1);
        String zipSourceFileSuffix = sourceFileSuffix+".zip";
        System.out.println(sourceFilePrefix);
        System.out.println(sourceFileSuffix);
        System.out.println(zipSourceFileSuffix);

    }


    @Test
    public void aaaqasaaswssq(){
        String sourceFile = "smb://Lenovo:a0000@172.16.39.138/photo/cifs/1.jpg";
        try {
            SmbFile remoteFile = new SmbFile(sourceFile);
            remoteFile.connect();
            boolean b = remoteFile.exists();
            System.out.println(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void aaaqasaaswsasq(){
        String uri = "http://www.gdcmxy.edu.cn/img/2023/gdcmxy/file/20231016/20231016084151_4578.docx";
        String uri1 = "http://192.168.35.210:1400/apps/xml/export?appIdListStr=ly-bd-data-governance";
        String uri2 = "https://img1.baidu.com/it/u=413643897,2296924942&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500";
//        HttpResponseDto httpResponseDto = HttpClientToInterface.getHttpResponse(uri1);
//        byte[] bytes = httpResponseDto.getBytes();
//        System.out.println(bytes);

//        HttpResponseDto httpFile = HttpClientToInterface.getHttpFile(uri);


    }



    @Test
    public void aaaqasaqaswsasq(){
        try {
            String password = Sm4Util.sm4EcbDecrypt("1fea8512fb849b6a28b3a5d62f4e7275");
            System.out.println(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void aaaqasaqacsswsasq(){
        String strLink = "11111111111111";
        URL url;
        try {
            url = new URL(strLink);
            HttpURLConnection connt = (HttpURLConnection)url.openConnection();
            connt.setRequestMethod("HEAD");
            String strMessage = connt.getResponseMessage();
            if (strMessage.compareTo("Not Found") == 0) {
                System.out.println("false");
            }
            connt.disconnect();
        } catch (Exception e) {
            System.out.println("false");
        }
        System.out.println("true");
    }



    @Test
    public void aaaqasaqasswsasq(){
        //String url = "mongodb://admin:37621040@192.168.35.152:27017";
        try {
            // 设置MongoDB的连接参数
//            String host = "192.168.35.111";
            String host = "192.168.35.152";
            int port = 27017;
            String database = "admin";
            String username = "admin1";
            String password = "37621040";

            //创建MongoCredential对象
            MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());

            //创建MongoClientOptions对象，并设置连接超时时间
            MongoClientOptions options = MongoClientOptions.builder()
                    .connectTimeout(5000)
                    .build();
            //创建MongoClient对象
            MongoClient client = new MongoClient(new ServerAddress(host, port), Collections.singletonList(credential), options);
            //获取MongoDatabase对象
            MongoDatabase db = client.getDatabase(database);
            db.getCollection("test").find().iterator();
            //打印连接状态
            System.out.println("Connected to MongoDB!");
            //关闭MongoClient连接
            client.close();
        } catch (Exception e) {
            System.out.println("DisConnected to MongoDB!");
            e.printStackTrace();
        }
    }


    @Test
    public void aaaqasaqssasswsasq(){
        long incr = redisConf.incr("aaaaaa", 1);
        redisConf.expire("aaaaaa",10);
        System.out.println(redisConf.getExpire("aaaaaa"));
        System.out.println(incr);
    }


    @Test
    public void aaaqasaqssasaswsasq(){
        redisConf.del("aaaaaa");
    }

    @Test
    public void aaaqasaqsasasaswsasq(){
        System.out.println(redisConf.hasKey("aaaaaa"));
        System.out.println(redisConf.getByBound("aaaaaa"));
    }

    @Test
    public void aaaqasaqasasasaswsasq(){
        String sourceFile = "/data/backup/portainer_data_20231114020001.zip";
        try {
            //连接ftp服务器
            FtpUtil2.loginFtp3("192.168.35.202",21,"lysk","ly37621040");
            //连接ftp服务器
            FtpUtil2.connectServer("192.168.35.202",21,"lysk","ly37621040",sourceFile);
            boolean b = FtpUtil2.dirIsExist(sourceFile);
            System.out.println(b);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void aaaqaasaqasasasaswsasq(){
        String s = "1@1111as";
        //判断有无特殊字符
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        System.out.println(m.find());
        // 定义特殊字符的正则表达式
        String specialChars = "[!@#$%^&*()_+\\[\\]{};':\",./<>?\\\\|`~-]";
        s = s.replaceAll("\\$","\\$");
        System.out.println(s);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            // 判断字符是否为特殊字符
            if (Pattern.matches(specialChars, String.valueOf(ch))) {
                // 字符串包含特殊字符，执行相应的操作
                System.out.println(ch);
                s = s.replaceAll(String.valueOf(ch),"\\\\"+ch);
            }
        }
        System.out.println(s);

        String password = "ly$_cloud_xtgl";




    }

    @Test
    public void test() {
        String str = "ly$_cloud_xtgl";
        String[] arr = str.split("");
        System.out.println("arr:"+ Arrays.toString(arr));
        List<String> list = Arrays.asList(arr);

        String regex = "[^a-zA-Z0-9\\s]";
        Pattern pattern = Pattern.compile(regex);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <list.size(); i++) {
            Matcher matcher = pattern.matcher(list.get(i));
            if (matcher.find() && !list.get(i).equals("_") && !list.get(i).equals("-")){
                System.out.println("特殊符号：" + matcher.group());
                stringBuilder.append("\\\\").append(list.get(i));
            }else {
                stringBuilder.append(list.get(i));
            }
        }
        System.out.println("stringBuilder:"+stringBuilder);

    }


    @Test
    public void test3(){
        JSchUtil jSchUtil =  new JSchUtil("192.168.35.202","root", "Ly37621040@@",222,3 * 60 * 1000);

        //exp \'ly_cloud_xtgl/\"ly\$_cloud_xtgl\"@192.168.35.203:1521/orcl\' file=/opt/1706076982661.dmp log=/opt/1706076982661.log statistics=none
        String s = jSchUtil.execCommand("exp \\'ly_cloud_xtgl/\\\"ly\\$_cloud_xtgl\\\"@192.168.35.203:1521/orcl\\' file=/tmp/backup/oracle/1.dmp log=/tmp/backup/oracle/1.log statistics=none");
        //String s = jSchUtil.execCommand("exp \\'ly_cloud_xtgl/\\\"ly\\$_cloud_xtgl\\\"@192.168.34.144:1521/orcl\\' file=/data/backup/oracle/1706172217972.dmp log=/data/backup/oracle/1706172217972.log statistics=none tables=LT_RZ_YYAC_USER");
        System.out.println(s);
    }


    @Test
    public void test2(){
        JSchUtil jSchUtil =  new JSchUtil("192.168.35.202","root", "Ly37621040@@",222,3 * 60 * 1000);
        String s = jSchUtil.execCommand("echo -e \"[mongodb-org-3.4]\nname=MongoDB Repository\nbaseurl=https://repo.mongodb.org/yum/redhat/7/mongodb-org/3.4/x86_64/\ngpgcheck=1\nenabled=1\ngpgkey=https://www.mongodb.org/static/pgp/server-3.4.asc\" > /etc/yum.repos.d/mongodb-org-3.4.repo");
        //exp \'ly_cloud_xtgl/\"ly\$_cloud_xtgl\"@192.168.35.203:1521/orcl\' file=/opt/1706076982661.dmp log=/opt/1706076982661.log statistics=none
//        String s = jSchUtil.execCommand("exp \\'ly_cloud_xtgl/\\\"ly\\$_cloud_xtgl\\\"@192.168.35.203:1521/orcl\\' file=/tmp/backup/oracle/1.dmp log=/tmp/backup/oracle/1.log statistics=none 2>&1");
        //String s = jSchUtil.execCommand("exp \\'ly_cloud_xtgl/\\\"ly\\$_cloud_xtgl\\\"@192.168.34.144:1521/orcl\\' file=/data/backup/oracle/1706172217972.dmp log=/data/backup/oracle/1706172217972.log statistics=none tables=LT_RZ_YYAC_USER");
        System.out.println(s);
    }

    @Test
    public void test21(){
        List<LyDbBackupHistoryRecordPo> historyRecordPoList = historyRecordMapper.selectList(new LambdaQueryWrapper<LyDbBackupHistoryRecordPo>().eq(LyDbBackupHistoryRecordPo::getBackupStatus, "0").or().eq(LyDbBackupHistoryRecordPo::getBackupStatus, "5"));
        for (int i = 0; i < historyRecordPoList.size(); i++) {
            Date operationTime = historyRecordPoList.get(i).getOperationTime();
            Date nowTime = new Date();
            long duration = nowTime.getTime() - operationTime.getTime();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            System.out.println(minutes);
        }
    }

    @Test
    public void test212(){
        String originalString = "Hello World World and World";
        String stringToReplace = "World";
        String replacementString = "Java";

        String newString = originalString.replace(stringToReplace, replacementString);
        System.out.println(newString); // 输出: Hello Java
    }

    @Test
    public void RandomSizeGenerator() {
        int randomInt = ThreadLocalRandom.current().nextInt(10, 31);
        // 转换为1位小数的浮点值
        double size = randomInt / 10.0;
        // 格式化为字符串
        String s = size+"MB";
        System.out.println(s);
    }














}




































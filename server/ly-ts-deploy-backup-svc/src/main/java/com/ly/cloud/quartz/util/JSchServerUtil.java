package com.ly.cloud.quartz.util;

import com.ly.cloud.backup.exception.ServiceException;
import com.ly.cloud.backup.po.DatabasePo;
import com.ly.cloud.backup.util.DesUtil;
import com.ly.cloud.backup.util.JSchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*zzy*/
public class JSchServerUtil {

    private static final Logger log = LoggerFactory.getLogger(JSchServerUtil.class);

    //查询备份路径所在的磁盘分区有多少（最小为1G，否则报错）
    public static Double checkServerSpace(JSchUtil jSchUtil, String backupPath){
        //创建路径
        jSchUtil.execCommand("mkdir -p "+backupPath);
        String data = jSchUtil.execCommand("df -h "+backupPath);
        System.out.println(data);
        //检查字符串是不是小于3个G
        int n = data.length()-data.replaceAll("G", "").length();
        System.out.println("字符串中字符G有"+n+"个");
        if (n < 3) {
            throw new ServiceException("服务器磁盘路径空间不足一个G，不可备份。路径："+backupPath);
        }

        boolean g = data.contains("G");
        StringBuffer data1 = new StringBuffer(data);
        data1 = data1.reverse();
        int i = data1.indexOf("G");
        int i1 = data1.indexOf("G",i+1);
        String substring = data1.substring(i, i1);
        substring = substring.trim();
        substring = substring.substring(1);
        StringBuffer data2 = new StringBuffer(substring);
        data2 = data2.reverse();
        System.out.println(data2);
        return Double.parseDouble(String.valueOf(data2));
    }


    //检查MySQL的表空间
    public static Double checkMysqlSpace(DatabasePo databasePo,String dataBase, List<String> tablesList){
        String url = "jdbc:mysql://"+databasePo.getIpv4()+":"+databasePo.getPort()+"/"+databasePo.getDatabaseName()+"?characterEncoding=UTF-8&useSSL=false";
        Connection conn=null;
        String tableName = null;
        Double space = 0d;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");  //注册数据库驱动
            //String url = "jdbc:mysql://192.168.35.202:3306/tc?characterEncoding=UTF-8&useSSL=false";  //定义连接数据库的url
            conn = DriverManager.getConnection(url,databasePo.getUser(), DesUtil.decrypt(databasePo.getPassword()));  //获取数据库连接
            String s1 = url.substring(0, url.indexOf("?"));
            String s2 = s1.substring(s1.lastIndexOf("/")+1);
            //String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema="+"'"+s2+"'";
            String sql = null;
            Statement st = null;
            ResultSet rs = null;
            //不选表格
            if (tablesList.size()==1 && tablesList.get(0).equals("")) {
                sql = "SELECT table_schema AS \"Database name\",\n" +
                        "ROUND(SUM(data_length + index_length) / 1024 / 1024 / 1024, 8) AS \"Size (GB)\"\n" +
                        "FROM information_schema.TABLES\n" +
                        "WHERE table_schema = '" + dataBase + "'\n" +
                        "GROUP BY table_schema";
                // 3.创建/获取sql语句对象
                st = conn.createStatement();
                // 4.执行sql   executeQuery() 查询
                rs = st.executeQuery(sql);
                for (;rs.next();) {
                    tableName = rs.getString("Size (GB)");
                    space = Double.parseDouble(tableName);
                }
            //选择表格
            }else {
                for (int i = 0; i < tablesList.size(); i++) {
                    sql = "SELECT\n" +
                            "SUM(DATA_LENGTH) + SUM(INDEX_LENGTH) AS TOTAL_SIZE,\n" +
                            "ROUND(SUM(data_length + index_length) / 1024 / 1024 / 1024, 8) AS \"Size (GB)\"\n" +
                            "FROM\n" +
                            "information_schema.TABLES\n" +
                            "WHERE\n" +
                            "TABLE_SCHEMA = '" + dataBase + "' AND TABLE_NAME = '" + tablesList.get(i) + "'";
                    // 3.创建/获取sql语句对象
                    st = conn.createStatement();
                    // 4.执行sql   executeQuery() 查询
                    rs = st.executeQuery(sql);
                    for (;rs.next();) {
                        tableName = rs.getString("Size (GB)");
                        space = space + Double.parseDouble(tableName);
                    }
                }
            }
            // 5.释放资源
            rs.close();
            st.close();
            conn.close();
            return space;
        }catch(Exception e){
            e.printStackTrace();
        }
        return space;
    }


    //检查一个字符串是否有特殊字符，如果有，则在前面加两条\\
    public static String checkSpecialChars(String s){
        //判断有无特殊字符
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        if (!m.find()) {
            return s;
        }
        String[] arr = s.split("");
        System.out.println("arr:"+ Arrays.toString(arr));
        List<String> list = Arrays.asList(arr);

        String regex = "[^a-zA-Z0-9\\s]";
        Pattern pattern = Pattern.compile(regex);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <list.size(); i++) {
            Matcher matcher = pattern.matcher(list.get(i));
            if (matcher.find() && !list.get(i).equals("_") && !list.get(i).equals("-")){
                stringBuilder.append("\\").append(list.get(i));
            }else {
                stringBuilder.append(list.get(i));
            }
        }
        return String.valueOf(stringBuilder);
    }

    //检查服务器是否有对应的mysqldump命令，如果没有，则给他安装
    public static String checkMysqldump(JSchUtil jSchUtil){
        String s = jSchUtil.execCommand("mysqldump 2>&1");
        if (!s.contains("command not found")) {
            return "";
        }
        //如果有command not found则进行下列操作
        return jSchUtil.execCommand("yum -y install holland-mysqldump.noarch 2>&1");
    }

    //检查服务器是否有对应的checkMongodump命令，如果没有，则给他安装
    public static String checkMongodump(JSchUtil jSchUtil){
        String s = jSchUtil.execCommand("mongo -version 2>&1");
        if (!s.contains("command not found") && s.contains("version v4")) {
            return "";
        }
        if (!s.contains("command not found") && !s.contains("version v4")) {
            //卸载之前的mongoDB版本
            jSchUtil.execCommand("yum -y remove mongodb-org*");
        }
        jSchUtil.execCommand("echo -e \"[mongodb-org-4.0]\nname=MongoDB Repository\nbaseurl=https://repo.mongodb.org/yum/redhat/7/mongodb-org/4.0/x86_64/\ngpgcheck=1\nenabled=1\ngpgkey=https://www.mongodb.org/static/pgp/server-4.0.asc\" > /etc/yum.repos.d/mongodb-org-4.0.repo 2>&1");
        String s1 = jSchUtil.execCommand("yum -y install mongodb-org");
        return s1;
    }
}


































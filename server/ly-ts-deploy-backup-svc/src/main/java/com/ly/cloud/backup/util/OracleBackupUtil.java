package com.ly.cloud.backup.util;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/*oracle备份命名工具类*/
public class OracleBackupUtil {

    private static final Logger logger = LoggerFactory.getLogger(MinioClientUtils.class);

    /*
    * url：备份的Oracle url
    * fileBackupPath：文件备份全路径
    * logBackupPath：生成的日志全路径
    * ownerName：Oracle下面所有者名字
    * */
    public void OracleBackupToShell(String url,String fileBackupPath,String logBackupPath,String ownerName) {
        StringBuilder command = new StringBuilder();

        String oracleUsername = "oracle12c";
        String serverPassword = "Ly37621040";
        String s1 =  "#!/bin/bash\n" +
                "yum -y install expect\n" +
                "expect -c \"\n" +
                "spawn su - " + oracleUsername + "\n" +
                "expect {\n" +
                "    \\\"*assword\\\" \n" +
                "                {\n" +
                "                    set timeout 300; \n" +
                "                    send \\\"" + serverPassword + "\\r\\\";\n" +
                "                }\n" +
                "    \\\"yes/no\\\" \n" +
                "                {\n" +
                "                    send \\\"yes\\r\\\"; exp_continue;}\n" +
                "                }\n" +
                "expect eof\";";
        command.append(s1);
        command.append("#!/bin/bash\n");
        command.append("su - " + oracleUsername + " <<EOF\n");
        //执行exp命令
        //command.append("exp LY_DG/LY_DG@192.168.35.205:1521/ORCL file=/tmp/backup/oracle/USR_LY_TEST.dmp log=/tmp/backup/oracle/USR_LY_TEST.log statistics=none \n");
        command.append("exp "+url+" file="+fileBackupPath+" log="+logBackupPath+" statistics=none owner="+ownerName+" \n");
        command.append("EOF;");
        System.out.println("command:"+command.toString());

        String fileBackupPath2 = fileBackupPath.substring(0, fileBackupPath.lastIndexOf("/")+1);
        String logBackupPath2 = logBackupPath.substring(0, logBackupPath.lastIndexOf("/")+1);
        SshUtils.DestHost host = new SshUtils.DestHost("192.168.35.205", "root", "Ly37621040");
        JSchUtil jSchUtil =  new JSchUtil("192.168.35.205","root", "Ly37621040");
        String s2 = jSchUtil.execCommand("mkdir -p "+fileBackupPath2+" 2>&1");
        String s7 = jSchUtil.execCommand("mkdir -p "+logBackupPath2+" 2>&1");
        String s3 = jSchUtil.execCommand("touch "+fileBackupPath+" 2>&1");
        String s4 = jSchUtil.execCommand("chmod 777 -R "+fileBackupPath+" 2>&1");
        String s5 = jSchUtil.execCommand("touch "+logBackupPath+" 2>&1");
        String s6 = jSchUtil.execCommand("chmod 777 -R "+logBackupPath+" 2>&1");

        try {
            Session shellSession = SshUtils.getJSchSession(host);
            String stdout = SshUtils.execCommandByJSch(shellSession, String.valueOf(command));
            //断开链接
            shellSession.disconnect();
            System.out.println("stdout:"+stdout);

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }

    }


}


































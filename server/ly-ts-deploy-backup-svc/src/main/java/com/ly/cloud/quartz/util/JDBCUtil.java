package com.ly.cloud.quartz.util;

import com.ly.cloud.backup.mapper.LyDbBackupHistoryRecordMapper;
import com.ly.cloud.backup.po.LyDbBackupHistoryRecordPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtil {

    public static void connect(){
        //通过jdbc去控制事务
        Connection conn = null;
        //1、注册驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //2、获得connection
            conn = DriverManager.getConnection("jdbc:mysql://192.168.35.202:3306/", "root", "Ly37621040");
            //手动开启事务,先声明不自动提交
            conn.setAutoCommit(false);
            //3、获得执行平台
            Statement stmt = conn.createStatement();
            //4、操作sql
            stmt.executeUpdate("INSERT INTO ly_dc.ly_db_backup_history_record\n" +
                    "(id, title, backup_strategy_type, backup_time, backup_status, operator_id, operation_time, remark, time_stamp, strategy_id, proportion)\n" +
                    "VALUES(1676475237684412416, 'sasas', '立即', '2023-07-05 14:16:59', '3', '2', '2023-07-05 14:22:15', NULL, '1688537841392.zip', '1676475237617303552', '100');\n");
            //提交事务
            conn.commit();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Autowired
    private static LyDbBackupHistoryRecordMapper historyRecordMapper;
    public static void connect2(LyDbBackupHistoryRecordPo historyRecordPo){
        historyRecordMapper.insert(historyRecordPo);
    }
}

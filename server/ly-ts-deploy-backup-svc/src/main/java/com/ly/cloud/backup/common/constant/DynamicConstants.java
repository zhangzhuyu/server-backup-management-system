package com.ly.cloud.backup.common.constant;

import com.ly.cloud.backup.po.DatabasePo;
import com.ly.cloud.backup.service.DatabaseService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 全链路-数据库监控指标
 * @author SYC
 */
@Data
public class DynamicConstants {

    @Autowired
    private static DatabaseService databaseService;


    public final static String BZK_CODE= "1";




    public static String getDatabaseInfo(String sourcetype) {
        //从数据库中获取所有的数据源
        AtomicReference<Long> databaseId  = null;
        List<DatabasePo> databasePoList = databaseService.list();
        databasePoList.forEach(databasePo -> {
            if (sourcetype.equals(databasePo.getSourceType())){
                databaseId.set(databasePo.getId());
            }
        });
        return databaseId.get().toString();
    };




}

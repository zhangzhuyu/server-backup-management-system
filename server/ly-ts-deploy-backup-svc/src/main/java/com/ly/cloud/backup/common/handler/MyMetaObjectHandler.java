package com.ly.cloud.backup.common.handler;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * @author admin
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {

        this.setFieldValByName("operation_time", Calendar.getInstance().getTime(), metaObject);
        this.setFieldValByName("operationTime", Calendar.getInstance().getTime(), metaObject);
//        this.setFieldValByName("createTime", Calendar.getInstance().getTime(), metaObject);
//        this.setFieldValByName("updateTime", Calendar.getInstance().getTime(), metaObject);
        //this.setInsertFieldValByName("operator", "Jerry", metaObject);//@since 快照：3.0.7.2-SNAPSHOT， @since 正式版暂未发布3.0.7
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("operation_time", Calendar.getInstance().getTime(), metaObject);
        this.setFieldValByName("operationTime", Calendar.getInstance().getTime(), metaObject);
//        this.setFieldValByName("updateTime", Calendar.getInstance().getTime(), metaObject);
        //this.setUpdateFieldValByName("operator", "Tom", metaObject);//@since 快照：3.0.7.2-SNAPSHOT， @since 正式版暂未发布3.0.7
    }


}


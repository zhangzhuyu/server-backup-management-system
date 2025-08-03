package com.ly.cloud.backup.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.common.annotation.DataSourceAnno;
import com.ly.cloud.backup.common.constant.DynamicConstants;
import com.ly.cloud.backup.common.constant.DynamicVersion;
import com.ly.cloud.backup.po.DatabasePo;
import com.ly.cloud.backup.vo.DatabaseMonitoringVo;

/**
 * 全链路-数据库监控指标
 * @author SYC
 */
@DataSourceAnno(beanName = DynamicConstants.BZK_CODE,version = DynamicVersion.VERSION_6)
public interface DatabaseMonitoringMapper  extends BaseMapper<DatabasePo> {

    /**
     * oracle数据库允许的最大连接数
     */
    String getOracleDatabaseMaxConnections();

    /**
     * oracle数据库允许的最大连接数
     */
    String getOracleDatabaseCurrentConnections();

    /**
     * 实例名称、数据库版本、数据库状态、实例状态
     */
    DatabaseMonitoringVo getOracleDatabaseInfo();

    /**
     * 获取数据库表空间信息
     */
    DatabaseMonitoringVo getOracleDatabaseTablespace();


}

package com.ly.cloud.backup.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * @author SYC
 * 配置Flyway参数，并设置创建Bean顺序高于所有
 */
@Configuration
public class FlywayConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${spring.flyway.enabled:false}")
    private Boolean enabled;

    @Value("${spring.flyway.baseline-on-migrate:true}")
    private Boolean baselineOnMigrate;

    @Value("${spring.flyway.baseline-on-migrate:false}")
    private Boolean validateOnMigrate;

    @Value("${spring.flyway.encoding:UTF-8}")
    private String encoding;

    @Value("${spring.flyway.locations:db/migration}")
    private String locations;

    @Value("${spring.flyway.sql-migration-prefix:V}")
    private String sqlMigrationPrefix;

    @Value("${spring.flyway.sql-migration-separator:__}")
    private String sqlMigrationSeparator;

    @Value("${spring.flyway.sql-migration-suffixes:.sql}")
    private String sqlMigrationSuffixes;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.flyway.ignore-failed-future-migration:true}")
    private Boolean ignoreFailedFutureMigration;

    @PostConstruct
    public void migrate() {
        Flyway flyway = Flyway.configure().dataSource(url, username, password).load();
        // 设置flyway扫描sql升级脚本、java升级脚本的目录路径或包路径（表示是src/main/resources/flyway下面，前缀默认为src/main/resources，因为这个路径默认在classpath下面）
        flyway.setLocations(locations);
        // 设置sql脚本文件的编码
        flyway.setEncoding(encoding);
        flyway.setOutOfOrder(true);
        flyway.setBaselineOnMigrate(baselineOnMigrate);
        flyway.setValidateOnMigrate(validateOnMigrate);
        flyway.setSqlMigrationPrefix(sqlMigrationPrefix);
        flyway.setSqlMigrationSeparator(sqlMigrationSeparator);
        flyway.setSqlMigrationSuffixes(sqlMigrationSuffixes);
        flyway.setIgnoreFutureMigrations(ignoreFailedFutureMigration);
        if (enabled != null && enabled == true) {
            logger.info("正在初始化数据库表结构，请耐心等待...");
            try {
                flyway.migrate();
            } catch (FlywayException e) {
                logger.error(e.getLocalizedMessage(),e);
            }
            //初始化完成后，更新CLOB、BLOB字段内容。
            logger.info("数据库初始化完成");

        }
    }
}
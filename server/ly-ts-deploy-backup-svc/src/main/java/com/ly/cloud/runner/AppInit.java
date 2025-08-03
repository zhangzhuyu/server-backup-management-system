package com.ly.cloud.runner;//package com.ly.cloud.mssp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import co.elastic.apm.attach.ElasticApmAttacher;

@Component
public class AppInit implements CommandLineRunner {

    @Value("${spring.application.name:ly-ts-deploy-backup-svc}")
    private  String appName;

    @Value("${apm.enabled:false}")
    private  String apm_enabled;

    @Value("${apm.server.url:http://apm-server:8200}")
    private  String apm_server_url;

    @Value("${apm.server.env:production}")
    private  String apm_evn;

    @Value("${apm.server.name:default}")
    private  String serverName;

    private final static Logger logger = LoggerFactory.getLogger(AppInit.class);

    /**
     * apm通过读取docker内部的环境变量进行启动设置是否启动监控和监控地址设置
     */
    @Override
    public void run(String... args) throws Exception {
            String apm_enabled = System.getenv("apm.enabled");
            //环境中取不到就从配置文件中取
            if (StringUtils.isEmpty(apm_enabled)) {
                apm_enabled = this.apm_enabled;
            }
            logger.info("env apm_enabled:"+apm_enabled);
            if (Boolean.parseBoolean(apm_enabled)){
            // TODO Auto-generated method stub
            Map<String, String> configuration = new HashMap<>();
            String apm_server_url = System.getenv("apm.server.url");
            String apm_evn = System.getenv("apm.server.env");
            String serverName = System.getenv("apm.server.name");
            logger.info("env apm_server_url:"+apm_server_url);
            logger.info("env apm.server.env:"+apm_evn);
            logger.info("env apm.server.name:"+serverName);

            if (StringUtils.isEmpty(apm_server_url)) {
                apm_server_url = this.apm_server_url;
            }
            if (StringUtils.isEmpty(apm_evn)) {
                apm_evn = this.apm_evn;
            }
            if (StringUtils.isEmpty(serverName)) {
                if ("default".equals(this.serverName)) {
                    serverName = this.appName;
                } else {
                    serverName = this.serverName;
                }
            }

            logger.info("yml apm_enabled:"+apm_enabled);
            logger.info("yml apm_server_url:"+apm_server_url);
            logger.info("yml apm.server.env:"+apm_evn);
            logger.info("yml apm.server.name:"+serverName);

            if(!StringUtils.isEmpty(apm_enabled) && "true".equals(apm_enabled.trim())) {
                configuration.put("recording", apm_enabled.trim());
            }else {
                //默认是false 不启动
                configuration.put("recording", "false");
            }
            if(!StringUtils.isEmpty(apm_server_url)) {
                configuration.put("server_urls", apm_server_url.trim());
            }else {
                //默认是url路径
                configuration.put("server_urls", "http://172.198.0.100:8200");
            }
            if(!StringUtils.isEmpty(apm_evn)) {
                configuration.put("environment", apm_evn.trim());
            }else {
                //默认是production,开发环境
                configuration.put("environment", "production");
            }
            configuration.put("service_name", serverName);

            System.out.println("apm启动设置为："+configuration.get("recording")+",apm服务端地址是："+configuration.get("server_urls")+",服务名称："+serverName);



            ElasticApmAttacher.attach(configuration);
            Span span = ElasticApm.currentSpan();
            span.setLabel("appName",serverName);
            //版本号，base,xmd 区分是否定制，  开发分支 master dev prod test
            span.setLabel("appVersion","base-master-1.0.0");
        }else {
            logger.info("apm已关闭");
        }



    }

}
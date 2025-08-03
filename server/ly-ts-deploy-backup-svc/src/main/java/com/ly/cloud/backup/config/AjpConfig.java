package com.ly.cloud.backup.config;


import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * @author SYC
 * tomcat启动端口修改，修复漏洞
 */
//@Configuration
public class AjpConfig {

    @Value("${ajpPort:5012}")
    private int ajpPort;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return server -> {
            if (server instanceof TomcatServletWebServerFactory) {
                ((TomcatServletWebServerFactory)   server).addAdditionalTomcatConnectors(redirectConnector());
            }
        };
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("AJP/1.3");
        connector.setScheme("http");
        connector.setPort(this.ajpPort);
        connector.setSecure(false);
        connector.setAllowTrace(false);
        return connector;
    }
}

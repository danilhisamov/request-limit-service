package ru.danilkhisamov.requestmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableConfigurationProperties
@EnableScheduling
public class RequestMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestMonitoringApplication.class, args);
    }

}

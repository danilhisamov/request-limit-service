package ru.danilkhisamov.requestmonitoring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "application.limit")
public class RequestLimitConfigurationProperties {
    private Integer requests;
    private Integer minutes;
}

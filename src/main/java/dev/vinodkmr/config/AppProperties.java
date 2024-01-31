package dev.vinodkmr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.config")
public class AppProperties {
    private String botName;
    private String botToken;
    private String domain;
}

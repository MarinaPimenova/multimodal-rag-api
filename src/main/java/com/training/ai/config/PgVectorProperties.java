package com.training.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix ="datasource.pgvector")
@Component
public class PgVectorProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}

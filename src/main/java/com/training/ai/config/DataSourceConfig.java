package com.training.ai.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/** @noinspection rawtypes*/
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Primary
    @Bean(name = "pgvectorDsProperties")
    public DataSourceProperties pgvectorDsProperties(PgVectorProperties properties) {
        DataSourceProperties ds = new DataSourceProperties();
        ds.setUrl(properties.getUrl());
        ds.setUsername(properties.getUsername());
        ds.setPassword(properties.getPassword());
        ds.setDriverClassName(properties.getDriverClassName());
        return ds;
    }

    @Primary
    @Bean(name = "pgvectorDataSource")
    public DataSource pgvectorDataSource(
            @Qualifier("pgvectorDsProperties") DataSourceProperties properties) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(properties.getDriverClassName());
        dataSourceBuilder.url(properties.getUrl());
        dataSourceBuilder.username(properties.getUsername());
        dataSourceBuilder.password(properties.getPassword());

        return dataSourceBuilder.build();
    }
}

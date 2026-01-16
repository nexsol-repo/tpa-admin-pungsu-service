package com.nexsol.tpa.storage.db.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BoonDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "storage.datasource.boon")
    public HikariConfig boonHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public HikariDataSource boonDataSource(@Qualifier("boonHikariConfig") HikariConfig config) {
        return new HikariDataSource(config);
    }

}
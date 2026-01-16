package com.nexsol.tpa.storage.db.core.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.nexsol.tpa.storage.db.core")
@EnableJpaRepositories(basePackages = "com.nexsol.tpa.storage.db.core",
        entityManagerFactoryRef = "coreEntityManagerFactory", // Default
        transactionManagerRef = "coreTransactionManager" // Default
)
public class CoreJpaConfig {

    @Primary
    @Bean(name = "coreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coreEntityManagerFactory(
            @Qualifier("coreDataSource") DataSource dataSource, // CoreDataSourceConfig에서
                                                                // 등록한 빈 주입
            EntityManagerFactoryBuilder builder) {

        return builder.dataSource(dataSource)
            .packages("com.nexsol.tpa.storage.db.core") // Core 엔티티 위치
            .persistenceUnit("core")
            .build();
    }

    // 2. TransactionManager 등록 (Primary)
    @Primary
    @Bean(name = "coreTransactionManager")
    public PlatformTransactionManager coreTransactionManager(
            @Qualifier("coreEntityManagerFactory") LocalContainerEntityManagerFactoryBean coreEntityManagerFactory) {

        return new JpaTransactionManager(Objects.requireNonNull(coreEntityManagerFactory.getObject()));
    }

}

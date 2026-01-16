package com.nexsol.tpa.storage.db.core.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.nexsol.tpa.storage.db.boon", // [핵심] boon 패키지
                                                                        // 스캔
        entityManagerFactoryRef = "boonEntityManagerFactory", transactionManagerRef = "boonTransactionManager")
public class BoonJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean boonEntityManagerFactory(
            @Qualifier("boonDataSource") DataSource dataSource, EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource)
            .packages("com.nexsol.tpa.storage.db.boon") // 엔티티 위치
            .persistenceUnit("boon")
            .build();
    }

    @Bean
    public PlatformTransactionManager boonTransactionManager(
            @Qualifier("boonEntityManagerFactory") LocalContainerEntityManagerFactoryBean boonEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(boonEntityManagerFactory.getObject()));
    }

}
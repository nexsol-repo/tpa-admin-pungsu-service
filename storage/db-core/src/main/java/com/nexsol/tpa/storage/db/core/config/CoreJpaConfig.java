package com.nexsol.tpa.storage.db.core.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.nexsol.tpa.storage.db.core",
        entityManagerFactoryRef = "coreEntityManagerFactory", transactionManagerRef = "coreTransactionManager")
public class CoreJpaConfig {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter) {
        // [수정] 생성자 시그니처 변경 대응: Function<DataSource, Map<String, ?>> 전달
        // dataSource -> new HashMap<>() 형태로 람다식 사용
        return new EntityManagerFactoryBuilder(jpaVendorAdapter, dataSource -> new HashMap<>(), null);
    }

    @Primary
    @Bean(name = "coreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coreEntityManagerFactory(
            @Qualifier("coreDataSource") DataSource dataSource, EntityManagerFactoryBuilder builder) {

        return builder.dataSource(dataSource)
            .packages("com.nexsol.tpa.storage.db.core")
            .persistenceUnit("core")
            .build();
    }

    @Primary
    @Bean(name = "coreTransactionManager")
    public PlatformTransactionManager coreTransactionManager(
            @Qualifier("coreEntityManagerFactory") LocalContainerEntityManagerFactoryBean coreEntityManagerFactory) {

        return new JpaTransactionManager(Objects.requireNonNull(coreEntityManagerFactory.getObject()));
    }

}
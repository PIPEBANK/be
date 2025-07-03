package com.pipebank.ordersystem.global.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    // ============= WEB DB 설정 (Primary) =============
    @Primary
    @Bean(name = "webDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.web")
    public DataSource webDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "webEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean webEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("webDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.pipebank.ordersystem.domain.web")  // Web 엔티티 패키지
                .persistenceUnit("web")
                .properties(webJpaProperties())
                .build();
    }

    @Primary
    @Bean(name = "webTransactionManager")
    public PlatformTransactionManager webTransactionManager(
            @Qualifier("webEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    // ============= ERP DB 설정 =============
    @Bean(name = "erpDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.erp")
    public DataSource erpDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "erpEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean erpEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("erpDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.pipebank.ordersystem.domain.erp")  // ERP 엔티티 패키지
                .persistenceUnit("erp")
                .properties(erpJpaProperties())
                .build();
    }

    @Bean(name = "erpTransactionManager")
    public PlatformTransactionManager erpTransactionManager(
            @Qualifier("erpEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    // WEB DB JPA 속성 설정
    private java.util.Map<String, Object> webJpaProperties() {
        java.util.Map<String, Object> props = new java.util.HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");  // Web DB는 자유롭게 수정 가능
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return props;
    }

    // ERP DB JPA 속성 설정 (안전하게)
    private java.util.Map<String, Object> erpJpaProperties() {
        java.util.Map<String, Object> props = new java.util.HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "validate");  // ERP DB는 검증만, 변경 안함
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return props;
    }
}

// ============= Repository 설정 =============
@Configuration
@EnableJpaRepositories(
        basePackages = "com.pipebank.ordersystem.domain.web",
        entityManagerFactoryRef = "webEntityManagerFactory",
        transactionManagerRef = "webTransactionManager"
)
class WebRepositoryConfig {}

@Configuration
@EnableJpaRepositories(
        basePackages = "com.pipebank.ordersystem.domain.erp", 
        entityManagerFactoryRef = "erpEntityManagerFactory",
        transactionManagerRef = "erpTransactionManager"
)
class ErpRepositoryConfig {} 
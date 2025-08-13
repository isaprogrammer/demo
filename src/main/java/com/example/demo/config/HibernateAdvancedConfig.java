package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Hibernate高级配置类
 */
@Configuration
@EnableCaching
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.demo.repository")
public class HibernateAdvancedConfig {
    
    /**
     * 配置EntityManagerFactory with Hibernate高级特性
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.demo.entity");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        
        return em;
    }
    
    /**
     * Hibernate高级属性配置
     */
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        
        // 基本配置
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        
        // 二级缓存配置 - 暂时禁用以确保应用启动
        properties.setProperty("hibernate.cache.use_second_level_cache", "false");
        properties.setProperty("hibernate.cache.use_query_cache", "false");
        
        // 批处理配置
        properties.setProperty("hibernate.jdbc.batch_size", "20");
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        
        // 连接池配置
        properties.setProperty("hibernate.connection.pool_size", "10");
        
        // 统计信息
        properties.setProperty("hibernate.generate_statistics", "true");
        
        // 懒加载配置
        properties.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        
        // SQL注释
        properties.setProperty("hibernate.use_sql_comments", "true");
        
        // 自动刷新模式
        properties.setProperty("hibernate.flushMode", "AUTO");
        
        // 命名策略
        properties.setProperty("hibernate.physical_naming_strategy", 
            "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        
        return properties;
    }
    
    /**
     * 事务管理器配置
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
    
    /**
     * 缓存管理器配置
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "users", "roles", "permissions", "userRoles", "products", "orders"
        ));
        return cacheManager;
    }
}
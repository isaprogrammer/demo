package com.example.demo.other;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author liweier
 */
@Slf4j
@Configuration
@MapperScan(value = "com.example.demo.other", annotationClass = Mapper.class, sqlSessionFactoryRef = "sqlSessionFactory")
public class H2DataSourceConfig {

    @Value("${db.first.driverClassName:org.h2.Driver}")
    private volatile String driverClassName;

    public static String H2_DB_NAME = "demo";

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("使用手动方式配置 DruidDataSource，不依赖 DataSourceProperties");
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setKeepAlive(true);
        druidDataSource.setUrl("jdbc:h2:file:" + "~/" + H2_DB_NAME + ";MODE=MYSQL;DATABASE_TO_UPPER=FALSE;AUTO_RECONNECT=TRUE;AUTO_SERVER=TRUE;");
        druidDataSource.setDriverClassName(driverClassName);
        druidDataSource.setUsername("sa");     // ✅ H2 默认用户名
        druidDataSource.setPassword("");       // ✅ H2 默认无密码
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxActive(10);
        return druidDataSource;
    }

    @Bean
    @Primary
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        log.info("指定事务管理器：" + dataSourceTransactionManager.getClass().getName());

        return dataSourceTransactionManager;
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        log.info("如果使用了MybatisPlus的BaseMapper，就需要将SqlSessionFactoryBean替换为MybatisSqlSessionFactoryBean");
        bean.setDataSource(dataSource);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        return getSqlSessionFactory(bean, resolver);
    }

    private SqlSessionFactory getSqlSessionFactory(SqlSessionFactoryBean bean, ResourcePatternResolver resolver) {
        final String mapperPath = "classpath*:mapper/**/*.xml";
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            log.info("此处可以为Mybatis注册更多的TypeHandler，例如：configuration.getTypeHandlerRegistry().register(JSONArray.class, FastjsonListTypeHandler.class);configuration.getTypeHandlerRegistry().register(JSONObject.class, FastjsonTypeHandler.class);");
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setUseColumnLabel(true);
            log.info("H2定义mapper文件存储的路径为：" + mapperPath);
            bean.setMapperLocations(resolver.getResources(mapperPath));
            bean.setConfiguration(configuration);

            return bean.getObject();
        } catch (Exception ex) {
            log.error("获取SqlSessionFactory时出现异常：", ex);
            throw new RuntimeException(ex);
        }
    }

}
package com.yalonglee.jmeter.admin.datasources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @author yalonglee
 */
@Slf4j
@Order(-1)
@Component
public class InitDatabase implements ApplicationRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        DataSourceInitializer dsi = null;
        try {
            dsi = new DataSourceInitializer();
            dsi.setDataSource(dataSource);
            dsi.setEnabled(true);
            dsi.setDatabasePopulator(new ResourceDatabasePopulator(false, false, "utf-8", new ClassPathResource("schema.sql")));
            dsi.afterPropertiesSet();
        } catch (Exception ex) {
            log.error("init database failed", ex);
            throw ex;
        } finally {
            if (null != dsi) {
                dsi.destroy();
            }
        }
    }
}
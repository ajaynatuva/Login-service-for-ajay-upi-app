package com.amps.user.configuration;


import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
public class JpaConfiguration {

    @Autowired
    private Environment environment;
    Logger logger = LogManager.getLogger(JpaConfiguration.class);


    @Bean
    @LiquibaseDataSource
    public DataSource ipuDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://" + environment.getProperty("ipu.database.host") + ":"
                + environment.getProperty("ipu.database.port") + "/" + environment.getProperty("ipu.database") + "?ApplicationName=IPU USER");
        dataSource.setUsername(environment.getProperty("ipu.database.user"));
        dataSource.setPassword(environment.getProperty("ipu.database.password"));
        logger.info(" ipu database url is " + dataSource.getJdbcUrl());
        logger.info(" ipu database user name is " + dataSource.getUsername());
        logger.info(" ipu db password is " + dataSource.getPassword());
        return dataSource;
    }
}

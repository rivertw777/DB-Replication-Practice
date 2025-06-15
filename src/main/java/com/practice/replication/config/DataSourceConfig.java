package com.practice.replication.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@Configuration
public class DataSourceConfig {

    private final DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource routingDataSource() {
        Map<Object, Object> dataSourceMap = new HashMap<>();

        DataSourceProperties.Master masterProperty = dataSourceProperties.getMaster();
        DataSource masterDataSource = createDataSource(
                masterProperty.getJdbcUrl(),
                masterProperty.getUsername(),
                masterProperty.getPassword(),
                masterProperty.getDriverClassName()
        );
        dataSourceMap.put("master", masterDataSource);

        dataSourceProperties.getSlaves()
                .forEach((key, value) -> dataSourceMap.put(value.getName(), createDataSource(
                        value.getJdbcUrl(),
                        value.getUsername(),
                        value.getPassword(),
                        value.getDriverClassName()
                )));

        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        return routingDataSource;
    }

    public DataSource createDataSource(String url, String username, String password, String driverClassName) {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}

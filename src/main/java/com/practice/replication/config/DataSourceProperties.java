package com.practice.replication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.datasource")
@Configuration
public class DataSourceProperties {

    private Master master = new Master();
    private final Map<String, Slave> slaves = new HashMap<>();

    @Getter
    @Setter
    public static class Master {
        private String jdbcUrl;
        private String username;
        private String password;
        private String driverClassName;
    }

    @Getter
    @Setter
    public static class Slave {
        private String name;
        private String jdbcUrl;
        private String username;
        private String password;
        private String driverClassName;
    }

}

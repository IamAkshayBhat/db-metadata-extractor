package com.example.dbmetadata.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "mysql.datasource")
public class MySQLDataSourceProperties {
    private String url;
    private String driverClassName;
    private String username;
    private String password;
}

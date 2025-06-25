package com.example.dbmetadata.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "h2.datasource")
public class H2DataSourceProperties {
	private String url;
	private String driverClassName;
	private String username;
	private String password;
}

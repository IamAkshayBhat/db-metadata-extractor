package com.example.dbmetadata.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.dbmetadata.model.H2DataSourceProperties;

@Configuration
public class H2DatabaseConfig {

	@Primary
	@Bean(name = "h2Datasource")
	public DataSource h2DataSource(H2DataSourceProperties props) {
		return DataSourceBuilder.create().driverClassName(props.getDriverClassName()).url(props.getUrl())
				.username(props.getUsername()).password(props.getPassword()).build();
	}
}

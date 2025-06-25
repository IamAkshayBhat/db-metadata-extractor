package com.example.dbmetadata.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.dbmetadata.model.MySQLDataSourceProperties;

@Configuration
public class MySQLDatabaseConfig {

	@Bean(name = "mysqlDataSource")
	@ConfigurationProperties(prefix = "mysql.datasource")
	public DataSource mysqlDataSource(MySQLDataSourceProperties props) {
		return DataSourceBuilder.create().driverClassName(props.getDriverClassName()).url(props.getUrl())
				.username(props.getUsername()).password(props.getPassword()).build();
	}
}

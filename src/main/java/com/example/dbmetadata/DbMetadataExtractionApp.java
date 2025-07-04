package com.example.dbmetadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ConfigurationPropertiesScan
public class DbMetadataExtractionApp {

	public static void main(String[] args) {
		SpringApplication.run(DbMetadataExtractionApp.class, args);
	}

}

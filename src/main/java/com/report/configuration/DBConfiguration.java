package com.report.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

@Configuration
public class DBConfiguration {

	@Primary
	@Bean
	@Qualifier("firstDB")
	@ConfigurationProperties(prefix = "spring.datasource.first")
	public DataSource firstDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "firstJdbcTemplate")
	public JdbcTemplate firstJdbcTemplate(@Qualifier("firstDB") DataSource firstDB) {
		return new JdbcTemplate(firstDB);
	}

	@Bean
	@Qualifier("secondDB")
	@ConfigurationProperties(prefix = "spring.datasource.second")
	public DataSource secondDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "secondJdbcTemplate")
	public JdbcTemplate secondJdbcTemplate(@Qualifier("secondDB") DataSource secondDB) {
		return new JdbcTemplate(secondDB);
	}

	@Bean
	@Qualifier("thirdDB")
	@ConfigurationProperties(prefix = "spring.datasource.third")
	public DataSource thirdDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "thirdJdbcTemplate")
	public JdbcTemplate thirdJdbcTemplate(@Qualifier("thirdDB") DataSource thirdDB) {
		return new JdbcTemplate(thirdDB);
	}

}

package com.report.repository;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepository {
	@Autowired 
    @Qualifier("firstJdbcTemplate") 
    protected JdbcTemplate firstJdbcTemplate;

	
	@Autowired 
    @Qualifier("secondJdbcTemplate") 
    protected JdbcTemplate secondJdbcTemplate;
	
	@Autowired 
    @Qualifier("thirdJdbcTemplate") 
    protected JdbcTemplate thirdJdbcTemplate;
	
	private Map<String, JdbcTemplate> jdbcTemplates;
	
	@PostConstruct
	private void initJdbcTemplates() {
		jdbcTemplates = new HashMap<>();
		jdbcTemplates.put("FIRST", firstJdbcTemplate);
		jdbcTemplates.put("SECOND", secondJdbcTemplate);
		jdbcTemplates.put("THIRD", thirdJdbcTemplate);
	}
	
	public JdbcTemplate getJdbcTemplate(String source) {
		return jdbcTemplates.get(source);
	}
}

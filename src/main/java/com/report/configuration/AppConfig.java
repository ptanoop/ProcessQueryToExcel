package com.report.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class AppConfig {
	
	@Value("${report.application.path}")
	private String applicationPath;
	
	private final static Logger logger = LoggerFactory.getLogger(AppConfig.class);
	
	public AppConfig() {
		logger.info("Loading configuration files");
		logger.info(applicationPath);
	}
	
}

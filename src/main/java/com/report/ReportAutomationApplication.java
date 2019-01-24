package com.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableScheduling
public class ReportAutomationApplication {

	private final static Logger logger = LoggerFactory.getLogger(ReportAutomationApplication.class);

	public static void main(String[] args) {
		logger.info("ReportAutomationApplication starts");
		SpringApplication.run(ReportAutomationApplication.class, args);
	}
}

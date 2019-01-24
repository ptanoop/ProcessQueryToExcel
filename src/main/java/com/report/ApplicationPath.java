package com.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationPath {
	
	public static String APPLICATION_PATH;
	public static String APPLICATION_PROPERTIES_PATH;
	public static String REPORT_CONFIGURATIONS_PATH;
	public static String REPORT_TEMPLATES_PATH;
	public static String REPORT_OUTPUT_PATH;
	public static String REPORT_SHEET_XML_CONFIGURATION_PATH;
	private final static Logger logger = LoggerFactory.getLogger(ApplicationPath.class);
	
	ApplicationPath(){
		APPLICATION_PATH 					= System.getProperty("app.home");
		APPLICATION_PROPERTIES_PATH 		= APPLICATION_PATH + "/PROPERTIES_FILES";
		REPORT_CONFIGURATIONS_PATH 			= APPLICATION_PATH + "/PROPERTIES_FILES/REPORT_CONF";
		REPORT_TEMPLATES_PATH 				= APPLICATION_PATH + "/REPORT_EXCEL_FILES/TEMPLATES";
		REPORT_OUTPUT_PATH 					= APPLICATION_PATH + "/REPORT_EXCEL_FILES";
		REPORT_SHEET_XML_CONFIGURATION_PATH = APPLICATION_PATH + "/REPORT_XML_CONF_FILES";
		
		logger.info("APPLICATION_PATH = "+APPLICATION_PATH);
		logger.info("APPLICATION_PROPERTIES_PATH = "+APPLICATION_PROPERTIES_PATH);
		logger.info("REPORT_CONFIGURATIONS_PATH = "+REPORT_CONFIGURATIONS_PATH);
		logger.info("REPORT_TEMPLATES_PATH = "+REPORT_TEMPLATES_PATH);
		logger.info("REPORT_OUTPUT_PATH = "+REPORT_OUTPUT_PATH);
		logger.info("REPORT_SHEET_XML_CONFIGURATION_PATH = "+REPORT_SHEET_XML_CONFIGURATION_PATH);
	}
	
}

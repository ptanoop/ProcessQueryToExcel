package com.report.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.report.constants.ReportConstants;

@Component
public class QueryServiceFactory {

	@Autowired
	private DatabaseQueryService databaseQueryService;
	@Autowired
	private SheetQueryService sheetQueryService;
	
	private Map<String, IQueryService> queryServices;
	
	@PostConstruct
	private void factoryInit() {
		queryServices = new HashMap<>();
		queryServices.put(ReportConstants.QUERY_SOURCE_DATABASE, databaseQueryService);
		queryServices.put(ReportConstants.QUERY_SOURCE_SHEET, sheetQueryService);
	}
	
	public IQueryService getQueryServices(String service) {
		return queryServices.get(service);
	}
	
}

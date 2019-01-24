package com.report.service;

import org.apache.poi.ss.usermodel.Cell;

import com.report.constants.ReportConstants;

public interface IQueryService {
	public ReportConstants.QUERY_EXECUTION_STATUS executeQuery(Cell cell, String source, String query, String resultType);
}

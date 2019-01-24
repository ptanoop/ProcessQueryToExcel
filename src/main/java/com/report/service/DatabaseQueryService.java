package com.report.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import com.report.constants.ReportConstants;
import com.report.constants.ReportConstants.QUERY_EXECUTION_STATUS;
import com.report.repository.ReportRepository;
import com.report.util.ReportUtils;

@Service
public class DatabaseQueryService implements IQueryService{
	
	@Autowired
	private ReportRepository reportRepository;
	@Autowired
	private ReportUtils reportUtils;
	private final Logger logger = LoggerFactory.getLogger(DatabaseQueryService.class);

	@Override
	public ReportConstants.QUERY_EXECUTION_STATUS executeQuery(Cell cell, String source, String query, String resultType) {
		logger.info("Query execute starts");
		JdbcTemplate jdbcTemplate = null;
		QUERY_EXECUTION_STATUS resultStatus = QUERY_EXECUTION_STATUS.UNKNOWN;
		switch(resultType) {
			case ReportConstants.QUERY_RESULT_TYPE_NUMBER:
				if(!source.isEmpty() && !query.isEmpty()) {
					try {
						jdbcTemplate = reportRepository.getJdbcTemplate(source);
						Double result = jdbcTemplate.queryForObject(query,  Double.class);
						logger.info("Query Result = "+result);
						if(result!=null)
							cell.setCellValue(result);
						else
							cell.setCellValue(0);
						//jdbcTemplate.getDataSource().getConnection().close();
						DataSourceUtils.releaseConnection(jdbcTemplate.getDataSource().getConnection(), jdbcTemplate.getDataSource());
						resultStatus = QUERY_EXECUTION_STATUS.SUCCESS;
					}
					catch(Exception e) {
						logger.error("Query execution failure", e);
						cell.setCellValue(0);
						resultStatus = QUERY_EXECUTION_STATUS.FAILURE;
					}
				}
				else {
					cell.setCellValue(0);
					resultStatus = QUERY_EXECUTION_STATUS.UNKNOWN;
				}
				break;
			case ReportConstants.QUERY_RESULT_TYPE_STRING:
				if(!source.isEmpty() && !query.isEmpty()) {
					try {
						jdbcTemplate = reportRepository.getJdbcTemplate(source);
						String resStr = jdbcTemplate.queryForObject(query, String.class);
						logger.info("Query Result = "+resStr);
						cell.setCellValue(resStr);
						//jdbcTemplate.getDataSource().getConnection().close();
						DataSourceUtils.releaseConnection(jdbcTemplate.getDataSource().getConnection(), jdbcTemplate.getDataSource());
						resultStatus = QUERY_EXECUTION_STATUS.SUCCESS;
					}
					catch(Exception e) {
						logger.error("Query execution failure", e);
						cell.setCellValue("");
						resultStatus = QUERY_EXECUTION_STATUS.FAILURE;
					}
				}
				else {
					logger.info("Both empty");
					cell.setCellValue("");
					resultStatus = QUERY_EXECUTION_STATUS.UNKNOWN;
				}
				break;	
			case ReportConstants.QUERY_RESULT_TYPE_DATE:
				if(!source.isEmpty() && !query.isEmpty()) {
					try {
						jdbcTemplate = reportRepository.getJdbcTemplate(source);
						Date resDate = jdbcTemplate.queryForObject(query, Date.class);
						logger.info("Query Result = "+reportUtils.formatDate(resDate));
						cell.setCellValue(reportUtils.formatDate(resDate));
						//jdbcTemplate.getDataSource().getConnection().close();
						DataSourceUtils.releaseConnection(jdbcTemplate.getDataSource().getConnection(), jdbcTemplate.getDataSource());
						resultStatus = QUERY_EXECUTION_STATUS.SUCCESS;
					}
					catch(Exception e) {
						logger.error("Query execution failure", e);
						cell.setCellValue(0);
						resultStatus = QUERY_EXECUTION_STATUS.FAILURE;
					}
				}
				else {
					cell.setCellValue(0);
					resultStatus = QUERY_EXECUTION_STATUS.UNKNOWN;
				}
				break;		
		}
		logger.info("Query execute completed");
		return resultStatus;
	}
	
	
	public List<Map<String, Object>> executeMultiResultQuery(String source, String query) {
		logger.info("MultiResultQuery execute starts");
		List<Map<String, Object>> list = new ArrayList<>();
		JdbcTemplate jdbcTemplate = null;
		if(!source.isEmpty() && !query.isEmpty()) {
			try {
				jdbcTemplate = reportRepository.getJdbcTemplate(source);
				list = jdbcTemplate.queryForList(query);
				logger.info("Query Result size = "+ list.size());
				DataSourceUtils.releaseConnection(jdbcTemplate.getDataSource().getConnection(), jdbcTemplate.getDataSource());
			}
			catch(Exception e) {
				logger.error("Query execution failure", e);
				list = null;
			}
		}
		else {
			logger.error("source or query is null");
		}
		return list;
	}
	
	
}

package com.report.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.report.ApplicationPath;
import com.report.constants.ReportConstants;
import com.report.constants.ReportConstants.QUERY_EXECUTION_STATUS;
import com.report.model.ReportConfig;
import com.report.model.ReportQuery;
import com.report.model.ReportSheetConfig;
import com.report.model.FailureQueryModel;
import com.report.model.MultiResultQuery;
import com.report.model.SingleResultQuery;
import com.report.util.ReportUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProcessReportService {
	
	private final static Logger logger = LoggerFactory.getLogger(ProcessReportService.class);
	@Autowired
	private ReportUtils reportUtils;
	@Autowired
	private QueryServiceFactory queryServiceFactory;
	
	
	private List<ReportConstants.REPORT> reportCurrentlyProcessing;
	private Map<ReportConstants.REPORT, Boolean> reportSchedulerFinishStatusMap;
	private List<FailureQueryModel> failureQueryList;
	
	@PostConstruct
	public void initService() {
		reportCurrentlyProcessing = new ArrayList<ReportConstants.REPORT>();
		reportSchedulerFinishStatusMap = new HashMap<>();
		resetSchedulerFinishStatusMap();
		resetFailureQueryList();
	}
	
	public void resetSchedulerFinishStatusMap() {
		Arrays.asList(ReportConstants.REPORT.values()).forEach( report -> {
			reportSchedulerFinishStatusMap.put(report, false);
		});
	}
	
	public void resetFailureQueryList() {
		logger.info("Reset Failure Query List");
		if(failureQueryList!=null)
			logger.info("Failure Query List Size = "+failureQueryList.size());
		failureQueryList = new ArrayList<FailureQueryModel>();
	}
	
	public ReportQuery createReportQueryFromXML(String xmlFilePath) {
		try {
			File reportFile = new File(xmlFilePath);
			logger.info("Reading Report XML File : "+xmlFilePath);
			JAXBContext jaxbContext = JAXBContext.newInstance(ReportQuery.class);
		    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		    logger.info("Creating object from xml file");
		    return (ReportQuery) jaxbUnmarshaller.unmarshal(reportFile);
		}
		catch(Exception e) {
			logger.debug("Create Report Object Failed", e);
			throw new RuntimeException(e);
		}
	}
	
	public void shiftColumnsInExcel(XSSFWorkbook workbook, ReportSheetConfig reportSheet) {
		String startCell 	= reportSheet.getStartCell();
		String endCell 		= reportSheet.getEndCell();
		String sheetName 	= reportSheet.getSheetName();
		if(startCell != "" && endCell != "") {
			int startRow 		= reportUtils.getExcelRowValue(startCell);
			int startColumn 	= reportUtils.getExcelColumnValue(startCell);
			int endRow   		= reportUtils.getExcelRowValue(endCell);
			int endColumn 		= reportUtils.getExcelColumnValue(endCell);
			int shiftNum 		= reportSheet.getColumnShift();
	        logger.info("Working on sheet name : "+sheetName);
	        logger.info("Start Cell = "+startCell+ " Start Row = "+startRow);
	        logger.info("Start Cell = "+startCell+ " Start Row = "+startColumn);
	        logger.info("End Cell = "+endCell+ " End Row = "+endRow);
	        logger.info("End Cell = "+endCell+ " End Column = "+endColumn);
	        Sheet datatypeSheet = (Sheet) workbook.getSheet(sheetName);
	        for(int i = startRow; i <= endRow; i++) {
	        	for(int j = startColumn; j <= endColumn - shiftNum; j++) {
	        		Cell currentCell = datatypeSheet.getRow(i).getCell(j);
	        		Cell nextCell = datatypeSheet.getRow(i).getCell(j + shiftNum);
	        		if(nextCell!=null) {
	        			switch(nextCell.getCellType()) {
		            		case NUMERIC:
		            			if(HSSFDateUtil.isCellDateFormatted(nextCell)) {
		            				currentCell.setCellValue(nextCell.getDateCellValue());
		            			}
		            			else {
		            				currentCell.setCellValue(nextCell.getNumericCellValue());
		            			}
		            			break;
		            		case STRING:
		            			currentCell.setCellValue(nextCell.getStringCellValue());
		            			break;
		            		case FORMULA:
		            			//currentCell.setCellFormula(nextCell.getCellFormula());
		            			break;
		            		case BLANK:
		            			currentCell.setCellType(CellType.BLANK);
		            			String nullString = null;
		            			currentCell.setCellValue(nullString);
		            			break;
		            		case BOOLEAN:
		            			currentCell.setCellValue(nextCell.getBooleanCellValue());
		            			break;
		            		case ERROR:
		            			currentCell.setCellErrorValue(nextCell.getErrorCellValue());
		            			break;
							case _NONE:
								break;
							default:
								break;
	            		}
	        		}
	        	}
	        }
	        for(int i = startRow; i <= endRow; i++) {
	        	for(int j = endColumn; j > endColumn - shiftNum; j--) {
	        		Cell currentCell = datatypeSheet.getRow(i).getCell(j);
	        		//logger.debug("Reset Report Cell on Row : ("+i+", Column : "+j+ ") = ");
	        		reportUtils.resetReportCell(currentCell);
	        	}
	        }
	        logger.info("evaluates all formula on sheet");
	        try {
	        	XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
	        }
	        catch(Exception e) {
	        	logger.debug("Formula Exception ", e);
	        }
		}
	}
	
	public void processSingleResultQuery (ReportConstants.REPORT report,
			 List<SingleResultQuery> singleResultQueryList,
			 Sheet reportSheet,
			 String formatedDateStr,
			 boolean isInvokedByScheduler) {
		
		if(singleResultQueryList!=null) {
			singleResultQueryList.forEach((singleResultQuery) -> {
				
				singleResultQuery.insertDateParameter(formatedDateStr);
				String cell 		= singleResultQuery.getCell();
				int row 			= reportUtils.getExcelRowValue(cell);
				int col 			= reportUtils.getExcelColumnValue(cell);
				String sourceType   = singleResultQuery.getSourceType();
				String source 		= singleResultQuery.getSource();
				String action 		= singleResultQuery.getAction();
				String query		= singleResultQuery.getQuery();
				String resultType 	= singleResultQuery.getResultType();
				String description 	= singleResultQuery.getDescription();
				
				Cell currentCell	= reportSheet.getRow(row).getCell(col);
				
				logger.info(" SingleResultQuery ");
				logger.info("Row : "+row+", Column : "+col+", SourceType : "+sourceType+", Source : "+source+", Action : "+action+", Query : "+query+", ResultType : "+resultType+", Description : "+description);
				logger.info(" SingleResultQuery ");
				
				if(sourceType.compareTo(ReportConstants.QUERY_SOURCE_SHEET)==0 && !source.isEmpty()) {
					logger.info(reportUtils.getReportPathOnADay(formatedDateStr));
					logger.info(reportUtils.getDayStringAppendReportFile(formatedDateStr));
					logger.info(ApplicationPath.REPORT_OUTPUT_PATH);
					source = 	ApplicationPath.REPORT_OUTPUT_PATH + "/" + 
								reportUtils.getReportPathOnADay(formatedDateStr) + "/" +
								source + "_"+ reportUtils.getDayStringAppendReportFile(formatedDateStr) + ".xlsx";
					logger.info("Source = "+ source);
				}
				singleResultQuery.setSource(source);
				switch(action) {
					case "FILL_DATE":
						Date fdate = reportUtils.parseDate(formatedDateStr);
						currentCell.setCellValue(fdate);
						break;
					case "FILL_VALUE":
						IQueryService queryService = queryServiceFactory.getQueryServices(sourceType);
						if(queryService!=null) {
							QUERY_EXECUTION_STATUS resultStatus = queryService.executeQuery(currentCell, source, query, resultType);
							logger.info(resultStatus.name());
							logger.info(Boolean.toString((resultStatus==QUERY_EXECUTION_STATUS.FAILURE)));
							logger.info(Boolean.toString(isInvokedByScheduler));
							if(isInvokedByScheduler && resultStatus==QUERY_EXECUTION_STATUS.FAILURE) {
								logger.info("Keeping failure single result queries");
								logger.info(singleResultQuery.toString());
								failureQueryList.add(new FailureQueryModel(report, reportSheet.getSheetName(), singleResultQuery));
							}
						}
						else
							logger.debug("Not implemented");
						break;
					case "FILL_DESCRIPTION":
						logger.info("FILLING DESCRIPTION : "+description);
						currentCell.setCellValue(description);
						break;
					case "SUM":
					
						break;
					case "PERCENTAGE":
					
						break;
					case "AVERAGE":
					
						break;
				}
			});
		}
	}
	
	public void processMultiResultQuery(ReportConstants.REPORT report,
										List<MultiResultQuery>	multiResultQueryList,
										Sheet 					reportSheet,
										String 					formatedDateStr,
										boolean 				isInvokedByScheduler) {
		if(multiResultQueryList!=null) {
			multiResultQueryList.forEach((multiResultQuery) -> {
				multiResultQuery.insertDateParameter(formatedDateStr);
				int startRow 	= reportUtils.getExcelRowValue(multiResultQuery.getStartCell());
				int startColumn = reportUtils.getExcelColumnValue(multiResultQuery.getStartCell());
				String source 	= multiResultQuery.getSource();
				String query 	= multiResultQuery.getQuery();
				String queryResultColumnNames = multiResultQuery.getQueryResultColumnNames();
				String queryResultColumnTypes = multiResultQuery.getQueryResultColumnTypes();
				
				logger.info("MultiQuery start row = "+startRow);
				logger.info("MultiQuery start col = "+startColumn);
				logger.info("MultiQuery source = "+source);
				logger.info("MultiQuery query = "+query);
				logger.info("MultiQuery queryResultColumnNames = "+queryResultColumnNames);
				logger.info("MultiQuery queryResultColumnTypes = "+queryResultColumnTypes);
				
				if(source != null && query != null && queryResultColumnNames != null && queryResultColumnTypes != null) {
					String colNameList[] 		= queryResultColumnNames.split(",");
					String colTypeList[] 		= queryResultColumnTypes.split(",");
					DatabaseQueryService 		dbQueryService = (DatabaseQueryService) queryServiceFactory.getQueryServices(ReportConstants.QUERY_SOURCE_DATABASE);
					List<Map<String, Object>> 	queryResult = dbQueryService.executeMultiResultQuery(source, query);
					if(queryResult!=null) {
						logger.info("Query result size = "+queryResult.size());
						for(int resIndex = 0 ; resIndex < queryResult.size(); resIndex++) {
							Map<String, Object> eachResultRow = queryResult.get(resIndex);
							for(int i = 0; i < colNameList.length; i++) {
								Cell currentCell = reportSheet.getRow(startRow + resIndex).getCell(startColumn + i);
								eachResultRow.get(colNameList[i]);
								double numberResult = 0.0;
								String stringResult = "";
								switch(colTypeList[i]) {
									case ReportConstants.QUERY_RESULT_TYPE_NUMBER:
										numberResult = ((BigDecimal)eachResultRow.get(colNameList[i])).doubleValue();
										logger.info("Number result = "+numberResult);
										currentCell.setCellValue(numberResult);
										break;
									case ReportConstants.QUERY_RESULT_TYPE_STRING:
										stringResult = (String)eachResultRow.get(colNameList[i]);
										logger.info("String result = "+stringResult);
										currentCell.setCellValue(stringResult);
										break;
									case ReportConstants.QUERY_RESULT_TYPE_DATE:
								
										break;
								}
							}
						}
					}
					else {
						if(isInvokedByScheduler) {
							logger.info("Keeping failure multi result queries");
							logger.info(multiResultQuery.toString());
							failureQueryList.add(new FailureQueryModel(report, reportSheet.getSheetName(), multiResultQuery));
						}
					}
				}
			});
		}
		else {
			logger.info("No multi result queries");
		}
		
	}
	
	public ReportConfig reportConfiguration(ReportConstants.REPORT report) {
		ReportConfig bcReportConfig = null;
		try {
			logger.info("Report Configuration File exists");
			ObjectMapper reportObjectMapper = new ObjectMapper();
			String reportPath = ApplicationPath.REPORT_CONFIGURATIONS_PATH+"/" + report.getReportFileName();
			logger.info("Report Configuration File Path = "+reportPath);
			File reportConfigurationFile = new File(reportPath);
			if(reportConfigurationFile.exists())
				bcReportConfig = reportObjectMapper.readValue(reportConfigurationFile, ReportConfig.class);
			else
				logger.info("Report configuration file doesn't find.");
		}
		catch(Exception e) {
			logger.info("Exception = "+e.getMessage());
			e.printStackTrace();
		}
		return bcReportConfig;
	}
	
	public void processReport(ReportConstants.REPORT report, String formatedDateStr, boolean isInvokedByScheduler) {
		logger.info("Processing Report = "+report.name());
		logger.info("Report Configuration File = "+report.getReportFileName());
		ReportConfig reportConfigObj = reportConfiguration(report);
		String xmlConfPath = ApplicationPath.REPORT_SHEET_XML_CONFIGURATION_PATH;
		
		if(!reportCurrentlyProcessing.contains(report) || isInvokedByScheduler) {
			
			if(reportConfigObj != null) {
				try {
					if(!isInvokedByScheduler)
						reportCurrentlyProcessing.add(report);
					
					ReportSheetConfig reportSheets[] = reportConfigObj.getReportSheets();
					String workFilePath = reportUtils.getReportFileToWorkOn(reportConfigObj.getReportName(), formatedDateStr);
					XSSFWorkbook reportWorkbook = new XSSFWorkbook(new FileInputStream(new File(workFilePath)));
					logger.info("Process Report Sheets");
					
					for(int i = 0; i < reportSheets.length; i++) {
						shiftColumnsInExcel(reportWorkbook, reportSheets[i]);
						Sheet reportWorkSheet = (Sheet) reportWorkbook.getSheet(reportSheets[i].getSheetName());
						logger.info("Processing single result queries");
						XSSFFormulaEvaluator xssfFormulaEval = new XSSFFormulaEvaluator(reportWorkbook);
						xssfFormulaEval.clearAllCachedResultValues();
						xssfFormulaEval.evaluateAll();
						ReportQuery reportQueryObj = createReportQueryFromXML(xmlConfPath+"/"+reportSheets[i].getReportQueries());
						processMultiResultQuery(report, reportQueryObj.getMultiResultQueries(), reportWorkSheet, formatedDateStr, isInvokedByScheduler);
						processSingleResultQuery(report, reportQueryObj.getSingleResultQueries(), reportWorkSheet, formatedDateStr, isInvokedByScheduler);
					}
					try {
						logger.info("Evaluating formulas in workbook");
						XSSFFormulaEvaluator.evaluateAllFormulaCells(reportWorkbook);
					}
					catch(Exception e) {
			        	logger.debug("Formula Exception ", e);
			        }
					logger.info("Writing values to file");
					reportWorkbook.setForceFormulaRecalculation(true);
					File reportOutput = reportUtils.getOutputReportFile(reportConfigObj.getReportName(), formatedDateStr);
					reportOutput.getParentFile().mkdirs();
					reportOutput.createNewFile();
					FileOutputStream fileOut = new FileOutputStream(reportOutput);
					reportWorkbook.write(fileOut);
			        fileOut.close();
			        reportWorkbook.close();
			        if(isInvokedByScheduler)
			        	reportSchedulerFinishStatusMap.put(report, true);
			        logger.info("Processing Report Sheets Finished");
			        if(!isInvokedByScheduler)
			        	reportCurrentlyProcessing.remove(report);
			        
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(!isInvokedByScheduler)
						reportCurrentlyProcessing.remove(report);
					
				}
			}
			else {
				logger.info("Report configuration creation failed");
			}
		}
		else {
			logger.info(report.getReportFileName()+" is already processing, please wait");
		}
		
		
	}

	public ArrayList<ReportQuery> getReportQueryObjects(ReportConstants.REPORT report) {
		ArrayList<ReportQuery> borderControlReportObjects = new ArrayList<ReportQuery>();
		String reportPath = ApplicationPath.REPORT_CONFIGURATIONS_PATH+"/" + report.getReportFileName();
		String xmlConfPath = ApplicationPath.REPORT_SHEET_XML_CONFIGURATION_PATH;
		logger.info("Report Configuration File Path = "+reportPath);
		File reportConfigurationFile = new File(reportPath);
		try {
			if(reportConfigurationFile.exists()) {
				ObjectMapper reportObjectMapper = new ObjectMapper();
				logger.info("Report JSON Configuration to Object Mapping");
				ReportConfig reportConfigObj = reportObjectMapper.readValue(reportConfigurationFile, ReportConfig.class);
				ReportSheetConfig reportSheets[] = reportConfigObj.getReportSheets();
				for(ReportSheetConfig reportSheet:reportSheets) {
					ReportQuery ngbExitDailyReportObj = createReportQueryFromXML(xmlConfPath+"/"+reportSheet.getReportQueries());
					borderControlReportObjects.add(ngbExitDailyReportObj);
				}
			}
		}
		catch(Exception e) {
			logger.info("Exception = "+e.getMessage());
			e.printStackTrace();
		}
		
		
		return borderControlReportObjects;
	}
	
	public boolean getReportSchedulerFinishStatus(ReportConstants.REPORT report) {
		return reportSchedulerFinishStatusMap.get(report);
	}
	
    public void processFailureQueries(String formatedDateStr) {
    	logger.info("Executing failure queries, List size = "+failureQueryList.size());
    	for(int f = 0; f < failureQueryList.size(); f++) {
    		
    		FailureQueryModel fQuery = failureQueryList.get(f);
    		ReportConstants.REPORT report = fQuery.getReport();
    		
    		if(getReportSchedulerFinishStatus(report)) {
    			try {
	    			logger.info("Processing Failures on Report = "+report.name());
	    			logger.info("Report Configuration File = "+report.getReportFileName());
	    			ReportConfig reportConfigObj = reportConfiguration(report);
	    			
	    			String reportPathOnDay = reportUtils.getReportPathOnADay(formatedDateStr);
	    			String appendDateStr = reportUtils.getDayStringAppendReportFile(formatedDateStr);
	    			String reportDayFilePath = ApplicationPath.REPORT_OUTPUT_PATH + "/" +reportPathOnDay + "/"+reportConfigObj.getReportName() + "_" + appendDateStr+".xlsx";
	    			logger.info(reportDayFilePath); 
	    			
					XSSFWorkbook reportWorkbook = new XSSFWorkbook(new FileInputStream(new File(reportDayFilePath)));
					
	    			String sheetName = fQuery.getSheetName();
	    			SingleResultQuery sQuery = fQuery.getSingleResQuery();
	    			MultiResultQuery mQuery = fQuery.getMulResQuery();
	    			
	    			Sheet reportSheet = reportWorkbook.getSheet(sheetName);
	    			
	    			if(sQuery!=null) {
	    				String cell 		= sQuery.getCell();
	    				int row 			= reportUtils.getExcelRowValue(cell);
	    				int col 			= reportUtils.getExcelColumnValue(cell);
	    				String sourceType   = sQuery.getSourceType();
	    				String source 		= sQuery.getSource();
	    				String action 		= sQuery.getAction();
	    				String query		= sQuery.getQuery();
	    				String resultType 	= sQuery.getResultType();
	    				logger.info("Failure Query Execution - SingleResultQuery");
	    				Cell currentCell	= reportSheet.getRow(row).getCell(col);
	    				if(action.compareTo("FILL_VALUE")==0) {
	    					IQueryService queryService = queryServiceFactory.getQueryServices(sourceType);
							if(queryService!=null) {
								QUERY_EXECUTION_STATUS resultStatus = queryService.executeQuery(currentCell, source, query, resultType);
								logger.info(resultStatus.name());
								if(resultStatus==QUERY_EXECUTION_STATUS.SUCCESS) {
									failureQueryList.remove(f);
								}
								
							}
	    				}
	    			}
	    			if(mQuery!=null) {
	    				int startRow 	= reportUtils.getExcelRowValue(mQuery.getStartCell());
	    				int startColumn = reportUtils.getExcelColumnValue(mQuery.getStartCell());
	    				String source 	= mQuery.getSource();
	    				String query 	= mQuery.getQuery();
	    				String queryResultColumnNames = mQuery.getQueryResultColumnNames();
	    				String queryResultColumnTypes = mQuery.getQueryResultColumnTypes();
	    				if(source != null && query != null && queryResultColumnNames != null && queryResultColumnTypes != null) {
	    					logger.info("Failure Query Execution - multiResultQuery");
	    					String colNameList[] 		= queryResultColumnNames.split(",");
	    					String colTypeList[] 		= queryResultColumnTypes.split(",");
	    					DatabaseQueryService 		dbQueryService = (DatabaseQueryService) queryServiceFactory.getQueryServices(ReportConstants.QUERY_SOURCE_DATABASE);
	    					List<Map<String, Object>> 	queryResult = dbQueryService.executeMultiResultQuery(source, query);
	    					if(queryResult!=null) {
	    						logger.info("Query result size = "+queryResult.size());
	    						for(int resIndex = 0 ; resIndex < queryResult.size(); resIndex++) {
	    							Map<String, Object> eachResultRow = queryResult.get(resIndex);
	    							for(int i = 0; i < colNameList.length; i++) {
	    								Cell currentCell = reportSheet.getRow(startRow + resIndex).getCell(startColumn + i);
	    								eachResultRow.get(colNameList[i]);
	    								double numberResult = 0.0;
	    								String stringResult = "";
	    								switch(colTypeList[i]) {
	    									case ReportConstants.QUERY_RESULT_TYPE_NUMBER:
	    										numberResult = ((BigDecimal)eachResultRow.get(colNameList[i])).doubleValue();
	    										logger.info("Number result = "+numberResult);
	    										currentCell.setCellValue(numberResult);
	    										break;
	    									case ReportConstants.QUERY_RESULT_TYPE_STRING:
	    										stringResult = (String)eachResultRow.get(colNameList[i]);
	    										logger.info("String result = "+stringResult);
	    										currentCell.setCellValue(stringResult);
	    										break;
	    									case ReportConstants.QUERY_RESULT_TYPE_DATE:
	    								
	    										break;
	    								}
	    							}
	    						}
	    						failureQueryList.remove(f);
	    					}
	    				}
	    			}
	    			reportWorkbook.close();
    			}
    			catch(Exception e) {
    				logger.info("Exception happend : "+e.getMessage());
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    public int totalNumberOfQueriesInReport(ReportConstants.REPORT report) {
    	int queryCount = 0;
    	logger.info("counting queries in a report");
    	ReportConfig reportConfigObj = reportConfiguration(report);
		String xmlConfPath = ApplicationPath.REPORT_SHEET_XML_CONFIGURATION_PATH;
		ReportSheetConfig reportSheets[] = reportConfigObj.getReportSheets();
		for(int i = 0; i < reportSheets.length; i++) {
			ReportQuery reportQueryObj = createReportQueryFromXML(xmlConfPath+"/"+reportSheets[i].getReportQueries());
			int numOfSingleResultQueries = reportQueryObj.getSingleResultQueries().size();
			int numOfMultiResultQueries = reportQueryObj.getMultiResultQueries().size();
			queryCount = queryCount + numOfSingleResultQueries + numOfMultiResultQueries;
		}
    	return queryCount;
    }
    
    public int numberOfFailedQueries(ReportConstants.REPORT report) {
    	int numberOfFailuresInReport = 0;
    	for(int i = 0; i < failureQueryList.size(); i++) {
    		if(failureQueryList.get(i).getReport()==report) {
    			numberOfFailuresInReport++;
    		}
    	}
    	return numberOfFailuresInReport;
    }
    
    public void removeFailedQueries(ReportConstants.REPORT report) {
    	logger.info("Removing failure queries on the report"+report.getReportFileName());
    	for(int i = 0; i < failureQueryList.size(); i++) {
    		if(failureQueryList.get(i).getReport()==report) {
    			FailureQueryModel fq = failureQueryList.remove(i);
    			if(fq.getSingleResQuery()!=null)
    				logger.info("Removed failure query="+fq.getSingleResQuery().getDescription());
    			if(fq.getMulResQuery()!=null)
    				logger.info("Removed failure query="+fq.getMulResQuery().getDescription());
    		}
    	}
    }
}

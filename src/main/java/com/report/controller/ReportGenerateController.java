package com.report.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.report.constants.ReportConstants;
import com.report.service.ProcessReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/generate")
@Api(value = "ReportGenerateController", tags = {"Generate Report"})
public class ReportGenerateController {

	private final static Logger logger = LoggerFactory.getLogger(ReportGenerateController.class);
	@Autowired
	protected ProcessReportService processReportService;
	
	@RequestMapping(path="/report_on_date" ,method=RequestMethod.GET)
	@ApiOperation(value = "Generate the report selected")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Report generated successfully"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public void generateReport(	@ApiParam(required = true, name = "report", value = "Select the report to generate")
								@RequestParam ReportConstants.REPORT report,
								@ApiParam(required = true, name = "reportDate", value = "Date on which report to generate (format must be DD-MM-YYYY)")
								@RequestParam String reportDate ){
		logger.info("Report on date : " +reportDate);
		processReportService.processReport(report, reportDate, false);
	}
	
	@RequestMapping(path="/all_reports_on_date" ,method=RequestMethod.GET)
	@ApiOperation(value = "Generate all reports")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "All reports generated"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			
	})
	public void generateAllReports(	@ApiParam(required = true, name = "reportDate", value = "Date on which reports to generate (format must be DD-MM-YYYY)")
									@RequestParam String reportDate){
		Arrays.asList(ReportConstants.REPORT.values()).forEach( report -> {
			logger.info("Generating Report for Report : " 	 + report.name());
			logger.info("Generating Report of Date  : "		 + reportDate);
			processReportService.processReport(report, reportDate, false);
			logger.info("Generating Report finished " + report.name());
		});
	}
	
	@RequestMapping(path="/report_on_date_range" ,method=RequestMethod.GET)
	@ApiOperation(value = "Generate reports for date range")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Reports generated for date range"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public void generateReportsForDateRange(@ApiParam(required = true, name = "startDate", value = "Start date to generate (format must be DD-MM-YYYY)")
											@RequestParam String startDate,
											@ApiParam(required = true, name = "endDate", value = "End date to generate (format must be DD-MM-YYYY)")
											@RequestParam String endDate) throws ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date stDate = dateFormat.parse(startDate);
		Date enDate = dateFormat.parse(endDate);
		int numOfDays = (int)( (enDate.getTime() - stDate.getTime()) / (1000 * 60 * 60 * 24));
		logger.info("Number of Days = "+numOfDays);
		Calendar cal = Calendar.getInstance();
		cal.setTime(stDate);
		if(numOfDays >= 0 && numOfDays < 7) {
			for(int i = 0; i <= numOfDays; i++) {
				Date currDate = cal.getTime();
				String currDateStr = dateFormat.format(currDate);
				Arrays.asList(ReportConstants.REPORT.values()).forEach( report -> {
					logger.info("Generating Report for Report : " 	 + report.name());
					logger.info("Generating Report of Date  : "		 + currDateStr);
					processReportService.processReport(report, currDateStr, false);
					logger.info("Generating Report finished " + report.name());
				});
				cal.add(Calendar.DATE, 1);
			}
		}
		else {
			throw new RuntimeException("Date Range is not proper");
		}
	}
	
	
}

package com.report.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.report.constants.ReportConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/report_logs")
@Api(value = "ReportLogsController", tags = {"Report Logs"})
public class ReportLogsController {
	
	private final static Logger logger = LoggerFactory.getLogger(ReportLogsController.class);

	@RequestMapping(path="/logs_on_date" ,method=RequestMethod.GET)
	@ApiOperation(value = "Get bc report log on selected date", response=File.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Return log file"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public ResponseEntity<Resource> bcReportLog( @ApiParam(required = true, name = "reportDateStr", value = "Date on which report to send (format must be DD-MM-YYYY)")
	                         @RequestParam String reportDateStr) throws IOException, ParseException {
		
		File logFile = null;
	    InputStreamResource resource  = null;
	    
		logger.debug("get report log on "+ reportDateStr);
		SimpleDateFormat smFormat 	= new SimpleDateFormat(ReportConstants.DATE_FORMAT_STRING);
		Calendar cal 				= Calendar.getInstance();
	    String currDay 				= smFormat.format(cal.getTime());
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    logger.debug("Current Day : "+currDay);
	    
	    Date repDate 				= smFormat.parse(reportDateStr);
	    Calendar repCal 			= Calendar.getInstance();
	    repCal.setTime(repDate);
	    int compCal 				= cal.compareTo(repCal);
	    logger.debug("Calendar difference = "+compCal);
	    String homePath				= System.getProperty("app.home");
	    logger.debug(homePath);
	   
	    if(compCal==0) {
	    	logFile = new File(homePath+"/bcReportLogs/LogFile.log");
		    if(logFile.exists()) {
	    		resource = new InputStreamResource(new FileInputStream(logFile));
	    	}
	    	else {
	    		throw new RuntimeException("report not found on date " + reportDateStr);
	    	}
	    }
	    else if(compCal > 0) {
	    	SimpleDateFormat repFormat = new SimpleDateFormat("yyyy-MM-dd");
		    String repDay = repFormat.format(repDate);
	    	logFile = new File(homePath+"/bcReportLogs/LogFile."+repDay+".log");
	    	if(logFile.exists()) {
	    		resource = new InputStreamResource(new FileInputStream(logFile));
	    	}
	    	else {
	    		throw new RuntimeException("report not found on date " + reportDateStr);
	    	}
	    }
	    else {
	    	throw new RuntimeException("report date should be less than current date");
	    }
    	
    	HttpHeaders headers = new HttpHeaders(); 
    	headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=BCReportLogFile_"+reportDateStr+".log");
        return ResponseEntity.ok()
        		.headers(headers)
                .contentLength(logFile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
	}
	
}

package com.report.controller;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.report.constants.ReportConstants;
import com.report.service.EmailReportService;
import com.report.service.ProcessReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/email_report")
@Api(value = "EmailReportController", tags = {"Email Report"})
public class EmailReportController {
	
	private final static Logger logger = LoggerFactory.getLogger(EmailReportController.class);
	@Autowired
	protected ProcessReportService processReportService;
	@Autowired
	protected EmailReportService emailReportService;
	
	@RequestMapping(path="/send_report" ,method=RequestMethod.GET)
	@ApiOperation(value = "Send report selected")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Send Reports To Configured Recipients"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public void sendReport(	@ApiParam(required = true, name = "report", value = "Select the report to send")
								@RequestParam ReportConstants.REPORT report, 
								@ApiParam(required = true, name = "reportDate", value = "Date on which report to send (format must be DD-MM-YYYY)")
								@RequestParam String reportDate) {
		try {
			emailReportService.sendReport(report, reportDate);
		} catch (MessagingException e) {
			logger.info("Report file is not found");
			e.printStackTrace();
		}
	}
	
}

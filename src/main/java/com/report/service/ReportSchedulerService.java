package com.report.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.report.constants.ReportConstants;


@Service
public class ReportSchedulerService {

	private final static Logger logger = LoggerFactory.getLogger(ReportSchedulerService.class);
	@Autowired
	private ProcessReportService processReportService;
	String reportScheduleDay;
	@Autowired
	protected EmailReportService emailReportService;
	
	private Map<ReportConstants.REPORT, Boolean> reportReadyToMailStatusMap;
	
	@PostConstruct
	public void initSchedulerService() {
		SimpleDateFormat smFormat = new SimpleDateFormat(ReportConstants.DATE_FORMAT_STRING);
		Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    reportScheduleDay = smFormat.format(cal.getTime());
	    logger.info("Report schedule day = "+reportScheduleDay);
	    reportReadyToMailStatusMap = new HashMap<>();
	    Arrays.asList(ReportConstants.REPORT.values()).forEach( report -> {
	    	reportReadyToMailStatusMap.put(report, false);
	    });
	}
	
	@Scheduled(cron="${report.application.process.service.reset.scheduler}")
	public void resetProcessReportServiceScheduler() {
		logger.info("reset process report service scheduler");
		processReportService.initService();
		initSchedulerService();
	}
	
	@Scheduled(cron="${report.application.process.failure.scheduler}")
	public void reportFailureExecutionScheduler() {
		logger.info("Application check for failures");
	    processReportService.processFailureQueries(reportScheduleDay);
	    Arrays.asList(ReportConstants.REPORT.values()).forEach( report -> {
	    	mailReportOnFailurePercentageLessThanTwenty(report);
	    });
	}
	
	@Scheduled(cron="${daily.report.scheduled.time.cron.expression}")
	public void scheduledReportGeneration() {
		Arrays.asList(ReportConstants.REPORT.values()).forEach( report -> {
			logger.info("Generating Report for Report : " + report.name());
			logger.info("Generating Report of Date  : "   + reportScheduleDay);
			processReportService.processReport(report, reportScheduleDay, true);
			reportReadyToMailStatusMap.put(report, true);
			mailReportOnFailurePercentageLessThanTwenty(report);
			logger.info("Generating Report finished " + report.name());
		});
	}
	
	public void mailReportOnFailurePercentageLessThanTwenty(ReportConstants.REPORT report) {
		if(processReportService.getReportSchedulerFinishStatus(report) && reportReadyToMailStatusMap.get(report)) {
			if(approxFailureQueryPercentage(report)<20) {
				logger.info("Failure percentage is less than 20%");
				processReportService.removeFailedQueries(report);
				logger.info("Sending report = "+report.getReportFileName());
				try {
					emailReportService.sendReport(report, reportScheduleDay);
					reportReadyToMailStatusMap.put(report, false);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public int approxFailureQueryPercentage(ReportConstants.REPORT report) {
		int totalQueries = processReportService.totalNumberOfQueriesInReport(report);
		int numOfFailures = processReportService.numberOfFailedQueries(report);
		int failurePercentage = numOfFailures / totalQueries * 100;
		logger.info("Total Queries = "+ totalQueries);
		logger.info("Number of Failures = "+ numOfFailures);
		logger.info("Failure Percentage = "+failurePercentage);
		return failurePercentage;
	}
	
}

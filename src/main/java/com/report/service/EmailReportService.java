package com.report.service;

import java.io.File;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.report.ApplicationPath;
import com.report.constants.ReportConstants;
import com.report.model.ReportConfig;
import com.report.util.ReportUtils;

@Service
public class EmailReportService {
	
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private ReportUtils reportUtils;
	@Autowired
	protected ProcessReportService processReportService;
	private final static Logger logger = LoggerFactory.getLogger(EmailReportService.class);
	
	@Value("${spring.mail.username}")
	private String mailUserName;
	
	public void sendReport(ReportConstants.REPORT report, String reportDate) throws MessagingException {
		
		ReportConfig reportConfig = processReportService.reportConfiguration(report);
		String reportFileName = reportConfig.getReportName();
		logger.info("Report File Name = "+reportFileName);
		String outputDirectory = ApplicationPath.REPORT_OUTPUT_PATH +"/"+ reportUtils.getReportPathOnADay(reportDate);
		
		logger.info("Report Output directory = "+outputDirectory);
		String reportFileOnDatePath = outputDirectory+"/"+reportFileName + "_" + reportUtils.getDayStringAppendReportFile(reportDate) + ".xlsx";
		logger.info("Report File On Date Path = "+reportFileOnDatePath);
		File reportFile = new File(reportFileOnDatePath);
		
		if(reportFile.exists()) {
			
			String reportMailSubject =  reportConfig.getMailOptions().getMailSubject();
			String[] mailRecipients  =  reportConfig.getMailOptions().getMailRecepients();
			String abPath = reportFile.getAbsolutePath();
			String fullFilename = abPath.substring(abPath.lastIndexOf('/') + 1);
			logger.info(fullFilename);
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED);
			logger.info(mailUserName);
			mimeMessageHelper.setFrom(mailUserName);
			InternetAddress[] recipientAddress = new InternetAddress[mailRecipients.length];
			for(int i = 0; i < mailRecipients.length; i++) {
				recipientAddress[i] = new InternetAddress(mailRecipients[i].trim());
			}
			mimeMessage.setRecipients(Message.RecipientType.TO, recipientAddress);
			mimeMessageHelper.setSubject(reportMailSubject+" on "+reportDate);
			mimeMessageHelper.setText("Dear All\n\n\n\t\tPlease find "+reportMailSubject+" on "+reportDate+"\n\n\nRegards,\nReport Controller");
			
			mimeMessageHelper.addAttachment(fullFilename, reportFile);
			logger.info("Sending message");
			javaMailSender.send(mimeMessage);
		}
		else {
			logger.info("Report file is not found");
			logger.info("Report sending is failed");
		}
	}
}

package com.report.controller;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.report.ApplicationPath;
import com.report.constants.ReportConstants;
import com.report.model.ReportConfig;
import com.report.service.ProcessReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/report_templates")
@Api(value = "ReportTemplatesController", tags = {"Report Templates"})
public class ReportTemplatesController  {
	
	private final static Logger logger = LoggerFactory.getLogger(ReportTemplatesController.class);
	@Autowired
	protected ProcessReportService processReportService;

	@RequestMapping(path="/upload_templates" ,method=RequestMethod.POST)
	@ApiOperation(value = "Upload report templates files")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Template file uploaded successfully"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public void uploadReportTemplate(@ApiParam (required = true, name = "report", value = "Select report")
	                                 @RequestParam ReportConstants.REPORT report, 
	                                 @ApiParam (required = true, name = "template_file", value = "select template file")
	                                 @RequestParam MultipartFile template_file) throws IOException {
		logger.debug("Uploading template file");
		logger.debug(template_file.getName());
		logger.debug(template_file.getOriginalFilename());
		logger.debug(template_file.getContentType());
		
		String fileExtention = template_file.getOriginalFilename().substring(template_file.getOriginalFilename().lastIndexOf('.'));
		logger.debug(fileExtention);
		
		if(fileExtention.compareTo(".xlsx")==0) {
			ReportConfig reportConfigObj = processReportService.reportConfiguration(report);
			File targetTemplateFile = new File(ApplicationPath.REPORT_TEMPLATES_PATH+"/"+reportConfigObj.getReportName()+".xlsx");
			template_file.transferTo(targetTemplateFile);
		}
		else {
			throw new RuntimeException("Invalid Extension");
		}
		
		
	}
	
}

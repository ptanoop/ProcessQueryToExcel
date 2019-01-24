package com.report.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.report.constants.ReportConstants;
import com.report.model.ReportQuery;
import com.report.model.MultiResultQuery;
import com.report.model.SingleResultQuery;
import com.report.service.ProcessReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;



class QueryModel{
	private String description;
	private String query;
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}

@RestController
@RequestMapping("/report_queries")
@Api(value = "ReportQueryController", tags = {"Report Queries"})
public class ReportQueryController {
	
	@Autowired
	protected ProcessReportService processReportService;
	
	private final static Logger logger = LoggerFactory.getLogger(ReportQueryController.class);

	@RequestMapping(path="/queries_in_report" ,method=RequestMethod.GET)
	@ApiOperation(value = "Get queries in a report")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Return Query Array"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public ArrayList<QueryModel> queriesInReport(ReportConstants.REPORT report, String reportDate) {
		
		logger.info("Read Queries");
		ArrayList<QueryModel> queryList = new ArrayList<QueryModel>();
		
	    ArrayList<ReportQuery> bcReportObjectArray = processReportService.getReportQueryObjects(report);
	   
	    bcReportObjectArray.forEach((reportObject) -> {
	    	List<SingleResultQuery> singleResultQueries = reportObject.getSingleResultQueries();
	    	if(singleResultQueries != null) {
	    		singleResultQueries.forEach((singleQuery)->{
		    		//logger.info(singleQuery.getQuery());
		    		String querySourceType = singleQuery.getSourceType();
		    		if(querySourceType.equals(ReportConstants.QUERY_SOURCE_DATABASE)) {
		    			String formatQuery = singleQuery.getQuery().trim().replaceAll("[\\n\\t]", " ");
			    		if(!formatQuery.isEmpty()) {
			    			String formatQueryArr[] = formatQuery.split(" ");
				    		formatQuery = "";
				    		for(int i = 0 ; i < formatQueryArr.length; i++) {
				    			//logger.info("'"+formatQueryArr[i]+"'");
				    			if(formatQueryArr[i].length()>0)
				    				formatQuery = formatQuery + " " + formatQueryArr[i].trim();
				    		}
				    		formatQuery = formatQuery.replaceAll("DATE_STRING", reportDate);
				    		QueryModel qm = new QueryModel();
				    		qm.setDescription(singleQuery.getDescription().trim());
				    		qm.setQuery(formatQuery);
				    		queryList.add(qm);
			    		}
		    		}	
		    	});
	    	}
	    	
	    	List<MultiResultQuery> multiResultQueries = reportObject.getMultiResultQueries();
	    	if(multiResultQueries!=null) {
		    	multiResultQueries.forEach((multiQuery) -> {
		    		if(multiQuery!=null) {
			    		String formatQuery = multiQuery.getQuery().trim().replaceAll("[\\n\\t]", " ");
			    		String desp = multiQuery.getDescription();
			    		if(!formatQuery.isEmpty()) {
			    			String formatQueryArr[] = formatQuery.split(" ");
				    		formatQuery = "";
				    		for(int i = 0 ; i < formatQueryArr.length; i++) {
				    			if(formatQueryArr[i].length()>0)
				    				formatQuery = formatQuery + " " + formatQueryArr[i].trim();
				    		}
				    		formatQuery = formatQuery.replaceAll("DATE_STRING", reportDate);
				    		QueryModel qm = new QueryModel();
				    		qm.setDescription(desp);
				    		qm.setQuery(formatQuery);
				    		queryList.add(qm);
			    		}
			    	}
		    	});
	    	}
	    
	    });
	    
		return queryList;
		
	}
	
}

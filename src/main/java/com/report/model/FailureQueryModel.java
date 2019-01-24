package com.report.model;

import com.report.constants.ReportConstants;

public class FailureQueryModel {

	private ReportConstants.REPORT report;
	
	private String sheetName;
	
	private SingleResultQuery singleResQuery;
	
	private MultiResultQuery mulResQuery;
	
	public ReportConstants.REPORT getReport() {
		return report;
	}

	public void setReport(ReportConstants.REPORT report) {
		this.report = report;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	
	public SingleResultQuery getSingleResQuery() {
		return singleResQuery;
	}

	public void setSingleResQuery(SingleResultQuery singleResQuery) {
		this.singleResQuery = singleResQuery;
	}

	public MultiResultQuery getMulResQuery() {
		return mulResQuery;
	}

	public void setMulResQuery(MultiResultQuery mulResQuery) {
		this.mulResQuery = mulResQuery;
	}
	
	public FailureQueryModel(ReportConstants.REPORT report, String stName, SingleResultQuery sQuery) {
		this.setReport(report);
		this.setSheetName(stName);
		this.setSingleResQuery(sQuery);
	}
	
	public FailureQueryModel(ReportConstants.REPORT report, String stName, MultiResultQuery mQuery) {
		this.setReport(report);
		this.setSheetName(stName);
		this.setMulResQuery(mQuery);
	}
	
}

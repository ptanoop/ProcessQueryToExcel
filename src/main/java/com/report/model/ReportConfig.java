package com.report.model;

public class ReportConfig {

	private String reportName;
	private ReportSheetConfig reportSheets[];
	private ReportMailOptions mailOptions;

	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public ReportSheetConfig[] getReportSheets() {
		return reportSheets;
	}
	public void setReportSheets(ReportSheetConfig[] reportSheets) {
		this.reportSheets = reportSheets;
	}
	public ReportMailOptions getMailOptions() {
		return mailOptions;
	}
	public void setMailOptions(ReportMailOptions mailOptions) {
		this.mailOptions = mailOptions;
	}

}

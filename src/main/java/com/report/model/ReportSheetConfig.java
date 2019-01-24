package com.report.model;

public class ReportSheetConfig {

	private String 	reportQueries;
	private String 	sheetName;
	private String 	startCell;
	private String 	endCell;
	private int 	columnShift;

	public String getReportQueries() {
		return reportQueries;
	}
	public void setReportQueries(String reportQueries) {
		this.reportQueries = reportQueries;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getStartCell() {
		return startCell;
	}
	public void setStartCell(String startCell) {
		this.startCell = startCell;
	}
	public String getEndCell() {
		return endCell;
	}
	public void setEndCell(String endCell) {
		this.endCell = endCell;
	}
	public int getColumnShift() {
		return columnShift;
	}
	public void setColumnShift(int columnShift) {
		this.columnShift = columnShift;
	}


}

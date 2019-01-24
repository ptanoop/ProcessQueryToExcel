package com.report.model;

public class MultiResultQuery {
	
	String startCell;
	String endCell;
	String query;
	String source;
	String queryResultColumnNames;
	String queryResultColumnTypes;
	String description;
	 
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getQueryResultColumnNames() {
		return queryResultColumnNames;
	}
	public void setQueryResultColumnNames(String queryResultColumnNames) {
		this.queryResultColumnNames = queryResultColumnNames;
	}
	public String getQueryResultColumnTypes() {
		return queryResultColumnTypes;
	}
	public void setQueryResultColumnTypes(String queryResultColumnTypes) {
		this.queryResultColumnTypes = queryResultColumnTypes;
	}
	public void insertDateParameter(String dateStr) {
		if(this.query!=null) {
			this.query = this.query.replaceAll("DATE_STRING", dateStr).trim();
		}
	}
	public String toString() {
		String toStr = "";
		toStr = "{ startRow : "+this.startCell+", startColumn : "+this.endCell+", query : \n\t"+this.query+", description : \n\t"+this.description+" } ";
		return toStr;
	}
	
	 
}



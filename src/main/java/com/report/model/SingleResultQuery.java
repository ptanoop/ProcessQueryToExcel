package com.report.model;

public class SingleResultQuery {

	String cell;
	String query;
	String resultType;
	String sourceType;
	String source;
	String action;
	String parameters;
	String description;
	
	public String getCell() {
		return cell;
	}
	public void setCell(String cell) {
		this.cell = cell;
	}
	public String getQuery() {
		if(query==null)setQuery("");
		return query.trim();
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getResultType() {
		if(resultType==null)setResultType("");
		return resultType.trim();
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getSourceType() {
		if(sourceType==null)setSourceType("");
		return sourceType.trim();
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getSource() {
		if(source==null)setSource("");
		return source.trim();
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getAction() {
		if(action==null)setAction("");
		return action.trim();
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getParameters() {
		if(parameters==null)setParameters("");
		return parameters.trim();
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getDescription() {
		if(description==null)setDescription("");
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void insertDateParameter(String dateStr) {
		if(this.query!=null) {
			this.query = this.query.replaceAll("DATE_STRING", dateStr).trim();
		}
	}
	public String toString() {
		String toStr = "";
		toStr = "{ row : "+this.cell+", resultType : "+this.resultType+", action : "+this.action+", query : \n\t"+this.query.trim()+"\n\t } ";
		return toStr;
	}
	
}

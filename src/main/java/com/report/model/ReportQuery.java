package com.report.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ReportQuery")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportQuery {
	@XmlElement(name = "SingleResultQuery")
	private List<SingleResultQuery> singleResultQueries;
	@XmlElement(name = "MultiResultQuery")
	private List<MultiResultQuery> multiResultQueries;

	public ReportQuery() {
		singleResultQueries = new ArrayList<>();
		multiResultQueries  = new ArrayList<>();
	}

	public List<SingleResultQuery> getSingleResultQueries() {
		return singleResultQueries;
	}

	public void setSingleResultQueries(List<SingleResultQuery> singleResultQueries) {
		this.singleResultQueries = singleResultQueries;
	}

	public List<MultiResultQuery> getMultiResultQueries() {
		return multiResultQueries;
	}

	public void setMultiResultQueries(List<MultiResultQuery> multiResultQueries) {
		this.multiResultQueries = multiResultQueries;
	}

	public void printObject() {
		if(singleResultQueries!=null) {
			for(int i = 0; i < singleResultQueries.size(); i++) {
				System.out.println(singleResultQueries.get(i).toString());
			}
		}
		if(multiResultQueries!=null) {
			for(int i = 0; i < multiResultQueries.size(); i++) {
				System.out.println(multiResultQueries.get(i).toString());
			}
		}
	}
}

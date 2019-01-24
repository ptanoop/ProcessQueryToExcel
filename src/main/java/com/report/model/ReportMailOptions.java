package com.report.model;

public class ReportMailOptions {

	private String mailSubject;
	private String mailRecepients[];

	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	public String[] getMailRecepients() {
		return mailRecepients;
	}
	public void setMailRecepients(String[] mailRecepients) {
		this.mailRecepients = mailRecepients;
	}

}

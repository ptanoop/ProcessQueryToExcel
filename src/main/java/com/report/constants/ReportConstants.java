package com.report.constants;

public class ReportConstants {

	public static enum REPORT{
												DailyReport	 	("daily_report.json");

												private final String reportFileName;


												private REPORT(String fileName) {
													reportFileName = fileName;
												}

												public String toString() {
													return this.name();
											    }

												public String getReportFileName() {
													return reportFileName;
												}

											 };

	public static enum QUERY_EXECUTION_STATUS{
												SUCCESS,
												FAILURE,
												UNKNOWN
											 };


	public final static String DATE_FORMAT_STRING 			= "dd-MM-yyyy";
	public final static String DATE_FORMAT_FOR_OUTPUT_FILE 	= "dd.MMM.yy";

	public final static String QUERY_RESULT_TYPE_NUMBER 	= "Number";
	public final static String QUERY_RESULT_TYPE_STRING 	= "String";
	public final static String QUERY_RESULT_TYPE_DATE 		= "Date";

	public final static String QUERY_SOURCE_DATABASE 		= "DATABASE";
	public final static String QUERY_SOURCE_SHEET			= "SHEET";
}

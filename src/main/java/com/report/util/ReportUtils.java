package com.report.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.report.ApplicationPath;
import com.report.constants.ReportConstants;

@Component
public class ReportUtils {
	
	private final Logger logger = LoggerFactory.getLogger(ReportUtils.class);
	
	public String previousDay(String dateStr) {
		String preDay = "";
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(ReportConstants.DATE_FORMAT_STRING);
			Date currDay = dateFormat.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(currDay);
			cal.add(Calendar.DATE, -1);
			Date previousDate = cal.getTime();
			preDay = dateFormat.format(previousDate);
		}
		catch(Exception e) {
			logger.debug("Exception occured on previous date calculation", e);
		}
		return preDay;
	}
	
	public String formatDate(Date rdate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(ReportConstants.DATE_FORMAT_STRING);
		String fdate = null;
		try {
			fdate = dateFormat.format(rdate);
		}
		catch(Exception e) {
			logger.debug("Exception occured on date formatting", e);
		}
		return fdate;
	}
	
	public Date parseDate(String dateStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(ReportConstants.DATE_FORMAT_STRING);
		Date fdate = null;
		try {
			fdate = dateFormat.parse(dateStr);
		}
		catch(Exception e) {
			logger.debug("Exception occured on date parsing", e);
		}
		return fdate;
	}

	
	private String getCellRowPart(String cellVal) {
		String rowStr = "";
		char[] colArr = cellVal.toCharArray();
		for(int i = 0; i < colArr.length; i++) {
			if(Character.isDigit(colArr[i])) {
				rowStr = cellVal.substring(i);
				break;
			}
		}
		return rowStr;
	}
	
	private String getCellColumnPart(String cellVal) {
		String colStr = "";
		char[] colArr = cellVal.toCharArray();
		for(int i = 0; i < colArr.length; i++) {
			if(Character.isDigit(colArr[i]))break;
			colStr = colStr + colArr[i];
		}
	    return colStr;
	}
	
	public int getExcelColumnValue(String cellVal) {
		int col = 0;
		char[] colArr = getCellColumnPart(cellVal).toCharArray();
		for(int i = 0; i < colArr.length; i++) {
			
			int baseVal = colArr[colArr.length - (i+1)] - 'A' + 1;
			int basePower = (int)Math.pow(26, i);
			col = col + basePower * baseVal;
		}
		col--;
		logger.info("Col of "+cellVal+ " = "+col);
		return col;
	}
	
	public int getExcelRowValue(String cellVal) {
		int row = 0;
		String rowStr = getCellRowPart(cellVal);
		row = Integer.parseInt(rowStr);
		row--;
		logger.info("Row of "+cellVal+ " = "+row);
		return row;
	}
	
	public String getExcelCellString(int row, int col) {
		String cellString = "";
		int tempCol = Math.abs(col);
		while((tempCol/26)!=0) {
			cellString = cellString + (char)('A' + (tempCol/26 - 1));
			logger.info(cellString);
			tempCol = (int) tempCol / 26;
		}
		cellString = cellString +  (char)('A' + (col%26));
		logger.info(cellString);
		cellString = cellString + (row + 1);
		return cellString;
	}
	
	public String getReportPathOnADay(String dateStr) {
		String reportPath = "";
		try {
			SimpleDateFormat sim = new SimpleDateFormat("dd-MM-yyyy");
			Date rDate = sim.parse(dateStr);
			Calendar mCalendar = Calendar.getInstance();    
			mCalendar.setTime(rDate);
			String monthStr = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
			String yearStr = Integer.toString(mCalendar.get(Calendar.YEAR));
			String dayStr = Integer.toString(mCalendar.get(Calendar.DAY_OF_MONTH));
			if(dayStr.length()<2)dayStr = "0"+dayStr;
			reportPath = yearStr + "/" + monthStr + "/"+dayStr;
			logger.info(" report Path = "+reportPath);
			
		}
		catch(Exception e) {
			logger.debug("Report Path on a date" ,e);
		}
		return reportPath;
	}
	
	public String getDayStringAppendReportFile(String dateStr) {
		SimpleDateFormat dateFormatForOuputFile = new SimpleDateFormat(ReportConstants.DATE_FORMAT_FOR_OUTPUT_FILE);
		SimpleDateFormat dateFormat = new SimpleDateFormat(ReportConstants.DATE_FORMAT_STRING);
		String appendDateStr = "";
		try {
			Date rDate = dateFormat.parse(dateStr);
			appendDateStr = dateFormatForOuputFile.format(rDate);
		}
		catch(Exception e) {
			logger.debug("Append String on Report File" ,e);
		}
		return appendDateStr;
	}
	
	public String getReportFileToWorkOn(String templateFilePath, String reportName, String outputFilePath, String dateStr) {
		String previousDayStr = previousDay(dateStr);
		String reportPathOnPreviousDay = getReportPathOnADay(previousDayStr);
		String appendDateStr = getDayStringAppendReportFile(previousDayStr);
		String previousDayReportFilePath = outputFilePath + "/" +reportPathOnPreviousDay + "/"+reportName + "_" + appendDateStr+".xlsx";
		logger.info(previousDayReportFilePath); 
		File previousReportFile = new File(previousDayReportFilePath);
		if(!previousReportFile.exists()) {
			logger.info("previous day report is not available, looking for template"); 
			previousDayReportFilePath = outputFilePath + "/TEMPLATES/"+reportName+".xlsx";
			logger.info("Reading template file : "+previousDayReportFilePath); 
		}
		return previousDayReportFilePath;
	}
	
	public String getReportFileToWorkOn(String reportName, String dateStr) {
		String previousDayStr = previousDay(dateStr);
		String reportPathOnPreviousDay = getReportPathOnADay(previousDayStr);
		String appendDateStr = getDayStringAppendReportFile(previousDayStr);
		String previousDayReportFilePath = ApplicationPath.REPORT_OUTPUT_PATH + "/" +reportPathOnPreviousDay + "/"+reportName + "_" + appendDateStr+".xlsx";
		logger.info(previousDayReportFilePath); 
		File previousReportFile = new File(previousDayReportFilePath);
		if(!previousReportFile.exists()) {
			logger.info("previous day report is not available, looking for template"); 
			previousDayReportFilePath = ApplicationPath.REPORT_OUTPUT_PATH  + "/TEMPLATES/"+reportName+".xlsx";
			logger.info("Reading template file : "+previousDayReportFilePath); 
		}
		return previousDayReportFilePath;
	}
	
	public File getOutputReportFile(String reportName, String outputFilePath,String dateStr) {
		File outputReportFile = null;
		String reportPathOnADay = getReportPathOnADay(dateStr);
		String appendDateStr = getDayStringAppendReportFile(dateStr);
		String outputReportFilePath = outputFilePath + "/" + reportPathOnADay + "/" + reportName + "_" + appendDateStr + ".xlsx";
		outputReportFile = new File(outputReportFilePath);
		return outputReportFile;
	}
	
	public File getOutputReportFile(String reportName, String dateStr) {
		File outputReportFile = null;
		String reportPathOnADay = getReportPathOnADay(dateStr);
		String appendDateStr = getDayStringAppendReportFile(dateStr);
		String outputReportFilePath = ApplicationPath.REPORT_OUTPUT_PATH + "/" + reportPathOnADay + "/" + reportName + "_" + appendDateStr + ".xlsx";
		outputReportFile = new File(outputReportFilePath);
		return outputReportFile;
	}
	
	public void resetReportCell(Cell currentCell) {
		String nullString = null;
		if(currentCell!=null) {
			switch(currentCell.getCellType()) {
				case NUMERIC:
					currentCell.setCellType(CellType.BLANK);
					currentCell.setCellValue(nullString);
					break;
				case STRING:
					currentCell.setCellValue(nullString);
					break;	
				case FORMULA:
					//currentCell.setCellFormula(nextCell.getCellFormula());
					break;		
				case BLANK:
					currentCell.setCellType(CellType.BLANK);
					currentCell.setCellValue(nullString);
					break;	
				case BOOLEAN:
					currentCell.setCellValue(false);
					break;
				case ERROR:
					//currentCell.setCellErrorValue();
					break;
				case _NONE:
					currentCell.setCellType(CellType.BLANK);
					currentCell.setCellValue(nullString);
					break;
				default:
					break;	
			}
		}
	}
	
	public static void main(String args[]) {
		ReportUtils cm = new ReportUtils();
		System.out.println("Converted value = "+cm.getExcelColumnValue("A12"));
		System.out.println("Converted value = "+cm.getExcelRowValue("A12"));
		//cm.reportPathOnADay("06-08-2018");
	}
	
}

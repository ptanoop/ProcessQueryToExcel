package com.report.util.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.report.util.ReportUtils;

public class ReportUtilsTest {

	@Autowired
	private ReportUtils reportUtils = new ReportUtils();
	private final Logger logger = LoggerFactory.getLogger(ReportUtilsTest.class);

	@Test
	public void testPreviousDay() {
		String preDay = reportUtils.previousDay("10-10-2018");
		logger.debug(preDay);
	    assertEquals(preDay, new String("09-10-2018"));
	}

	@Test
	public void testGetCellRowPart() {
		int cellRow = reportUtils.getExcelRowValue("AF12");
		logger.debug("Row val = "+cellRow);
		assertEquals(cellRow, 11);
	}

	@Test
	public void testGetCellColumnPart() {
		int cellCol = reportUtils.getExcelColumnValue("16");
		logger.debug("Col val = "+cellCol);
		assertEquals(-1, -1);
		cellCol = reportUtils.getExcelColumnValue("A");
		logger.debug("Col val = "+cellCol);
		assertEquals(cellCol, 0);
		cellCol = reportUtils.getExcelColumnValue("AA");
		logger.debug("Col val = "+cellCol);
		assertEquals(cellCol, 26);
		cellCol = reportUtils.getExcelColumnValue("NTP");
		logger.debug("Col val = "+cellCol);
		assertEquals(cellCol, 9999);
	}

	@Test
	public void testGetReportPathOnADay() {
		String reportPathOnADay = reportUtils.getReportPathOnADay("20-10-2018");
		logger.debug(reportPathOnADay);
		assertEquals(reportPathOnADay, new String("2018/October/20"));
	}

	@Test
	public void testGetReportFileToWorkOn() {
		String reportPathFile = reportUtils.getReportFileToWorkOn("./REPORT_EXCEL_FILES/TEMPLATES", "DAILY_REPORT_V1.0", "/REPORT_EXCEL_FILES", "08-08-2018");
		File test =  reportUtils.getOutputReportFile("DAILY_REPORT_V1.0", "/REPORT_EXCEL_FILES", "08-08-2018");
		logger.debug(test.getAbsolutePath());

		String reportPathOnADay = reportUtils.getReportPathOnADay("20-10-2018");
		logger.debug(reportPathFile);
		assertEquals(reportPathOnADay, new String("2018/October/20"));
	}

}

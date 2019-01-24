package com.report.util.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.helpers.XSSFColumnShifter;


public class ColumnShiftTest {

	public static void main(String[] args) {

		String workFilePath = "path.xlsx";
		try {
			XSSFWorkbook ngbWorkbook = new XSSFWorkbook(new FileInputStream(new File(workFilePath)));
			Sheet ngbWorkSheet = (Sheet) ngbWorkbook.getSheet("Daily KPIs");
			XSSFColumnShifter colShifter = new XSSFColumnShifter((XSSFSheet) ngbWorkSheet);
			colShifter.shiftColumns(3, 31, 1);
			FileOutputStream fileOut = new FileOutputStream("out.xlsx");
			ngbWorkbook.write(fileOut);
	        fileOut.close();
			ngbWorkbook.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

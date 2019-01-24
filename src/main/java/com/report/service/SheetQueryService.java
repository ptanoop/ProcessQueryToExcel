package com.report.service;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.report.constants.ReportConstants;
import com.report.constants.ReportConstants.QUERY_EXECUTION_STATUS;



@Service
public class SheetQueryService implements IQueryService {
	
	private final Logger logger = LoggerFactory.getLogger(SheetQueryService.class);
	

	@Override
	public ReportConstants.QUERY_EXECUTION_STATUS executeQuery(Cell cell, String source, String query, String resultType) {
		logger.info("Executing sheet query");
		logger.info("Source = "+source);
		logger.info("query = "+query);
		try {
			if(source!=null && !source.isEmpty()) {
				XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(source)));
				FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
				workbook.createSheet("tempWorkSheet");
				Sheet tempWorkSheet = workbook.getSheet("tempWorkSheet");
				tempWorkSheet.createRow(0).createCell(0);
				Cell tempCell = tempWorkSheet.getRow(0).getCell(0);
				tempCell.setCellFormula(query);
				CellValue cellVal = formulaEvaluator.evaluate(tempCell);
				logger.info("Executed Formula = "+cellVal.formatAsString());
				switch(resultType) {
					case ReportConstants.QUERY_RESULT_TYPE_NUMBER:
						cell.setCellValue(cellVal.getNumberValue());
						break;
					case ReportConstants.QUERY_RESULT_TYPE_STRING:
						cell.setCellValue(cellVal.getStringValue());
						break;
				}
				workbook.close();
			}
			else {
				cell.setCellFormula(query);
				FormulaEvaluator formulaEvaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
				CellValue cellVal = formulaEvaluator.evaluate(cell);
				logger.info("Executed Formula = "+cellVal.formatAsString());
				switch(resultType) {
					case ReportConstants.QUERY_RESULT_TYPE_NUMBER:
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(cellVal.getNumberValue());
						break;
					case ReportConstants.QUERY_RESULT_TYPE_STRING:
						cell.setCellType(CellType.STRING);
						cell.setCellValue(cellVal.getStringValue());
						break;
				}
			}
			logger.info("Executing sheet finished");
			return QUERY_EXECUTION_STATUS.SUCCESS;
		}
		catch(Exception e) {
			logger.debug("Exception occurs on sheet query execution ", e);
			return QUERY_EXECUTION_STATUS.FAILURE;
		}
		
	}

}

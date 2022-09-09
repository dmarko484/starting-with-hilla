package com.example.application;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkbookGeneratorService {
	
	@Autowired
	TodoRepository todoRepository;

	public Workbook createExcelFile() {	
		Workbook wb = new XSSFWorkbook();
		
		Sheet sheet = wb.createSheet("Todos");
		Row row = sheet.createRow(0);
		
		Cell cell = row.createCell(0);
		cell.setCellValue("Todo Items: "+todoRepository.count());
		
		
		int line = 2;
		
		for(Todo todo: todoRepository.findAll()) {
			row = sheet.createRow(line);
			
			cell = row.createCell(0);
			cell.setCellValue(todo.getTask());
			
			cell = row.createCell(1);
			cell.setCellValue(todo.getAuthor());
			
			if(todo.isDone()) {
				cell = row.createCell(2);
				cell.setCellValue("DONE");
			}
			
			line++;
		}
		
		return wb;
	}
	
}

package com.example.application;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Controller
@RequestMapping("/excel")
public class ExcelController {

	@Autowired
	WorkbookGeneratorService workbookGeneratorService;

	@GetMapping
	public ResponseEntity<StreamingResponseBody> excel() {
		Workbook workBook = workbookGeneratorService.createExcelFile();

		return ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"excel-todos.xlsx\"")
				.body(workBook::write);
	}

}

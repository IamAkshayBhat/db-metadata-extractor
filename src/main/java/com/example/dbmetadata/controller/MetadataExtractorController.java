package com.example.dbmetadata.controller;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dbmetadata.service.MetadataExtractionService;

@Controller
@RequestMapping("/metadata-extractor")
public class MetadataExtractorController {

	@Autowired
	private MetadataExtractionService metadataExtractionService;
	
	@GetMapping("/extract")
	public String extractMetadataFromDB() throws SQLException, IOException {
		metadataExtractionService.extractAndWriteMetadata();
		return "Folder name \"Output\" is created with metadata information";
	}
}

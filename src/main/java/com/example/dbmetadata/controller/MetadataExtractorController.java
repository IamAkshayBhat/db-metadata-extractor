package com.example.dbmetadata.controller;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dbmetadata.service.MetadataExtractionService;

@RestController
@RequestMapping("/metadata-extractor")
public class MetadataExtractorController {

	@Autowired
	private MetadataExtractionService metadataExtractionService;
	
	@GetMapping("/extract")
	public String extractMetadataFromDB() throws SQLException, IOException {
		metadataExtractionService.extractAndWriteMetadata();
		return "Metadata JSON files are saved under \"metadata_output/\", one file per table.";
	}
}

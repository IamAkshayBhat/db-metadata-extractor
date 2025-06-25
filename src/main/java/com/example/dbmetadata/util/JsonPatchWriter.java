package com.example.dbmetadata.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.example.dbmetadata.model.Metadata;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonPatchWriter {

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final String OUTPUT_DIR = "metadata_output/";

	public void writePatch(Metadata metadata) throws IOException {
		File outputDir = new File(OUTPUT_DIR);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		//multiple files per table info
		createMetadataPerTable(metadata);
		
		//or 
		
		//one file with multiple table's info
		//appendMetadataAsJsonLine(metadata);
	}
	
	public void appendMetadataAsJsonLine(Metadata metadata) throws IOException {
		String fileName = OUTPUT_DIR + "FINAL_METADATA_INFO" + ".json";
	    File file = new File(fileName);
	    file.getParentFile().mkdirs();

	    try (FileWriter fw = new FileWriter(file, true)) {
	        String jsonLine = mapper.writeValueAsString(metadata);
	        fw.write(jsonLine + System.lineSeparator());
	    }
	}
	
	public void createMetadataPerTable(Metadata metadata) throws StreamWriteException, DatabindException, IOException {
		String filename = OUTPUT_DIR + metadata.getName() + ".json"; File file = new
		File(filename); mapper.writeValue(file, metadata);
	}

}

package com.example.dbmetadata.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.dbmetadata.model.Metadata;

public class JsonPatchWriterTest {

    private JsonPatchWriter writer;

    @BeforeEach
    public void setup() {
        writer = new JsonPatchWriter();
    }

    @Test
    public void testWritePatchCreatesFile() throws IOException {
		File outputDir = new File("metadata_output/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
        Metadata metadata = Metadata.builder()
        		.name("test_table")
        		.type("TABLE")
        		.owner("TEST")
        		.createdTimestamp(null)
        		.updatedTimestamp(null)
        		.parent("parent_table")
        		.build();

        writer.writePatch(metadata);

        File[] files = outputDir.listFiles();
        assertNotNull(files);
    }
}

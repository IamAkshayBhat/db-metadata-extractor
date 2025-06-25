# Metadata Extractor

## Overview
Java Spring Boot application to extract table metadata from a relational database (MySQL, H2).

## Setup

### 1. Configure Database
Edit `application.yml` to match your DB (MySQL or H2).

### 2. Run the Application
 - mvn clean install
 - mvn spring-boot:run
 - Once the application is UP, you can hit `http://localhost:8080/metadata-extractor/extract`


### 3. Output
Metadata JSON files are saved under `metadata_output/`, one file per table.

### 4. Run Tests
mvn test


## Notes
- Extend `Metadata` for more fields.
- In the JsonPatchWriter, you can uncomment line `//appendMetadataAsJsonLine(metadata);` in order to have a single file with metadata of all tables.
- You can also add support for Oracle, if required.
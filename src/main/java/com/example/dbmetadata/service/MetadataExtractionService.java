package com.example.dbmetadata.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dbmetadata.model.Metadata;
import com.example.dbmetadata.util.JsonPatchWriter;

@Service
public class MetadataExtractionService {

	@Autowired
    private DataSource dataSource;

    @Autowired
    private JsonPatchWriter patchWriter;

	public void extractAndWriteMetadata() throws SQLException, IOException {
		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE", "VIEW" });

			while (tables.next()) {
				String tableName = tables.getString("TABLE_NAME");
				Metadata metadata = Metadata.builder()
						.name(tableName)
						.type(tables.getString("TABLE_TYPE"))
						.owner(tables.getString("TABLE_SCHEM"))
						.parent(getParentInfo(connection, tableName))
						.createdTimestamp(convertTimestampToLocalDateTime(getCreationTime(connection, tableName)))
						.updatedTimestamp(convertTimestampToLocalDateTime(getUpdateTime(connection, tableName)))
						.build();

				patchWriter.writePatch(metadata);
			}
		}
	}
    
    private Timestamp getCreationTime(Connection conn, String tableName) {
        String dbProduct = getDatabaseProduct(conn);
        String sql;

        if ("MySQL".equalsIgnoreCase(dbProduct)) {
            sql = "SELECT CREATE_TIME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?";
        } else if ("Oracle".equalsIgnoreCase(dbProduct)) {
            sql = "SELECT CREATED FROM ALL_OBJECTS WHERE OBJECT_TYPE = 'TABLE' AND OBJECT_NAME = ?";
        } else {
            return null;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName.toUpperCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching create time: " + e.getMessage());
        }

        return null;
    }


    private Timestamp getUpdateTime(Connection conn, String tableName) {
        String dbProduct = getDatabaseProduct(conn);
        String sql;

        if ("MySQL".equalsIgnoreCase(dbProduct)) {
            sql = "SELECT UPDATE_TIME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?";
        } else if ("Oracle".equalsIgnoreCase(dbProduct)) {
            // Oracle doesn't store UPDATE_TIME by default, return null or implement triggers
            return null;
        } else {
            return null;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName.toUpperCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching update time: " + e.getMessage());
        }

        return null;
    }

    private String getParentInfo(Connection conn, String tableName) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet fkResultSet = metaData.getImportedKeys(null, null, tableName);

            if (fkResultSet.next()) {
                return fkResultSet.getString("PKTABLE_NAME");
            } 
        } catch (SQLException e) {
            System.err.println("Error fetching parent info for table " + tableName + ": " + e.getMessage());
        }
        return null;
    }

    private String getDatabaseProduct(Connection conn) {
        try {
            return conn.getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            return "";
        }
    }
    
    private LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
    	return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
    
}

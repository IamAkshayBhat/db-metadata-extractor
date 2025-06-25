package com.example.dbmetadata.service;

import com.example.dbmetadata.util.JsonPatchWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MetadataExtractorServiceTest {

    @InjectMocks
    private MetadataExtractionService service;

    @Mock
    private DataSource dataSource;

    @Mock
    private JsonPatchWriter patchWriter;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData databaseMetaData;

    @Mock
    private ResultSet tableResultSet;

    @Mock
    private ResultSet dummyResultSet;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        assertNotNull(tableResultSet, "Mocked tableResultSet should not be null");
    }

    @Test
    public void testExtractAndWriteMetadata() throws Exception {
        when(databaseMetaData.getTables(null, null, "%", new String[]{"TABLE", "VIEW"}))
                .thenReturn(tableResultSet);

        when(tableResultSet.next()).thenReturn(true, false);
        when(tableResultSet.getString("TABLE_NAME")).thenReturn("EMPLOYEE");
        when(tableResultSet.getString("TABLE_TYPE")).thenReturn("TABLE");
        when(tableResultSet.getString("TABLE_SCHEM")).thenReturn("HR");

        PreparedStatement createStmt = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(createStmt);
        when(createStmt.executeQuery()).thenReturn(dummyResultSet);
        when(dummyResultSet.next()).thenReturn(true);
        when(dummyResultSet.getTimestamp(1)).thenReturn(Timestamp.valueOf("2023-01-01 00:00:00"));

        ResultSet fkResultSet = mock(ResultSet.class);
        when(databaseMetaData.getImportedKeys(null, null, "EMPLOYEE")).thenReturn(fkResultSet);
        when(fkResultSet.next()).thenReturn(true);
        when(fkResultSet.getString("PKTABLE_NAME")).thenReturn("DEPARTMENT");

        service.extractAndWriteMetadata();

        verify(patchWriter, times(1)).writePatch(argThat(metadata ->
                metadata.getName().equals("EMPLOYEE") &&
                metadata.getType().equals("TABLE") &&
                metadata.getOwner().equals("HR") &&
                "DEPARTMENT".equals(metadata.getParent())
        ));
    }

    @Test
    public void testEmptyTables() throws Exception {
        when(databaseMetaData.getTables(null, null, "%", new String[]{"TABLE", "VIEW"}))
                .thenReturn(tableResultSet);
        when(tableResultSet.next()).thenReturn(false);

        service.extractAndWriteMetadata();

        verify(patchWriter, never()).writePatch(any());
    }
    
    @Test
    public void testSQLExceptionDuringMetadataExtraction() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("DB not available"));

        try {
            service.extractAndWriteMetadata();
        } catch (SQLException e) {
            assert e.getMessage().equals("DB not available");
        }

        verify(patchWriter, never()).writePatch(any());
    }

    @Test
    public void testIOExceptionDuringPatchWrite() throws Exception {
        when(databaseMetaData.getTables(null, null, "%", new String[]{"TABLE", "VIEW"}))
                .thenReturn(tableResultSet);
        when(databaseMetaData.getImportedKeys(any(), any(), any())).thenReturn(tableResultSet);
        when(tableResultSet.next()).thenReturn(true, false);
        when(tableResultSet.getString("TABLE_NAME")).thenReturn("EMPLOYEE");
        when(tableResultSet.getString("TABLE_TYPE")).thenReturn("TABLE");
        when(tableResultSet.getString("TABLE_SCHEM")).thenReturn("HR");

        PreparedStatement createStmt = mock(PreparedStatement.class);
        ResultSet dummyResultSet = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(createStmt);
        when(createStmt.executeQuery()).thenReturn(dummyResultSet);
        when(dummyResultSet.next()).thenReturn(true);
        when(dummyResultSet.getTimestamp(1)).thenReturn(Timestamp.valueOf("2023-01-01 00:00:00"));

        doThrow(new IOException("Failed to write patch"))
                .when(patchWriter).writePatch(any());

        try {
            service.extractAndWriteMetadata();
        } catch (IOException e) {
            assert e.getMessage().equals("Failed to write patch");
        }
    }

}

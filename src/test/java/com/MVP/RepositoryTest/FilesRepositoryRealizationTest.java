package com.MVP.RepositoryTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.MVP.Models.File;
import com.MVP.Models.FileForDownloadDTO;
import com.MVP.Repository.FilesRepositoryRealization;

@SpringBootTest
@ActiveProfiles("test")
public class FilesRepositoryRealizationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // функции для тестов
    private void truncateTable(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("TRUNCATE TABLE ");
        sql.append(tableName);
        sql.append(" CASCADE");

        try {
            jdbcTemplate.execute(sql.toString());
        } catch (DataAccessException ex) {
            throw ex;
        }
    }
    
    private void setTestData() {
        //1 вставка
        String sql1 = "INSERT INTO files (id, filename, path, created_at) VALUES (?, ?, ?, ?)";
        UUID uuid1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String filename1 = "filename1";
        String path1 = "path1";
        LocalDate createdAt1 = LocalDate.of(2025, 1, 1);

        try{ 
            jdbcTemplate.update(sql1, uuid1, filename1, path1, createdAt1);
        } catch (DataAccessException ex) {
            throw ex;
        }

        //2 вставка
        String sql2 = "INSERT INTO files (id, filename, path, created_at) VALUES (?, ?, ?, ?)";
        UUID uuid2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        String filename2 = "filename2";
        String path2 = "path2";
        LocalDate createdAt2 = LocalDate.of(2025, 2, 2);

        try{ 
            jdbcTemplate.update(sql2, uuid2, filename2, path2, createdAt2);
        } catch (DataAccessException ex) {
            throw ex;
        }

        //3 вставка
        String sql3 = "INSERT INTO files (id, filename, path, created_at) VALUES (?, ?, ?, ?)";
        UUID uuid3 = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        String filename3 = "filename3";
        String path3 = "path3";
        LocalDate createdAt3 = LocalDate.of(2025, 3, 3);

        try{ 
            jdbcTemplate.update(sql3, uuid3, filename3, path3, createdAt3);
        } catch (DataAccessException ex) {
            throw ex;
        }
    }

    @Autowired
    private FilesRepositoryRealization fileRepo;

    @Test
    public void getAllFilesFromDataBaseTest() {
        //preparing
        try {
            truncateTable("files");
            setTestData();
        } catch (DataAccessException ex) {
            fail("ошибка при очистке таблицы или вставке в нее тестовых данных - " + ex.getMessage());
        }

        //act
        List<File> listOfFiles = fileRepo.getAllFilesFromDataBase();

        //assert
        UUID expectedUUID1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID expectedUUID2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID expectedUUID3 = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");

        File file1 = new File(expectedUUID1, "filename1", "path1", LocalDate.of(2025, 1, 1));
        File file2 = new File(expectedUUID2, "filename2", "path2", LocalDate.of(2025, 2, 2));
        File file3 = new File(expectedUUID3, "filename3", "path3", LocalDate.of(2025, 3, 3));
        List<File> expectedListOfFiles = List.of(file1, file2, file3);

        assertEquals(expectedListOfFiles, listOfFiles, "данные не совпадают");
    }

    @Test
    public void getFileByIdFromDataBaseTest() {
        //preparing
        try {
            truncateTable("files");
            setTestData();
        } catch (DataAccessException ex) {
            fail("ошибка при очистке таблицы или вставке в нее тестовых данных - " + ex.getMessage());
        }
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        //act
        List<File> listOfFiles = fileRepo.getFileByIdFromDataBase(uuid);

        //assert
        File expectedFile = new File(uuid, "filename1", "path1", LocalDate.of(2025, 1, 1));

        assertEquals(expectedFile, listOfFiles.get(0));
    }

    @Test
    public void getFileByFileNameFromDataBaseTest() {
        //preparing
        try {
            truncateTable("files");
            setTestData();
        } catch (DataAccessException ex) {
            fail("ошибка при очистке таблицы или вставке в нее тестовых данных - " + ex.getMessage());
        }
        String filename = "filename1";

        //act
        List<File> listOfFiles = fileRepo.getFileByFileNameFromDataBase(filename);

        //assert

        //1 вариант
        UUID expectedUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String expectedFilename = "filename1";
        String expectedPath = "path1";
        LocalDate expectedCreatedAt = LocalDate.of(2025, 1, 1);
        assertEquals(expectedUUID, listOfFiles.get(0).getId());
        assertEquals(expectedFilename, listOfFiles.get(0).getFilename());
        assertEquals(expectedPath, listOfFiles.get(0).getPath());
        assertEquals(expectedCreatedAt, listOfFiles.get(0).getCreatedAt());

        //2 вариант
        File expectedFile = new File(expectedUUID, expectedFilename, expectedPath, expectedCreatedAt);
        assertEquals(expectedFile, listOfFiles.get(0));
    }

    @Test
    public void getFileByPathFromDataBaseTest() {
        //preparing
        try {
            truncateTable("files");
            setTestData();
        } catch (DataAccessException ex) {
            fail("ошибка при очистке таблицы или вставке в нее тестовых данных - " + ex.getMessage());
        }
        String filepath = "path1";

        //act
        List<File> listOfFiles = fileRepo.getFileByPathFromDataBase(filepath);

        //assert
        UUID expectedUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String expectedFilename = "filename1";
        String expectedFilepath = "path1";
        LocalDate expectedCreatedAt = LocalDate.of(2025, 1, 1);

        File expectedFile = new File(expectedUUID, expectedFilename, expectedFilepath, expectedCreatedAt);

        assertEquals(expectedFile, listOfFiles.get(0));
    }

    @Test
    public void saveFileToDataBaseTest() {
        //preparing
        try {
            truncateTable("files");
        } catch (DataAccessException ex) {
            fail("не удалось очистить тестовую таблицу - " + ex.getMessage());
        }
        
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String extension = "во имя добра этот гнев";
        String pathPart = "123e4567-e89b-12d3-a456-426614174000".concat(extension);
        String filename = "во славу космоса на грех";

        //act
        File file = fileRepo.saveFileToDataBase(pathPart, filename, uuid);

        //assert
        UUID expectedUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String expectedFilename = "во славу космоса на грех";
        String expectedPathSubExtension = "во имя добра этот гнев";

        int filePathLength = file.getPath().length();
        int expectedPathSubExtensionLength = expectedPathSubExtension.length();

        assertEquals(expectedUUID, file.getId());
        assertEquals(expectedFilename, file.getFilename());
        assertEquals(expectedPathSubExtension, file.getPath().substring(filePathLength - expectedPathSubExtensionLength));
    }

    @Test
    public void getPathPartFromDataBaseTest_Success() {
        //preparing
        truncateTable("pathParts");
        FileForDownloadDTO fileForDownloadDTO = new FileForDownloadDTO("не давай паскудам забирать твое", "полюби меня как свежие ветра");
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO pathParts (id, filename, pathPart) VALUES (?, ?, ?)");
        Object[] args = List.of(UUID.fromString("f7c21b57-17de-4e29-9b5a-8a40fc41f43b"), 
        "полюби меня как свежие ветра", 
        "не давай паскудам забирать твое").toArray();
        try {
            jdbcTemplate.update(sql.toString(), args);
        } catch (DataAccessException ex) {
            fail();
        }

        //act
        FileForDownloadDTO fileForDownloadDTOFromDataBase = fileRepo.getPathPartFromDataBase(UUID.fromString("f7c21b57-17de-4e29-9b5a-8a40fc41f43b"));

        //assert
        if (!fileForDownloadDTOFromDataBase.getFilename().equals("полюби меня как свежие ветра") || !fileForDownloadDTOFromDataBase.getPathPart().equals("не давай паскудам забирать твое")) {
            fail();
        }
    }

    @Test
    public void addPathPartToDataBaseTest_Success() {
        //preparing
        truncateTable("pathParts");
        UUID uuid = UUID.fromString("f7c21b57-17de-4e29-9b5a-8a40fc41f43b");
        String filename = "filename";
        String pathPart = "pathPart";

        //act
        fileRepo.addPathPartToDataBase(uuid, pathPart, filename);

        //assert
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT filename, pathPart FROM pathParts");
        FileForDownloadDTO fileForDownloadDTOForCheck = null;
        try {
            fileForDownloadDTOForCheck = jdbcTemplate.queryForObject(sql.toString(), (rs, rowNum) -> new FileForDownloadDTO(
                rs.getString("pathPart"),
                rs.getString("filename")
            ));
        } catch (DataAccessException ex) {
            fail();
        }

        if (!fileForDownloadDTOForCheck.getPathPart().equals("pathPart") || !fileForDownloadDTOForCheck.getFilename().equals("filename")) {
            fail();
        }
    }
}


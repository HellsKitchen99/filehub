package com.MVP.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.MVP.Components.StorageProperties;
import com.MVP.Models.File;
import com.MVP.Models.FileForDownloadDTO;
import com.MVP.Repository.FilesRepository;
import com.MVP.Service.FileService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FileServiceTest {
    
    @Mock
    private FilesRepository fileRepo;

    @Mock
    private StorageProperties storageProperties;

    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @InjectMocks
    private FileService fileService;

    private void createUploadsDir(String path) throws IOException {
        Path pathDir = Paths.get(path);
        Files.createDirectories(pathDir);
    }

    private void deleteUploadsDir(String path) throws IOException {
        Path pathDir = Paths.get(path);
        if (!Files.exists(pathDir)) {
            return;
        }

        Files.walk(pathDir)
        .sorted((a, b) -> b.compareTo(a))
        .forEach(f -> {
            try {
                Files.delete(f);
            } catch (IOException ex) {
                throw new RuntimeException("не удалось удалить тестовую директорию - " + ex.getMessage());
            }
        });
    }

    private final String uploadsDir = "src/test/resources/uploads";

    @Test
    public void prepareFileForSavingTest_Success() {
        UUID fixedUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class, CALLS_REAL_METHODS))  {
            //preparing
            mockedUUID.when(UUID::randomUUID).thenReturn(fixedUUID);
            mockedUUID.when(() -> UUID.fromString(Mockito.anyString())).thenCallRealMethod();

            String filename = "test";
            String extension = ".txt";
            String pathPart = fixedUUID + extension;
            MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                filename + extension,
                "text/plane",
                "data".getBytes()
            );
            when(storageProperties.getUploadDir()).thenReturn(uploadsDir);
            try {
                createUploadsDir(uploadsDir);
            } catch (IOException ex) {
                fail("тест упал - не удалось создать тестовую директорию");
            }
            File fileForReturn = new File(fixedUUID, filename, pathPart, LocalDate.of(1, 1, 1));
            when(fileRepo.saveFileToDataBase(uploadsDir + "/" + pathPart, filename, fixedUUID)).thenReturn(fileForReturn);

            //act
            Map<String, Object> responseFromFileService = fileService.prepareFileForSaving(mockFile);

            //assert
            UUID expectedUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
            String expectedFilename = "test";
            String expectedExtension = ".txt";
            String expectedPathPart = expectedUUID.toString() + expectedExtension;
            File expectedFile = new File(expectedUUID, expectedFilename, expectedPathPart, LocalDate.of(1, 1, 1));
            assertEquals(expectedFile, responseFromFileService.get("savedFile"));

            Path dirPath = Paths.get(uploadsDir + "/" + expectedPathPart);
            String dataString = "";
            try {
                dataString = Files.readString(dirPath);
            } catch (IOException ex) {
                fail("не удалось прочитать данные из тестового файла - " + ex.getMessage());
            }
            String expectedDataString = "data";
            assertEquals(expectedDataString, dataString);

            try {
                deleteUploadsDir(uploadsDir);
            } catch (IOException ex) {
                fail("не удалось удалить тестовую директорию - " + ex.getMessage());
            }
        }
    }

    @Test
    public void prepareFileForSavingTest_Failure_BlankFilename() {
        //preparing
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "",
            "text/plane",
            "namedropping".getBytes()
        );

        //act + assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            fileService.prepareFileForSaving(mockFile);
        });
        assertEquals("The name of file is empty", ex.getMessage());
    }

    @Test
    public void prepareFileForSavingTest_Failure_FileError() {
        UUID fixedUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class);
        MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
            //preparing
            mockedUUID.when(UUID::randomUUID).thenReturn(fixedUUID);
            MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plane",
                "16138352".getBytes()
            );
            String uploadDir = "uploadDir";
            String filename = "test";
            File fileForReturn = new File(fixedUUID, filename, "path", LocalDate.of(1, 1, 1));
            when(fileRepo.saveFileToDataBase(uploadDir + "/" + fixedUUID.toString() + ".txt", filename, fixedUUID)).thenReturn(fileForReturn);
            when(storageProperties.getUploadDir()).thenReturn(uploadDir);
            mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), eq(StandardCopyOption.REPLACE_EXISTING))).thenThrow(new IOException("нас нет и никогда не было"));

            //act + assert
            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                fileService.prepareFileForSaving(mockFile);
            });
            assertEquals("ошибка при сохранении файла - нас нет и никогда не было", ex.getMessage());
        }
    }

    @Test
    public void getAllFilesTest_Success() {
        //preparing
        String downloadUrlPart = "/files/download/";

        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        String filename1 = "filename1";
        String path1 = "path1";
        File file1 = new File(uuid, filename1, path1, LocalDate.of(1, 1, 1));

        String filename2 = "filename2";
        String path2 = "path2";
        File file2 = new File(uuid, filename2, path2, LocalDate.of(2, 2, 2));

        String filename3 = "filename3";
        String path3 = "path3";
        File file3 = new File(uuid, filename3, path3, LocalDate.of(3, 3, 3));

        List<File> listOfFiles = List.of(file1, file2, file3);

        when(fileRepo.getAllFilesFromDataBase()).thenReturn(listOfFiles);

        //act
        List<Map<String, String>> result = fileService.getAllFiles();

        //assert
        List<Map<String, String>> expectedListOfMaps = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, String> mapa = new HashMap<>();
            mapa.put("id", uuid.toString());
            mapa.put("filename", "filename" + i);
            mapa.put("downloadUrl", downloadUrlPart + uuid.toString());
            expectedListOfMaps.add(mapa);
        }
        if (result.size() != 3) {
            fail("длина списка неверна");
        }
        assertEquals(expectedListOfMaps, result);
    }

    @Test
    public void getFileByIdTest_Success() {
        //preparing
        String downloadUrlPart = "/files/download/";
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        String filename1 = "filename1";
        File file1 = new File(uuid, filename1, "path1", LocalDate.of(1, 1, 1));

        String filename2 = "filename2";
        File file2 = new File(uuid, filename2, "path2", LocalDate.of(2, 2, 2));
        List<File> listOfFiles = List.of(file1, file2);
        when(fileRepo.getFileByIdFromDataBase(uuid)).thenReturn(listOfFiles);

        //act
        List<Map<String, String>> result = fileService.getFileById(uuid);

        //assert
        List<Map<String, String>> expectedListOfFiles = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Map<String, String> mapa = new HashMap<>();
            mapa.put("id", uuid.toString());
            mapa.put("filename", "filename" + i);
            mapa.put("downloadUrl", downloadUrlPart + uuid.toString());
            expectedListOfFiles.add(mapa);
        }
        if (result.size() != 2) {
            fail("длина списка неверна");
        }
        assertEquals(expectedListOfFiles, result);
    }

    @Test
    public void getFileByFileNameTest_Success() {
        //preparing
        String downloadUrlPart = "/files/download/";
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        String filename = "filename1";
        File file1 = new File(uuid, filename, "path1", LocalDate.of(1, 1, 1));

        List<File> listOfFiles = List.of(file1);
        when(fileRepo.getFileByFileNameFromDataBase(filename)).thenReturn(listOfFiles);

        //act
        List<Map<String, String>> result = fileService.getFileByFileName(filename);

        //assert
        List<Map<String, String>> expectedListOfFiles = new ArrayList<>();
        Map<String, String> mapa = new HashMap<>();
        mapa.put("id", uuid.toString());
        mapa.put("filename", filename);
        mapa.put("downloadUrl", downloadUrlPart + uuid.toString());
        expectedListOfFiles.add(mapa);
        if (result.size() != 1) {
            fail("длина списка неверна");
        }
        assertEquals(expectedListOfFiles, result);
    }

    @Test
    public void getFileByPathTest_Success() {
        //preparing
        String downloadUrlPart = "/files/download/";
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String path = "path";
        File file = new File(uuid, "filename", path, LocalDate.of(1, 1, 1));
        List<File> listOfFiles = List.of(file);
        when(fileRepo.getFileByPathFromDataBase(path)).thenReturn(listOfFiles);

        //act
        List<Map<String, String>> result = fileService.getFileByPath(path);

        //assert
        List<Map<String, String>> expectedListOfFiles = new ArrayList<>();
        Map<String, String> mapa = new HashMap<>();
        mapa.put("id", uuid.toString());
        mapa.put("filename", "filename");
        mapa.put("downloadUrl", downloadUrlPart + uuid.toString());
        expectedListOfFiles.add(mapa);
        if (result.size() != 1) {
            fail("длина списка неверна");
        }
        assertEquals(expectedListOfFiles, result);
    }

    @Test
    public void getFileFromRepositoryDirectoryTest_Success() {
        //preparing
        when(storageProperties.getUploadDir()).thenReturn(uploadsDir);
        try {
            createUploadsDir(uploadsDir);
        } catch (IOException ex) {
            fail();
        }
        UUID uuid = UUID.fromString("f7c21b57-17de-4e29-9b5a-8a40fc41f43b");
        String filename = "filename";
        String pathPart = uuid.toString();
        FileForDownloadDTO fileForDownloadDTO = new FileForDownloadDTO(pathPart, filename);
        when(fileRepo.getPathPartFromDataBase(uuid)).thenReturn(fileForDownloadDTO);
        String fullFilepath = uploadsDir + "/" + pathPart;
        Path filepath = Paths.get(fullFilepath);
        byte[] dataForWritingToFileForCheck = "test data".getBytes();
        try {
            Files.write(filepath, dataForWritingToFileForCheck, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            byte[] bufForCheckTestFile = Files.readAllBytes(filepath);
            System.out.println(">>> " + new String(bufForCheckTestFile, StandardCharsets.UTF_8) + " <<<");
        } catch (IOException ex) {
            fail();
        }

        //act
        Map<String, Object> responseFromFileService = fileService.getFileFromRepositoryDirectory(uuid);

        //assert
        if (!responseFromFileService.get("filename").equals("filename")) {
            fail();
        }
        String dataForCheck = "test data";
        String fileFromFileServiceAsString = responseFromFileService.get("file (bytes)").toString();
        if (fileFromFileServiceAsString.equals(dataForCheck)) {
            fail();
        }
    }

    @Test
    public void getFileFromRepositoryDirectoryTest_Failure() {
        //preparing
        UUID token = UUID.fromString("f7c21b57-17de-4e29-9b5a-8a40fc41f43b");
        FileForDownloadDTO fileForDownloadDTO = new FileForDownloadDTO("pathPart", "filename");
        when(fileRepo.getPathPartFromDataBase(token)).thenReturn(fileForDownloadDTO);

        //act + assert
        RuntimeException result = assertThrows(RuntimeException.class, () -> {
            fileService.getFileFromRepositoryDirectory(token);
        });
        System.out.println(">>> " + result + " <<<");
        if (!result.getMessage().contains("не удалось прочитать файл - ")) {
            fail();
        }
    }
}

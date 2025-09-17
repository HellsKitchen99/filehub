package com.MVP.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.MVP.Components.StorageProperties;
import com.MVP.Models.File;
import com.MVP.Models.FileForDownloadDTO;
import com.MVP.Repository.FilesRepository;

@Service
public class FileService {

    private final String downloadUrlPart = "/files/download/";

    private FilesRepository fileRepo;
    private StorageProperties storageProperties;

    public FileService(FilesRepository fileRepo, StorageProperties storageProperties) {
        this.fileRepo = fileRepo;
        this.storageProperties = storageProperties;
    }
    
    //сохранение файла
    public Map<String, Object> prepareFileForSaving(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        //получение директории для хранения
        String uploadDir = storageProperties.getUploadDir();

        String fullFilename = file.getOriginalFilename();

        //проверка на пустоту имени
        if (fullFilename == null || fullFilename.isBlank()) {
            throw new IllegalArgumentException("The name of file is empty");
        }

        int lastDot = fullFilename.lastIndexOf(".");
        String filename = "";
        String extension = "";
        if (lastDot != -1) {
            filename = fullFilename.substring(0, lastDot);
            extension = fullFilename.substring(lastDot + 1);
        } else {
            filename = fullFilename;
        }
        UUID uuid = UUID.randomUUID();
        String pathPart = uuid.toString() + (extension.isBlank() ? "" : "." + extension);
        File savedFile = fileRepo.saveFileToDataBase(uploadDir + "/" + pathPart, filename, uuid);
        response.put("uuid", uuid);

        //сохранение в Uploads
        Path uploadDirPath = Paths.get(uploadDir);
        Path filePath = uploadDirPath.resolve(pathPart);

        fileRepo.addPathPartToDataBase(uuid, pathPart, filename);
        
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("ошибка при сохранении файла - " + ex.getMessage());
        }
        response.put("savedFile", savedFile);

        return response;
    }

    //скачивание файла из папки хранения
    public Map<String, Object> getFileFromRepositoryDirectory(UUID token) {
        String uploadDir = storageProperties.getUploadDir();
        FileForDownloadDTO fileForDownloadDTO = fileRepo.getPathPartFromDataBase(token);
        
        Map<String, Object> response = new HashMap<>();
        String filename = fileForDownloadDTO.getFilename();
        String filePath = uploadDir + "/" + fileForDownloadDTO.getPathPart(); 
        byte[] fileBytes = null;
        try {
            fileBytes = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException ex) {
            throw new RuntimeException("не удалось прочитать файл - " + ex.getMessage());
        }
        response.put("filename", filename);
        response.put("file (bytes)", fileBytes);
        return response;
    }

    //получение файла
    //получение всех файлов
    public List<Map<String, String>> getAllFiles() {
        List<Map<String, String>> files = new ArrayList<>();
        List<File> listOfFiles = fileRepo.getAllFilesFromDataBase();
        for (int i = 0; i < listOfFiles.size(); i++) {
            Map<String, String> file = new HashMap<>();
            String uuid = listOfFiles.get(i).getId().toString();
            String filename = listOfFiles.get(i).getFilename();
            String downloadUrl = downloadUrlPart + uuid.toString();
            file.put("id", uuid.toString());
            file.put("filename", filename);
            file.put("downloadUrl", downloadUrl);
            files.add(file);
        }
        return files;
    }

    //получение файла по id
    public List<Map<String, String>> getFileById(UUID uuid) {
        List<Map<String, String>> files = new ArrayList<>();
        List<File> listOfFiles = fileRepo.getFileByIdFromDataBase(uuid);
        for (int i = 0; i < listOfFiles.size(); i++) {
            Map<String, String> file = new HashMap<>();
            String uuidString = listOfFiles.get(i).getId().toString();
            String filename = listOfFiles.get(i).getFilename();
            String downloadUrl = downloadUrlPart + uuidString;
            file.put("id", uuidString);
            file.put("filename", filename);
            file.put("downloadUrl", downloadUrl);
            files.add(file);
        }
        return files;
    }

    //получение файла по имени
    public List<Map<String, String>> getFileByFileName(String filename) {
        List<Map<String, String>> files = new ArrayList<>();
        List<File> listOfFiles = fileRepo.getFileByFileNameFromDataBase(filename);
        for (int i = 0; i < listOfFiles.size(); i++) {
            Map<String, String> file = new HashMap<>();
            String uuid = listOfFiles.get(i).getId().toString();
            String filenameString = listOfFiles.get(i).getFilename();
            String downloadUrl = downloadUrlPart + uuid.toString();
            file.put("id", uuid.toString());
            file.put("filename", filenameString);
            file.put("downloadUrl", downloadUrl);
            files.add(file);
        }
        return files;
    }

    //получение файла по пути
    public List<Map<String, String>> getFileByPath(String path) {
        List<Map<String, String>> files = new ArrayList<>();
        List<File> listOfFiles = fileRepo.getFileByPathFromDataBase(path);
        for (int i = 0; i < listOfFiles.size(); i++) {
            Map<String, String> file = new HashMap<>();
            String uuid = listOfFiles.get(i).getId().toString();
            String filename = listOfFiles.get(i).getFilename();
            String downloadUrl = downloadUrlPart + uuid.toString();
            file.put("id", uuid.toString());
            file.put("filename", filename);
            file.put("downloadUrl", downloadUrl);
            files.add(file);
        }
        return files;
    }

    //функции
    //получение файла в качестве Resource
    private Resource getFileAsResource(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Filepath - " + path + " - doesn't exist");
        }

        return new UrlResource(path.toUri());
    }
}

package com.MVP.Repository;

import java.util.UUID;
import java.util.List;
import com.MVP.Models.File;
import com.MVP.Models.FileForDownloadDTO;

import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository {
    List<File> getAllFilesFromDataBase();
    List<File> getFileByIdFromDataBase(UUID uuid);
    List<File> getFileByFileNameFromDataBase(String filename);
    List<File> getFileByPathFromDataBase(String path);
    File saveFileToDataBase(String pathPart, String filename, UUID uuid);
    FileForDownloadDTO addPathPartToDataBase(UUID uuid, String pathPart, String filename);
    FileForDownloadDTO getPathPartFromDataBase(UUID token);
}

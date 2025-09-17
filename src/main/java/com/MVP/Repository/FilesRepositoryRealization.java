package com.MVP.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.MVP.Models.File;
import com.MVP.Models.FileForDownloadDTO;

@Repository
public class FilesRepositoryRealization implements FilesRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public FilesRepositoryRealization(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //получение файлов
    //получить все файлы
    public List<File> getAllFilesFromDataBase() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM files");

        List<File> listOfFiles = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> 
        new File(rs.getObject("id", UUID.class), 
        rs.getString("filename"), 
        rs.getString("path"), 
        rs.getObject("created_at", LocalDate.class)));

        return listOfFiles;
    }

    //получить файл по id
    public List<File> getFileByIdFromDataBase(UUID id) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM files WHERE id = ?");

        List<Object> args = new ArrayList<>();
        args.add(id);

        List<File> listOfFiles = jdbcTemplate.query(sql.toString(), args.toArray(), (RowMapper<File>) (rs, rowNum) -> 
        new File(rs.getObject("id", UUID.class), 
        rs.getString("filename"), 
        rs.getString("path"), 
        rs.getObject("created_at", LocalDate.class)));

        return listOfFiles;
    }

    //получить файл по имени
    public List<File> getFileByFileNameFromDataBase(String filename) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM files WHERE filename = ?");

        List<Object> args = new ArrayList<>();
        args.add(filename);

        List<File> listOfFiles = jdbcTemplate.query(sql.toString(), args.toArray(), (RowMapper<File>) (rs, rowNum) -> 
        new File(rs.getObject("id", UUID.class), 
        rs.getString("filename"), 
        rs.getString("path"), 
        rs.getObject("created_at", LocalDate.class)));

        return listOfFiles;
    }


    //получить файл по пути
    public List<File> getFileByPathFromDataBase(String path) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM files WHERE path = ?");

        List<Object> args = new ArrayList<>();
        args.add(path);

        List<File> listOfFiles = jdbcTemplate.query(sql.toString(), args.toArray(), (RowMapper<File>) (rs, rowNum) -> 
        new File(rs.getObject("id", UUID.class), 
        rs.getString("filename"), 
        rs.getString("path"), 
        rs.getObject("created_at", LocalDate.class)));

        return listOfFiles;
    }

    //сохранение файлов
    public File saveFileToDataBase(String pathPart, String filename, UUID uuid) {
        StringBuilder sql = new StringBuilder();
        String fullPath = pathPart;
        sql.append("INSERT INTO files (id, filename, path, created_at) VALUES (?, ?, ?, ?)");

        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(sql.toString(), uuid, filename, fullPath, now);

        File file = new File(uuid, filename, fullPath, LocalDate.now());
        return file;
    }

    //сохранение pathPart
    public FileForDownloadDTO addPathPartToDataBase(UUID uuid, String pathPart, String filename) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO pathParts (id, filename, pathPart) VALUES (?, ?, ?)");
        Object[] args = List.of(uuid, filename, pathPart).toArray();
        try {
            jdbcTemplate.update(sql.toString(), args);
        } catch (DataAccessException ex) {
            throw new RuntimeException("не удалось сохранить pathPart в базу - " + ex.getMessage());
        }
        FileForDownloadDTO fileForDownloadDTO = new FileForDownloadDTO(pathPart, filename);
        return fileForDownloadDTO;
    }

    //получение pathPart
    public FileForDownloadDTO getPathPartFromDataBase(UUID token) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pathPart, filename FROM pathParts WHERE id = ?");

        FileForDownloadDTO fileForDownloadDTO = jdbcTemplate.queryForObject(sql.toString(), 
        (rs, rowNum) -> new FileForDownloadDTO(
            rs.getString("pathPart"),
            rs.getString("filename")
        ), 
        token);

        return fileForDownloadDTO;
    }
}

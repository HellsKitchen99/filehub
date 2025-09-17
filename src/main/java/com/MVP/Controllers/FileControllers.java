package com.MVP.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.MVP.Service.FileService;
import com.MVP.Service.QRCodeService;
import com.google.zxing.WriterException;

@RestController
@RequestMapping("/files")
public class FileControllers {

    @Autowired
    QRCodeService qr;

    @Autowired
    private FileService fileService;

    public FileControllers(FileService fileService, QRCodeService qr) {
        this.fileService = fileService;
        this.qr = qr;
    }
    
    //эндпоинт для сохранения файла
    @PostMapping("/upload")
    public ResponseEntity<?> UploadFileController(@RequestParam("file") MultipartFile multipartFile) {
        Map<String, String> response = new HashMap<>();
        Map<String, Object> responseFromFileService = fileService.prepareFileForSaving(multipartFile);
        UUID uuid = (UUID) responseFromFileService.get("uuid");
        response.put("answer", "file has been saved");
        response.put("tokenForDownload", uuid.toString());
        return ResponseEntity.status(200).body(response);
    }

    //эндпоинт для получения куар кода по токену
    @GetMapping("/qr/{token}")
    public ResponseEntity<byte[]> GetQRCodeByTokenController(@PathVariable("token") String token) throws IOException, WriterException{
        String url = "http://localhost:5555/files/download/" + token;
        byte[] imageInBytes = qr.generateQRCodeImage(url, 300, 300);
        return ResponseEntity.status(200).contentType(MediaType.IMAGE_PNG).body(imageInBytes);
    }

    //эндпоинт для скачивания файла
    @GetMapping("/download/{token}")
    public ResponseEntity<?> DownloadFileByQRCodeController(@PathVariable("token") UUID token) {
        Map<String, Object> responseFromFileService = fileService.getFileFromRepositoryDirectory(token);
        byte[] file = (byte[]) responseFromFileService.get("file (bytes)");
        String filename = (String) responseFromFileService.get("filename");
        return ResponseEntity.status(200)
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        .body(file);
    }

    /*@PostMapping("/download/{id}")
    public ResponseEntity<?> DownloadFileController(@PathVariable("id") UUID id) {
        StorageProperties storageProperties = new StorageProperties();
        String uploadDir = storageProperties.getUploadDir();
        String filename = uploadDir + id.toString();
        Path filepath = Paths.get(filename);

        Map<String, Object> response = new HashMap<>();

        if (!Files.exists(filepath)) {
            response.put("error", "0 files found");
            return ResponseEntity.status(404).body(response);
        }

        ResponseEntity.status(200).contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filepath.getFileName() + "\"");
    }

    private String determineExtension(UUID uuid, String uploadDir) throws IOException {
        Path uploadsDir = Paths.get(uploadDir);

        try (Stream<Path> files = Files.list(uploadsDir)) {
            Optional<Path> match = files.filter(Files::isRegularFile)
            .filter(f -> f.getFileName().toString().startsWith(uuid.toString()))
            .findFirst();

            if (match.isEmpty()) {
                throw new FileNotFoundException("File with " + uuid.toString() + " not found");
            }

            String filename = match.get().getFileName().toString();
        }
    }*/
}

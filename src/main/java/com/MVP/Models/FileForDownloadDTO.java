package com.MVP.Models;

import lombok.Data;

@Data
public class FileForDownloadDTO {
    
    public FileForDownloadDTO(String pathPart, String filename) {
        this.pathPart = pathPart;
        this.filename = filename;
    }

    private String pathPart;
    private String filename;
}

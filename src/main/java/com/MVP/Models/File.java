package com.MVP.Models;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class File {
    
    public File(UUID id, String filename, String path, LocalDate createdAt) {
        this.id = id;
        this.filename = filename;
        this.path = path;
        this.createdAt = createdAt;
    }

    private UUID id;
    private String filename;
    private String path;
    private LocalDate createdAt;
}

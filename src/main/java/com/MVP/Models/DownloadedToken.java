package com.MVP.Models;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class DownloadedToken {
    
    public DownloadedToken(UUID token, UUID fileId, LocalDate expiresAt, boolean used) {
        this.token = token;
        this.fileId = fileId;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    private UUID token;
    private UUID fileId;
    private LocalDate expiresAt;
    private boolean used;
}

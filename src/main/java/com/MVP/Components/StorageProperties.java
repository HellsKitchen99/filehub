package com.MVP.Components;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
    
    //в properties у нас upload-dir, все это преобразуется в uploadDir и тут уже ищется соответсвующее поле и ставится в него
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    //сетер обязателен, если поле private
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}

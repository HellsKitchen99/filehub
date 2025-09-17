package com.MVP.Components;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StorageInitializer implements CommandLineRunner {
    
    private final StorageProperties properties;

    public StorageInitializer(StorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(String... args)  {
        //делаем из строки директорию
        Path path = Paths.get(properties.getUploadDir());

        //пытаемся создать директорию
        try {
            Files.createDirectories(path);
            System.out.println("Директория для логов готова");
        } catch (IOException ex) {
            System.out.printf("Не удалось создать директорию для загрузки файлов - %v\n", ex);
            throw new RuntimeException("Не удалось создать папку загрузки - ", ex);
        }
    }
}

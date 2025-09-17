package com.MVP;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.MVP.Components.StorageInitializer;
import com.MVP.Components.StorageProperties;
import com.MVP.Repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
public class StorageInitializerTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StorageProperties properties;

    @Autowired StorageInitializer initializer;

    private Path path;

    //удаление файла перед проверкой
    private void setup() {
        path = Paths.get(properties.getUploadDir());
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new RuntimeException("не удалось удалить тестовую директорию перед тестом - " + ex);
        }
    }

    @Test
    public void runTest() {
        //preparing
        setup();
        assertFalse(Files.exists(path), "Папка уже существует");

        //act
        initializer.run(null);

        //assert
        assertTrue(Files.exists(path), "Папка не была создана");
        assertTrue(Files.isDirectory(path), "Создан не каталог");
        cleanup();
    }

    private void cleanup() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new RuntimeException("не удалось удалить тестовую директорию после теста");
        }
    }
}

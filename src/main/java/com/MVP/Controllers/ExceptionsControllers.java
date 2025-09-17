package com.MVP.Controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.google.zxing.WriterException;

import jakarta.servlet.ServletException;

@RestControllerAdvice
public class ExceptionsControllers {
    
    //ошибка с базой данных
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> DataAccessExceptionController(DataAccessException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    //пришел неверный заголов для файла
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> MissingServletRequestPartExceptionController(MissingServletRequestPartException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    //пришел файл без имени(null)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> NullPointerExceptionController(NullPointerException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    //передан файл без точки и расширения
    @ExceptionHandler(StringIndexOutOfBoundsException.class)
    public ResponseEntity<?> StringIndexOutOfBoundsExceptionController(StringIndexOutOfBoundsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    //обработка IOException, пока что только в сервисе prepareForSaving и getFileFromRepositoryDirectory
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> IOExceptionController(IOException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    //пустое имя файла в мултипарте
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> IllegalArgumentExceptionController(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    //ловля RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> RuntimeExceptionController(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    //ловля WriterException (при генерации QR кода)
    @ExceptionHandler(WriterException.class)
    public ResponseEntity<?> WriterExceptionController(WriterException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    //ловля ServletException
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<?> ServletExceptionController(ServletException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    //ловля Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> ExceptionController(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    //ловля HttpMessageNotReadableException (битый JSON в реквесте контроллеру)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> HttpMessageNotReadableExceptionController(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }
}

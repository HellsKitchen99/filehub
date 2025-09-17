package com.MVP.ControllersTest;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.MVP.Config.JwtUtil;
import com.MVP.Controllers.FileControllers;
import com.MVP.Service.CustomUserDetailsService;
import com.MVP.Service.FileService;
import com.MVP.Service.QRCodeService;
import com.google.zxing.WriterException;
import com.MVP.Models.File;

@WebMvcTest(FileControllers.class)
@AutoConfigureMockMvc(addFilters = false)
public class FileControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRCodeService qrCodeService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private FileService fileService;

    @Test
    public void UploadFileControllerTest_Success() {
        //preparing
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "data".getBytes()
        );

        UUID uuid = UUID.fromString("f7c21b57-17de-4e29-9b5a-8a40fc41f43b");
        String filename = "test";
        String path = "path";
        LocalDate createdAt = LocalDate.now();

        File savedFile = new File(uuid, filename, path, createdAt);

        Map<String, Object> responseFromFileService = new HashMap<>();
        responseFromFileService.put("uuid", uuid);
        responseFromFileService.put("savedFile", savedFile);

        when(fileService.prepareFileForSaving(mockFile)).thenReturn(responseFromFileService);

        //act
        MvcResult result = null;
        try {
            result = mockMvc.perform(multipart("/files/upload").file(mockFile))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.answer").isNotEmpty())
            .andExpect(jsonPath("$.tokenForDownload").isNotEmpty())
            .andReturn();
        } catch (Exception ex) {
            fail();
        }
        
        //assert
        String resultAsString = "";
        try {
            resultAsString = result.getResponse().getContentAsString();
        } catch (UnsupportedEncodingException ex) {
            fail();
        }
        if (!resultAsString.contains("file has been saved") || !resultAsString.contains(uuid.toString())) {
            fail();
        }
    }

    @Test
    public void GetQRCodeByTokenControllerTest_Success() {
        //preparing
        String token = "token";
        byte[] imageInBytes = "image in bytes".getBytes();
        try {
            when(qrCodeService.generateQRCodeImage(anyString(), anyInt(), anyInt())).thenReturn(imageInBytes);
        } catch (IOException ex) {
            fail();
        } catch (WriterException ex) {
            fail();
        }

        //act
        MvcResult result = null;
        try {
            result = mockMvc.perform(get("/files/qr/" + token))
            .andReturn();
        } catch (Exception ex) {
            fail();
        }

        //assert
        byte[] resultAsByteArray = result.getResponse().getContentAsByteArray();
        boolean isEqualed = Arrays.equals(imageInBytes, resultAsByteArray);
        if (isEqualed == false) {
            fail();
        }
    }

    @Test
    public void DownloadFileByQRCodeControllerTest_Success() {
        //preparing
        UUID token = UUID.fromString("f7c21b57-17de-4e29-9b5a-8a40fc41f43b");
        Map<String, Object> responseFromFileService = new HashMap<>();
        String filename = "астронавт";
        byte[] fileBytes = "священный рэйв".getBytes();
        responseFromFileService.put("filename", filename);
        responseFromFileService.put("file (bytes)", fileBytes);
        when(fileService.getFileFromRepositoryDirectory(token)).thenReturn(responseFromFileService);

        //act
        MvcResult result = null;
        try {
            result = mockMvc.perform(get("/files/download/" + token.toString()))
            .andExpect(status().is(200))
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"астронавт\""))
            .andReturn();
        } catch (Exception ex) {
            fail();
        }

        //assert
        byte[] resultAsByteArray = result.getResponse().getContentAsByteArray();
        boolean isEqualed = Arrays.equals(fileBytes, resultAsByteArray);
        if (isEqualed == false) {
            fail();
        }
    }
}
package com.MVP.ServiceTest;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.MVP.Service.QRCodeService;
import com.google.zxing.WriterException;

@ExtendWith(MockitoExtension.class)
public class QRCodeServiceTest {

    @InjectMocks
    private QRCodeService qrCodeService;
    
    @Test
    public void generateQRCodeImageTest_Success() {
        //preparing
        String text = "in my restless dreams";
        int width = 300;
        int height = 300;

        //act
        byte[] result = null;
        try {
            result = qrCodeService.generateQRCodeImage(text, width, height);
        } catch (IOException ex) {
            fail();
        } catch (WriterException ex) {
            fail();
        }

        //assert
        String resultAsString = new String(result);
        if (resultAsString == null || resultAsString.isEmpty() || !resultAsString.contains("PNG")) {
            fail("тест упал - пришел неверный ответ");
        }
    }
}

package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils.EncryptionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = ApplicationQr.class, properties = {
        "spring.application.name=amaterasu-qr-backend-test",
        "qr.encryption.password=testpassword",
        "qr.encryption.salt=1234567890abcdef"
})
@ActiveProfiles("test")
public class EncryptionTest {
    @Autowired
    private EncryptionUtil encryptionUtil;

    @Test
    public void testEncryption() {
        String originalText = "test123";
        String encrypted = encryptionUtil.encrypt(originalText);
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);

        String decrypted = encryptionUtil.decrypt(encrypted);
        assertEquals(originalText, decrypted);
    }
}
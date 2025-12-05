package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils.EncryptionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EncryptionTest {

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Test
    void testEncryption() {
        String original = "12345_2024-01-15T10:30:00_BANK_PAYED_PENDING";

        String encrypted = encryptionUtil.encrypt(original);
        System.out.println("Encriptado: " + encrypted);

        String decrypted = encryptionUtil.decrypt(encrypted);
        System.out.println("Desencriptado: " + decrypted);

        assert original.equals(decrypted);
    }
}
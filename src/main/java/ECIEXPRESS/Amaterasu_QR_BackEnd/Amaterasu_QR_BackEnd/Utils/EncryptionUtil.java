package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service
public class EncryptionUtil {

    @Value("${qr.encryption.password:ECIEXPRESS_QR_PASSWORD_2025}")
    private String password;

    @Value("${qr.encryption.salt:1234567890abcdef}")
    private String salt;

    private TextEncryptor encryptor;

    @PostConstruct
    public void init() {
        this.encryptor = Encryptors.text(password, salt);
    }

    public String encrypt(String text) {
        return encryptor.encrypt(text);
    }

    public String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText);
    }
}
package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@UtilityClass
public class SimpleEncryptionUtil {
    @Value("${qr.encryption.password:ECIEXPRESS_QR_KEY_2024}") String password;
    @Value("${qr.encryption.salt:QR_SALT_123}") String salt;
    private final TextEncryptor encryptor = Encryptors.text(password, salt);


    public String encrypt(String text) {
        return encryptor.encrypt(text);
    }

    public String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText);
    }
}
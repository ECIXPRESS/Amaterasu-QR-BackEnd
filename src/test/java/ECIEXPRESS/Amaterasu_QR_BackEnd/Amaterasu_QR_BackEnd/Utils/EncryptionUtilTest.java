package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EncryptionUtilTest {

    @InjectMocks
    private EncryptionUtil encryptionUtil;

    private final String password = "ECIEXPRESS_QR_PASSWORD_2025";
    private final String salt = "1234567890abcdef";

    @BeforeEach
    void setUp() {
        // Manually set the values since @Value doesn't work in unit tests
        ReflectionTestUtils.setField(encryptionUtil, "password", password);
        ReflectionTestUtils.setField(encryptionUtil, "salt", salt);

        // Manually call init() since @PostConstruct doesn't run in tests
        encryptionUtil.init();
    }

    @Test
    void testEncryptAndDecrypt_Success() {
        // Arrange
        String originalText = "Hello, World!";

        // Act
        String encrypted = encryptionUtil.encrypt(originalText);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // Assert
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    void testEncrypt_SameText_DifferentEncryption() {
        // Arrange
        String text = "Test message";

        // Act
        String encrypted1 = encryptionUtil.encrypt(text);
        String encrypted2 = encryptionUtil.encrypt(text);

        // Assert - AES with random IV should produce different results
        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    void testDecrypt_InvalidText() {
        // Arrange
        String invalidEncryptedText = "not_a_valid_encrypted_string";

        // Act & Assert
        assertThrows(Exception.class, () -> encryptionUtil.decrypt(invalidEncryptedText));
    }

    @Test
    void testEncrypt_EmptyString() {
        // Arrange
        String emptyText = "";

        // Act
        String encrypted = encryptionUtil.encrypt(emptyText);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // Assert
        assertEquals(emptyText, decrypted);
    }

    @Test
    void testEncrypt_SpecialCharacters() {
        // Arrange
        String specialText = "Test_12345_@#$%_2024-01-15T10:30:00.000Z";

        // Act
        String encrypted = encryptionUtil.encrypt(specialText);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // Assert
        assertEquals(specialText, decrypted);
    }

    @Test
    void testEncrypt_NullInput() {
        // Act & Assert
        assertThrows(Exception.class, () -> encryptionUtil.encrypt(null));
    }
}
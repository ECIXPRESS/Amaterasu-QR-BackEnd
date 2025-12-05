package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Services;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port.ReceiptProvider;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.CreateQrCodeRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.ValidateQRRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrResponses.CreateQrCodeResponse;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QRServiceTest {

    @Mock
    private ReceiptProvider receiptProvider;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private QRService qrService;

    private final String validQRString = "12345_2024-01-15T10:30:00.000Z_2024-01-15T10:29:00.000Z_BANK_PAYED_PENDING";
    private final String encryptedQR = "encrypted_qr_string_123";
    private final String orderId = "12345";

    @BeforeEach
    void setUp() {
        // Common setup if needed
    }

    @Test
    void testValidateQrCode_Success() {
        // Arrange
        ValidateQRRequest request = new ValidateQRRequest(encryptedQR);

        when(encryptionUtil.decrypt(encryptedQR)).thenReturn(validQRString);
        doNothing().when(receiptProvider).updateToPayed(orderId);
        doNothing().when(receiptProvider).updateToDelivered(orderId);

        // Act
        boolean result = qrService.ValidateQrCode(request);

        // Assert
        assertTrue(result);
        verify(encryptionUtil, times(1)).decrypt(encryptedQR);
        verify(receiptProvider, times(1)).updateToPayed(orderId);
        verify(receiptProvider, times(1)).updateToDelivered(orderId);
    }

    @Test
    void testValidateQrCode_DecryptionFails() {
        // Arrange
        ValidateQRRequest request = new ValidateQRRequest(encryptedQR);

        when(encryptionUtil.decrypt(encryptedQR))
                .thenThrow(new RuntimeException("Decryption failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> qrService.ValidateQrCode(request));

        assertTrue(exception.getMessage().contains("Error validating QR code"));
        verify(encryptionUtil, times(1)).decrypt(encryptedQR);
        verify(receiptProvider, never()).updateToPayed(anyString());
        verify(receiptProvider, never()).updateToDelivered(anyString());
    }

    @Test
    void testValidateQrCode_InvalidQRFormat() {
        // Arrange
        ValidateQRRequest request = new ValidateQRRequest(encryptedQR);
        String invalidQR = "invalid_format";

        when(encryptionUtil.decrypt(encryptedQR)).thenReturn(invalidQR);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> qrService.ValidateQrCode(request));

        assertTrue(exception.getMessage().contains("Error validating QR code"));
        verify(encryptionUtil, times(1)).decrypt(encryptedQR);
        verify(receiptProvider, never()).updateToPayed(anyString());
        verify(receiptProvider, never()).updateToDelivered(anyString());
    }

    @Test
    void testCreateQrCode_Success() {
        // Arrange
        CreateQrCodeRequest request = new CreateQrCodeRequest(orderId);

        when(receiptProvider.getQrCodeByOrderId(orderId)).thenReturn(validQRString);
        when(encryptionUtil.encrypt(validQRString)).thenReturn(encryptedQR);

        // Act
        CreateQrCodeResponse response = qrService.createQrCode(request);

        // Assert
        assertNotNull(response);
        assertEquals(encryptedQR, response.encodedQRCode());
        verify(receiptProvider, times(1)).getQrCodeByOrderId(orderId);
        verify(encryptionUtil, times(1)).encrypt(validQRString);
    }

    @Test
    void testCreateQrCode_ReceiptProviderFails() {
        // Arrange
        CreateQrCodeRequest request = new CreateQrCodeRequest(orderId);

        when(receiptProvider.getQrCodeByOrderId(orderId))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> qrService.createQrCode(request));

        assertTrue(exception.getMessage().contains("Error creating QR code for order"));
        verify(receiptProvider, times(1)).getQrCodeByOrderId(orderId);
        verify(encryptionUtil, never()).encrypt(anyString());
    }

    @Test
    void testCreateQrCode_EncryptionFails() {
        // Arrange
        CreateQrCodeRequest request = new CreateQrCodeRequest(orderId);

        when(receiptProvider.getQrCodeByOrderId(orderId)).thenReturn(validQRString);
        when(encryptionUtil.encrypt(validQRString))
                .thenThrow(new RuntimeException("Encryption failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> qrService.createQrCode(request));

        assertTrue(exception.getMessage().contains("Error creating QR code for order"));
        verify(receiptProvider, times(1)).getQrCodeByOrderId(orderId);
        verify(encryptionUtil, times(1)).encrypt(validQRString);
    }

    @Test
    void testCreateQrCode_InvalidQRFromProvider() {
        // Arrange
        CreateQrCodeRequest request = new CreateQrCodeRequest(orderId);
        String invalidQR = "invalid_qr_from_provider";

        when(receiptProvider.getQrCodeByOrderId(orderId)).thenReturn(invalidQR);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> qrService.createQrCode(request));

        assertTrue(exception.getMessage().contains("Error creating QR code for order"));
        verify(receiptProvider, times(1)).getQrCodeByOrderId(orderId);
        verify(encryptionUtil, never()).encrypt(anyString());
    }
}
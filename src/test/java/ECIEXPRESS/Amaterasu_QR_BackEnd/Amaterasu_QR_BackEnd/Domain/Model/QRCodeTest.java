package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model;


import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.Enums.OrderStatus;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.Enums.PaymentMethodType;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.Enums.ReceiptStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QRCodeTest {

    private QRCode qrCode;

    @BeforeEach
    void setUp() {
        qrCode = new QRCode();
    }

    @Test
    void testValidQRCode_BANK_PAYED_PENDING() throws Exception {
        // Arrange
        String validQR = "12345_2024-01-15T10:30:00.000Z_2024-01-15T10:29:00.000Z_BANK_PAYED_PENDING";

        // Act
        qrCode.validateQrCode(validQR);

        // Assert
        assertEquals("12345", qrCode.getOrderId());
        assertEquals("2024-01-15T10:30:00.000Z", qrCode.getReceiptGeneratedDate());
        assertEquals("2024-01-15T10:29:00.000Z", qrCode.getPaymentProcessedAt());
        assertEquals(PaymentMethodType.BANK, qrCode.getPaymentMethodType());
        assertEquals(ReceiptStatus.PAYED, qrCode.getReceiptStatus());
        assertEquals(OrderStatus.PENDING, qrCode.getOrderStatus());
    }

    @Test
    void testValidQRCode_CASH_PENDING_PENDING() throws Exception {
        // Arrange
        String validQR = "12345_2024-01-15T10:30:00.000Z_2024-01-15T10:31:00.000Z_CASH_PENDING_PENDING";

        // Act
        qrCode.validateQrCode(validQR);

        // Assert
        assertEquals(PaymentMethodType.CASH, qrCode.getPaymentMethodType());
        assertEquals(ReceiptStatus.PENDING, qrCode.getReceiptStatus());
    }

    @Test
    void testInvalidQRCode_WrongNumberOfParts() {
        // Arrange
        String invalidQR = "part1_part2_part3_part4_part5"; // 5 parts instead of 6

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> qrCode.validateQrCode(invalidQR));
        assertEquals("QR Code is not valid", exception.getMessage());
    }

    @Test
    void testInvalidQRCode_CashWithFutureDate() {
        // Arrange
        String futureDate = "2025-01-15T10:30:00.000Z";
        String currentDate = "2024-01-15T10:29:00.000Z";
        String invalidQR = String.format("12345_%s_%s_CASH_PENDING_PENDING", futureDate, currentDate);

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> qrCode.validateQrCode(invalidQR));
        assertTrue(exception.getMessage().contains("QR Code is not valid"));
    }

    @Test
    void testInvalidQRCode_DeliveredInCreation() {
        // Arrange
        String invalidQR = "12345_2024-01-15T10:30:00.000Z_2024-01-15T10:29:00.000Z_BANK_PAYED_DELIVERED";

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> qrCode.validateQrCode(invalidQR));
        assertEquals("Receipt can't be delivered in creation", exception.getMessage());
    }

    @Test
    void testInvalidQRCode_RefundedInCreation() {
        // Arrange
        String invalidQR = "12345_2024-01-15T10:30:00.000Z_2024-01-15T10:29:00.000Z_BANK_REFUNDED_PENDING";

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> qrCode.validateQrCode(invalidQR));
        assertEquals("Receipt can't be refunded in creation", exception.getMessage());
    }

    @Test
    void testInvalidQRCode_BankPaymentAfterReceipt() {
        // Arrange
        String invalidQR = "12345_2024-01-15T10:30:00.000Z_2024-01-15T10:31:00.000Z_BANK_PAYED_PENDING";

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> qrCode.validateQrCode(invalidQR));
        assertEquals("Payment processed at can't be after receipt generated date", exception.getMessage());
    }

    @Test
    void testInvalidQRCode_CashPaymentBeforeReceipt() {
        // Arrange
        String invalidQR = "12345_2024-01-15T10:30:00.000Z_2024-01-15T10:29:00.000Z_CASH_PENDING_PENDING";

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> qrCode.validateQrCode(invalidQR));
        assertEquals("Payment processed at can't be before receipt generated date", exception.getMessage());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        QRCode customQR = new QRCode(
                "67890",
                "2024-01-15T10:30:00.000Z",
                "2024-01-15T10:29:00.000Z",
                PaymentMethodType.WALLET,
                ReceiptStatus.PAYED,
                OrderStatus.PENDING
        );

        // Assert
        assertEquals("67890", customQR.getOrderId());
        assertEquals(PaymentMethodType.WALLET, customQR.getPaymentMethodType());
        assertEquals(ReceiptStatus.PAYED, customQR.getReceiptStatus());
    }
}
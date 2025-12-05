package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastucture.Clients;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Dto.ReceiptResponses.GetQrReceiptResponse;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.ReceiptProviderAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptProviderAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReceiptProviderAdapter receiptProviderAdapter;

    private final String baseUrl = "http://localhost:8082";
    private final String basePath = "/api/v1/receipts";
    private final String orderId = "12345";
    private final String qrCode = "12345_2024-01-15T10:30:00.000Z_2024-01-15T10:29:00.000Z_BANK_PAYED_PENDING";

    @BeforeEach
    void setUp() {
        // Inject values using reflection since @Value doesn't work in tests
        receiptProviderAdapter = new ReceiptProviderAdapter(restTemplate);
        try {
            var baseUrlField = ReceiptProviderAdapter.class.getDeclaredField("baseUrl");
            baseUrlField.setAccessible(true);
            baseUrlField.set(receiptProviderAdapter, baseUrl);

            var basePathField = ReceiptProviderAdapter.class.getDeclaredField("basePath");
            basePathField.setAccessible(true);
            basePathField.set(receiptProviderAdapter, basePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set fields", e);
        }
    }

    @Test
    void getQrCodeByOrderId_Success() {
        // Arrange
        String url = baseUrl + basePath + "/order/" + orderId + "/qr";
        GetQrReceiptResponse mockResponse = new GetQrReceiptResponse(qrCode);
        ResponseEntity<GetQrReceiptResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        // Act
        String result = receiptProviderAdapter.getQrCodeByOrderId(orderId);

        // Assert
        assertEquals(qrCode, result);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId));
    }

    @Test
    void getQrCodeByOrderId_NotFound() {
        // Arrange
        String url = baseUrl + basePath + "/order/" + orderId + "/qr";
        ResponseEntity<GetQrReceiptResponse> responseEntity =
                new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> receiptProviderAdapter.getQrCodeByOrderId(orderId));

        assertTrue(exception.getMessage().contains("Failed to get QR code for order"));
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId));
    }

    @Test
    void getQrCodeByOrderId_NullResponseBody() {
        // Arrange
        String url = baseUrl + basePath + "/order/" + orderId + "/qr";
        ResponseEntity<GetQrReceiptResponse> responseEntity =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> receiptProviderAdapter.getQrCodeByOrderId(orderId));

        assertTrue(exception.getMessage().contains("Failed to get QR code for order"));
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId));
    }

    @Test
    void getQrCodeByOrderId_NetworkError() {
        // Arrange
        String url = baseUrl + basePath + "/order/" + orderId + "/qr";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId))
        ).thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> receiptProviderAdapter.getQrCodeByOrderId(orderId));

        assertTrue(exception.getMessage().contains("Error getting QR code for order"));
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId));
    }

    @Test
    void updateToPayed_Success() {
        // Arrange
        String url = baseUrl + basePath + "/" + orderId + "/pay";
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        // Act
        assertDoesNotThrow(() -> receiptProviderAdapter.updateToPayed(orderId));

        // Assert
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId));
    }

    @Test
    void updateToPayed_Failure() {
        // Arrange
        String url = baseUrl + basePath + "/" + orderId + "/pay";
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> receiptProviderAdapter.updateToPayed(orderId));

        assertTrue(exception.getMessage().contains("Failed to update receipt to payed for order"));
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId));
    }

    @Test
    void updateToDelivered_Success() {
        // Arrange
        String url = baseUrl + basePath + "/" + orderId + "/deliver";
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        // Act
        assertDoesNotThrow(() -> receiptProviderAdapter.updateToDelivered(orderId));

        // Assert
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId));
    }

    @Test
    void updateToDelivered_Failure() {
        // Arrange
        String url = baseUrl + basePath + "/" + orderId + "/deliver";
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> receiptProviderAdapter.updateToDelivered(orderId));

        assertTrue(exception.getMessage().contains("Failed to update receipt to delivered for order"));
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId));
    }
}

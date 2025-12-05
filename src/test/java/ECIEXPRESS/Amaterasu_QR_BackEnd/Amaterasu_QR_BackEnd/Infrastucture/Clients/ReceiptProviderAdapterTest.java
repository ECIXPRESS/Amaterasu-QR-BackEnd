package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastucture.Clients;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Exception.ExternalServiceException;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Dto.ReceiptResponses.GetQrReceiptResponse;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.ReceiptProviderAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
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
        ReflectionTestUtils.setField(receiptProviderAdapter, "baseUrl", baseUrl);
        ReflectionTestUtils.setField(receiptProviderAdapter, "basePath", basePath);
    }

    @Test
    void getQrCodeByOrderId_Success() {
        // The actual URL will have the orderId already replaced
        String expectedUrl = baseUrl + basePath + "/order/" + orderId + "/qr";

        GetQrReceiptResponse mockResponse = new GetQrReceiptResponse(qrCode);
        ResponseEntity<GetQrReceiptResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        // Mock with actual arguments
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        String result = receiptProviderAdapter.getQrCodeByOrderId(orderId);

        assertEquals(qrCode, result);
        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId));
    }

    @Test
    void getQrCodeByOrderId_NotFound() {
        String expectedUrl = baseUrl + basePath + "/order/" + orderId + "/qr";

        // Create response with NOT_FOUND status and null body
        ResponseEntity<GetQrReceiptResponse> responseEntity =
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        ExternalServiceException exception = assertThrows(ExternalServiceException.class,
                () -> receiptProviderAdapter.getQrCodeByOrderId(orderId));

        assertEquals("Failed to get QR code for order: " + orderId + ". Status: 404 NOT_FOUND", exception.getMessage());
        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId));
    }

    @Test
    void getQrCodeByOrderId_NullResponseBody() {
        String expectedUrl = baseUrl + basePath + "/order/" + orderId + "/qr";

        // Create response with OK status but null body
        ResponseEntity<GetQrReceiptResponse> responseEntity =
                ResponseEntity.ok(null);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        ExternalServiceException exception = assertThrows(ExternalServiceException.class,
                () -> receiptProviderAdapter.getQrCodeByOrderId(orderId));

        assertEquals("Empty response body for order: " + orderId, exception.getMessage());
        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId));
    }

    @Test
    void getQrCodeByOrderId_NetworkError() {
        String expectedUrl = baseUrl + basePath + "/order/" + orderId + "/qr";

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId))
        ).thenThrow(new RestClientException("Network error"));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class,
                () -> receiptProviderAdapter.getQrCodeByOrderId(orderId));

        assertEquals("Network error getting QR code for order: " + orderId, exception.getMessage());
        assertNotNull(exception.getCause());
        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GetQrReceiptResponse.class),
                eq(orderId));
    }

    @Test
    void updateToPayed_Success() {
        String expectedUrl = baseUrl + basePath + "/" + orderId + "/pay";
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        assertDoesNotThrow(() -> receiptProviderAdapter.updateToPayed(orderId));

        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId));
    }

    @Test
    void updateToPayed_Failure() {
        String expectedUrl = baseUrl + basePath + "/" + orderId + "/pay";

        // Use BAD_REQUEST with .build() to create the response
        ResponseEntity<Void> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        ExternalServiceException exception = assertThrows(ExternalServiceException.class,
                () -> receiptProviderAdapter.updateToPayed(orderId));

        assertEquals("Failed to update receipt to payed for order: " + orderId + ". Status: 400 BAD_REQUEST", exception.getMessage());
        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId));
    }

    @Test
    void updateToDelivered_Success() {
        String expectedUrl = baseUrl + basePath + "/" + orderId + "/deliver";
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        assertDoesNotThrow(() -> receiptProviderAdapter.updateToDelivered(orderId));

        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId));
    }

    @Test
    void updateToDelivered_Failure() {
        String expectedUrl = baseUrl + basePath + "/" + orderId + "/deliver";

        // Use BAD_REQUEST with .build() to create the response
        ResponseEntity<Void> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId))
        ).thenReturn(responseEntity);

        ExternalServiceException exception = assertThrows(ExternalServiceException.class,
                () -> receiptProviderAdapter.updateToDelivered(orderId));

        assertEquals("Failed to update receipt to delivered for order: " + orderId + ". Status: 400 BAD_REQUEST", exception.getMessage());
        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Void.class),
                eq(orderId));
    }
}
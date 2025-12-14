package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port.ReceiptProvider;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Exception.ExternalServiceException;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Dto.ReceiptResponses.GetQrReceiptResponse;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Mappers.ReceiptProviderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptProviderAdapter implements ReceiptProvider {

    private final RestTemplate restTemplate;

    @Value("${gateway.url:http://gateway:8081}")
    private String baseUrl;

    @Value("${microservices.receipt.base-path}")
    private String basePath;

    @Override
    public String getQrCodeByOrderId(String orderId) {
        try {
            log.info("Getting QR code for order: {}", orderId);
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = buildUrl("/order/{orderId}/qr", orderId);

            ResponseEntity<GetQrReceiptResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    GetQrReceiptResponse.class,
                    orderId);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Failed to get QR code for order: {}. Status: {}", orderId, response.getStatusCode());
                throw new ExternalServiceException("Failed to get QR code for order: " + orderId + ". Status: " + response.getStatusCode());
            }

            GetQrReceiptResponse receiptResponse = response.getBody();
            if (receiptResponse == null) {
                log.error("Empty response body for order: {}", orderId);
                throw new ExternalServiceException("Empty response body for order: " + orderId);
            }

            log.info("Successfully retrieved QR code for order: {}", orderId);
            return ReceiptProviderMapper.mapQrResponseToString(receiptResponse);

        } catch (RestClientException e) {
            log.error("Network error getting QR code for order {}: {}", orderId, e.getMessage());
            throw new ExternalServiceException("Network error getting QR code for order: " + orderId, e);
        } catch (ExternalServiceException e) {
            // Re-throw ExternalServiceException without wrapping
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting QR code for order {}: {}", orderId, e.getMessage());
            throw new ExternalServiceException("Unexpected error getting QR code for order: " + orderId, e);
        }
    }

    @Override
    public void updateToDelivered(String orderId) {
        updateReceiptStatus(orderId, "deliver", "delivered");
    }

    @Override
    public void updateToPayed(String orderId) {
        updateReceiptStatus(orderId, "pay", "payed");
    }

    private void updateReceiptStatus(String orderId, String endpoint, String status) {
        try {
            log.info("Updating receipt to {} for order: {}", status, orderId);
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = buildUrl("/{orderId}/" + endpoint, orderId);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    entity,
                    Void.class,
                    orderId);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Failed to update receipt to {} for order: {}. Status: {}",
                        status, orderId, response.getStatusCode());
                throw new ExternalServiceException("Failed to update receipt to " + status + " for order: " + orderId + ". Status: " + response.getStatusCode());
            }

            log.info("Successfully updated receipt to {} for order: {}", status, orderId);

        } catch (RestClientException e) {
            log.error("Network error updating receipt to {} for order {}: {}", status, orderId, e.getMessage());
            throw new ExternalServiceException("Network error updating receipt to " + status + " for order: " + orderId, e);
        } catch (ExternalServiceException e) {
            // Re-throw ExternalServiceException without wrapping
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating receipt to {} for order {}: {}", status, orderId, e.getMessage());
            throw new ExternalServiceException("Unexpected error updating receipt to " + status + " for order: " + orderId, e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private String buildUrl(String pathTemplate, String orderId) {
        String cleanBasePath = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        String cleanPathTemplate = pathTemplate.startsWith("/") ? pathTemplate : "/" + pathTemplate;

        String finalPath = cleanPathTemplate.replace("{orderId}", orderId);

        return String.format("%s%s%s",
                baseUrl,
                cleanBasePath,
                finalPath);
    }
}
package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port.ReceiptProvider;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Dto.ReceiptRequests.GetQrReceiptRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Dto.ReceiptResponses.GetQrReceiptResponse;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Mappers.ReceiptProviderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptProviderAdapter implements ReceiptProvider {
    private final RestTemplate restTemplate;

    @Value("${microservices.receipt.url}")
    private String baseUrl;

    @Value("${microservices.receipt.base-path}")
    private String basePath;

    @Override
    public String getQrCodeByOrderId(String orderId) {
        try {
            log.info("Processing QrCode for order: {}", orderId);
            GetQrReceiptRequest receiptRequest = ReceiptProviderMapper.mapToGetQrReceiptRequest(orderId);

            HttpHeaders headers = createHeaders();
            HttpEntity<GetQrReceiptRequest> entity = new HttpEntity<>(receiptRequest, headers);

            ResponseEntity<GetQrReceiptResponse> response = restTemplate.exchange(
                    baseUrl+basePath, HttpMethod.POST, entity, GetQrReceiptResponse.class);

            GetQrReceiptResponse receiptResponse = response.getBody();

            if(receiptResponse == null){
                log.error("Receipt response is null for order {}", orderId);
                throw new RuntimeException("Receipt response is null for order "+orderId);
            }

            log.info("Request response received for order {}", orderId);

            return ReceiptProviderMapper.mapQrResponseToString(receiptResponse);

        } catch (Exception e) {
            log.error("Error processing QrCode for order {} Error: {}",orderId, e.getMessage());
            throw new RuntimeException("Error processing QrCode for order "+orderId+" Error: "+e.getMessage());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}

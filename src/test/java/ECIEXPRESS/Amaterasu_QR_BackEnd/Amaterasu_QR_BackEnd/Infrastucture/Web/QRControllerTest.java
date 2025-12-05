package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastucture.Web;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Ports.QRUseCases;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.CreateQrCodeRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.ValidateQRRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrResponses.CreateQrCodeResponse;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.QRController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = QRController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "ECIEXPRESS\\.Amaterasu_QR_BackEnd\\.Amaterasu_QR_BackEnd\\.Config\\..*"
        )
)
class QRControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRUseCases qrUseCases;

    @Autowired
    private ObjectMapper objectMapper;

    private final String validOrderId = "12345";
    private final String encryptedQR = "encrypted_qr_string_123";

    @Test
    void createQrCode_Success() throws Exception {
        // Arrange
        CreateQrCodeRequest request = new CreateQrCodeRequest(validOrderId);
        CreateQrCodeResponse response = new CreateQrCodeResponse(encryptedQR);

        when(qrUseCases.createQrCode(any(CreateQrCodeRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.encodedQRCode").value(encryptedQR));
    }

    @Test
    void createQrCode_MissingOrderId() throws Exception {
        // Arrange
        String invalidRequest = "{}"; // Missing orderId

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQrCode_EmptyOrderId() throws Exception {
        // Arrange
        String invalidRequest = "{\"orderId\": \"\"}"; // Empty orderId

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQrCode_ServiceThrowsException() throws Exception {
        // Arrange
        CreateQrCodeRequest request = new CreateQrCodeRequest(validOrderId);

        when(qrUseCases.createQrCode(any(CreateQrCodeRequest.class)))
                .thenThrow(new RuntimeException("Internal error"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void validateQrCode_Success_ReturnsTrue() throws Exception {
        // Arrange
        ValidateQRRequest request = new ValidateQRRequest(encryptedQR);

        when(qrUseCases.ValidateQrCode(any(ValidateQRRequest.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void validateQrCode_Success_ReturnsFalse() throws Exception {
        // Arrange
        ValidateQRRequest request = new ValidateQRRequest(encryptedQR);

        when(qrUseCases.ValidateQrCode(any(ValidateQRRequest.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void validateQrCode_MissingQRCode() throws Exception {
        // Arrange
        String invalidRequest = "{}"; // Missing encodedQrCode

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateQrCode_EmptyQRCode() throws Exception {
        // Arrange
        String invalidRequest = "{\"encodedQrCode\": \"\"}"; // Empty encodedQrCode

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateQrCode_InvalidJSON() throws Exception {
        // Arrange
        String invalidJson = "{invalid json}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateQrCode_ServiceThrowsException() throws Exception {
        // Arrange
        ValidateQRRequest request = new ValidateQRRequest(encryptedQR);

        when(qrUseCases.ValidateQrCode(any(ValidateQRRequest.class)))
                .thenThrow(new RuntimeException("Validation error"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/qr/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
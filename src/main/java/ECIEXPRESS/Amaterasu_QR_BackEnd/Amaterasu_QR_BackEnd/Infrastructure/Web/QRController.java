package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Ports.QRUseCases;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.CreateQrCodeRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.ValidateQRRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrResponses.CreateQrCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/qr")
@RequiredArgsConstructor
@Tag(name = "QR Code Management", description = "APIs for generating and validating QR codes")
public class QRController {

    private final QRUseCases qrUseCases;

    @PostMapping("/create")
    public ResponseEntity<?> createQrCode(@Valid @RequestBody CreateQrCodeRequest request) {
        try {
            CreateQrCodeResponse response = qrUseCases.createQrCode(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating QR code: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateQrCode(@Valid @RequestBody ValidateQRRequest request) {
        try {
            boolean isValid = qrUseCases.ValidateQrCode(request);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error validating QR code: " + e.getMessage());
        }
    }
}
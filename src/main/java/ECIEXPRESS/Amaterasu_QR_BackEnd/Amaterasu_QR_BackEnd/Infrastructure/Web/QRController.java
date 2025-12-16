package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Ports.QRUseCases;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.CreateQrCodeRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.ValidateQRRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrResponses.CreateQrCodeResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/qr")
@RequiredArgsConstructor
@Tag(name = "QR Code Management", description = "APIs for generating and validating QR codes")
@Validated
public class QRController {

    private final QRUseCases qrUseCases;

    @PostMapping(
            value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CreateQrCodeResponse> createQrCode(
            @Valid @RequestBody CreateQrCodeRequest request) {
        try {
            CreateQrCodeResponse response = qrUseCases.createQrCode(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating QR code: " + e.getMessage(),
                    e
            );
        }
    }

    @PostMapping(
            value = "/validate",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> validateQrCode(
            @Valid @RequestBody ValidateQRRequest request) {
        try {
            boolean isValid = qrUseCases.ValidateQrCode(request);
            return isValid ?
                    ResponseEntity.ok().build() :
                    ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error validating QR code: " + e.getMessage(),
                    e
            );
        }
    }
}
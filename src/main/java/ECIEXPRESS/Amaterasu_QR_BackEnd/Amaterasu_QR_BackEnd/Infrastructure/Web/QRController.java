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

    @Operation(
            summary = "Generate QR Code",
            description = "Generates an encrypted QR code for the specified order ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order details for QR code generation",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateQrCodeRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"orderId\": \"12345\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "QR code generated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CreateQrCodeResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"encodedQRCode\": \"encrypted_qr_code_string_here\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content
                    )
            }
    )
    @PostMapping(
            value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CreateQrCodeResponse> createQrCode(@Valid @RequestBody CreateQrCodeRequest request) {
        CreateQrCodeResponse response = qrUseCases.createQrCode(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Validate QR Code",
            description = "Validates the provided QR code and updates the order status if valid",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "QR code to validate",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidateQRRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"encodedQrCode\": \"encrypted_qr_code_string_here\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "QR code validation result",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "boolean"),
                                    examples = @ExampleObject(value = "true")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid QR code format",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Order not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content
                    )
            }
    )
    @PostMapping(
            value = "/validate",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Boolean> validateQrCode(@Valid @RequestBody ValidateQRRequest request) {
        boolean isValid = qrUseCases.ValidateQrCode(request);
        return ResponseEntity.ok(isValid);
    }
}
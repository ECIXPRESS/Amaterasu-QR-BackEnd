package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests;

import jakarta.validation.constraints.NotBlank;

public record CreateQrCodeRequest (
        @NotBlank(message = "orderId is required")
        String orderId
){
}

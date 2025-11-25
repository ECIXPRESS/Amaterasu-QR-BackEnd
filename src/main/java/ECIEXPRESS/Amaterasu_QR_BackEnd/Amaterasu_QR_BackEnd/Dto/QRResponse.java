package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Dto;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.OrderStatus;
import java.time.LocalDateTime;

/**
 * Response returned after generating or retrieving a QR code.
 *
 * @param qrId internal identifier of the QR (if applicable)
 * @param orderId associated order identifier
 * @param orderStatus current state of the order
 * @param date creation date of the QR
 * @param qr encoded QR representation (Base64, bytes, etc.)
 */
public record QRResponse(
        String qrId,
        String orderId,
        OrderStatus orderStatus,
        LocalDateTime date,
        String qr
) {}

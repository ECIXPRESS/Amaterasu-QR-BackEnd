package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Dto;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.OrderStatus;

/**
 * Request body used to generate a QR for an order.
 *
 * @param orderId unique identifier of the order
 * @param orderStatus initial status assigned to the order
 */
public record CreateQRRequest(
        String orderId,
        OrderStatus orderStatus
) {}

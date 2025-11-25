package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.OrderStatus;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.QRCode;

/**
 * Input port (use case) responsible for creating a new QR definition
 * associated with a business order.
 */
public interface CreateQRUseCase {

    /**
     * Creates and persists a new QR domain object.
     *
     * @param orderId unique identifier of the order
     * @param status  current business status of the order
     * @return generated {@link QRCode} instance
     */
    QRCode create(String orderId, OrderStatus status);
}

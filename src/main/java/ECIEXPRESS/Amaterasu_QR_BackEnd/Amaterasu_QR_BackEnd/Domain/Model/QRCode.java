package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model;

import java.time.LocalDateTime;

/**
 * Domain model representing a generated QR associated with an order.
 */
public class QRCode {
    private final String orderId;
    private final OrderStatus status;
    private final LocalDateTime date;

    /**
     * Creates an immutable {@link QRCode} domain object.
     *
     * @param orderId unique alphanumeric order identifier
     * @param status  business status of the associated order
     * @param date    timestamp when the QR was created
     */
    public QRCode(String orderId, OrderStatus status, LocalDateTime date) {
        this.orderId = orderId;
        this.status = status;
        this.date = date;
    }
}

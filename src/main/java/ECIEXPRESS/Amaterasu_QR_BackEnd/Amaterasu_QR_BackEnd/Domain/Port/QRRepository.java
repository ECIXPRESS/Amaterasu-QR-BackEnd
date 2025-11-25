package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.QRCode;

import java.util.Optional;

/**
 * Port that defines persistence operations for QR codes.
 */
public interface QRRepository {

    /**
     * Saves the provided QR information.
     *
     * @param qr domain object representing the QR
     * @return saved QR instance
     */
    QRCode save(QRCode qr);

    /**
     * Retrieves a QR by its associated order ID.
     *
     * @param orderId identifier of the order linked to the QR
     * @return optional containing the QR if found
     */
    Optional<QRCode> findById(String orderId);
}

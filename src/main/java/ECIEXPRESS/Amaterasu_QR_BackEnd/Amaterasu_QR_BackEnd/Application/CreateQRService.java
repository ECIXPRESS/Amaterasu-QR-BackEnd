package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.OrderStatus;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.QRCode;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port.QRRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Application service implementing {@link CreateQRUseCase}.
 */
@Service
public class CreateQRService implements CreateQRUseCase {

    private final QRRepository qrRepository;

    /**
     * Constructs a CreateQRService instance.
     *
     * @param qrRepository outbound port used to persist QR domain objects
     */
    public CreateQRService(QRRepository qrRepository) { //falta conectar bd en repositorios
        this.qrRepository = qrRepository;
    }

    /**
     * Creates a new QR with the given orderId and status, assigns a creation timestamp,
     * and persists it through the repository port.
     *
     * @param orderId unique identifier of the associated order
     * @param status  status of the order at QR generation time
     * @return persisted {@link QRCode} instance
     */
    @Override
    public QRCode create(String orderId, OrderStatus status) {
        QRCode qr = new QRCode(
                orderId,
                status,
                LocalDateTime.now()
        );

        qrRepository.save(qr);
        return qr;
    }
}

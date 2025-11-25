package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.CreateQRUseCase;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.QRCode;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port.QRGenerator;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Dto.CreateQRRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Dto.QRResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for exposing QR code creation endpoints.
 */
@RestController
@RequestMapping("/api/v1/qr")
public class QRController {

    private final CreateQRUseCase createQrUseCase;
    private final QRGenerator qrGenerator;

    /**
     * Creates a new instance of {@link QRController}.
     *
     * @param createQrUseCase application use case responsible for generating QR domain objects
     * @param qrGenerator     outbound port responsible for generating the visual QR representation
     */
    public QRController(CreateQRUseCase createQrUseCase, QRGenerator qrGenerator) {
        this.createQrUseCase = createQrUseCase;
        this.qrGenerator = qrGenerator;
    }

    /**
     * Creates a new QR associated with the given order ID.
     *
     * @param request Create QR request containing the orderId
     * @return {@link QRResponse} including metadata and the generated QR image encoded in Base64
     */
    @PostMapping
    public ResponseEntity<QRResponse> create(@RequestBody CreateQRRequest request) {
        QRCode qr = createQrUseCase.create(request.orderId());
        String qrImage = qrGenerator.generate(qr.getId());

        return ResponseEntity.ok(
                new QRResponse(
                        qr.getId(),
                        qr.getOrderId(),
                        qr.getOrderStatus(),
                        qr.getCreatedAt(),
                        qrImage
                )
        );
    }
}

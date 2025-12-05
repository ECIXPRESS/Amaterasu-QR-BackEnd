package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Services;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Mappers.QrCodeMapper;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Ports.QRUseCases;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.QRCode;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port.ReceiptProvider;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.CreateQrCodeRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.ValidateQRRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrResponses.CreateQrCodeResponse;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils.SimpleEncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class QRService implements QRUseCases {
    private final ReceiptProvider receiptProvider;

    @Override
    public boolean ValidateQrCode(ValidateQRRequest request) {
        try {
            String qrCodeText = request.encodedQrCode();
            String decryptedText = SimpleEncryptionUtil.decrypt(qrCodeText);

            QRCode qrcode = new QRCode();
            qrcode.validateQrCode(decryptedText);

            receiptProvider.updateToPayed(qrcode.getOrderId());
            receiptProvider.updateToDelivered(qrcode.getOrderId());
            log.info("QR code validated and receipt updated for order: {}", qrcode.getOrderId());
            return true;
        } catch (Exception e) {
            log.error("Error validating QR code: {}", e.getMessage());
            throw new RuntimeException("Error validating QR code: " + e.getMessage());
        }
    }

    @Override
    public CreateQrCodeResponse createQrCode(CreateQrCodeRequest request) {
        try {
            String qrCodeString = receiptProvider.getQrCodeByOrderId(request.orderId());

            QRCode qrcode = new QRCode();
            qrcode.validateQrCode(qrCodeString);

            String encryptedQR = SimpleEncryptionUtil.encrypt(qrCodeString);
            log.info("QR code created and validated for order: {}", request.orderId());
            return QrCodeMapper.QrStringToCreateQrCodeResponse(encryptedQR);
        } catch (Exception e) {
            log.error("Error creating QR code for order {}: {}", request.orderId(), e.getMessage());
            throw new RuntimeException("Error creating QR code for order " + request.orderId() + ": " + e.getMessage());
        }
    }
}

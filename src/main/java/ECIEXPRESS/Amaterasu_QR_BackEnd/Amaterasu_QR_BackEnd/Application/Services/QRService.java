package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Services;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Mappers.QrCodeMapper;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Ports.QRUseCases;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.QRCode;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port.ReceiptProvider;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Exception.QRValidationException;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.CreateQrCodeRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.ValidateQRRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrResponses.CreateQrCodeResponse;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils.EncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class QRService implements QRUseCases {
    private final ReceiptProvider receiptProvider;
    private final EncryptionUtil encryptionUtil;

    @Override
    public boolean ValidateQrCode(ValidateQRRequest request) {
        if (request == null || request.encodedQrCode() == null || request.encodedQrCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid QR code request: encoded QR code cannot be null or empty");
        }

        try {
            String qrCodeText = request.encodedQrCode();
            String decryptedText = encryptionUtil.decrypt(qrCodeText);

            if (decryptedText == null || decryptedText.trim().isEmpty()) {
                throw new QRValidationException("Decrypted QR code text is empty");
            }

            QRCode qrcode = new QRCode();
            qrcode.validateQrCode(decryptedText);

            receiptProvider.updateToPayed(qrcode.getOrderId());
            receiptProvider.updateToDelivered(qrcode.getOrderId());
            log.info("QR code validated and receipt updated for order: {}", qrcode.getOrderId());
            return true;
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument in QR validation: {}", e.getMessage());
            throw e;
        } catch (QRValidationException e) {
            log.error("QR validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error validating QR code: {}", e.getMessage());
            throw new QRValidationException("Unexpected error during QR validation", e);
        }
    }

    @Override
    public CreateQrCodeResponse createQrCode(CreateQrCodeRequest request) {
        try {
            String qrCodeString = receiptProvider.getQrCodeByOrderId(request.orderId());

            QRCode qrcode = new QRCode();
            qrcode.validateQrCode(qrCodeString);

            String encryptedQR = encryptionUtil.encrypt(qrCodeString);
            log.info("QR code created and validated for order: {}", request.orderId());
            return QrCodeMapper.QrStringToCreateQrCodeResponse(encryptedQR);
        } catch (Exception e) {
            log.error("Error creating QR code for order {}: {}", request.orderId(), e.getMessage());
            throw new RuntimeException("Error creating QR code for order " + request.orderId() + ": " + e.getMessage());
        }
    }
}

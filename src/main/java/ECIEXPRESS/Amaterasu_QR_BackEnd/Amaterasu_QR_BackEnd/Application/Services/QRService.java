package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Services;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Mappers.QrCodeMapper;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Ports.QRUseCases;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.QRCode;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port.ReceiptProvider;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.CreateQrCodeRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrRequests.ValidateQRRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrResponses.CreateQrCodeResponse;
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
            QRCode qrcode = new QRCode();
            qrcode.validateQrCode(request.encodedQrCode());
            receiptProvider.updateToPayed(qrcode.getOrderId());
            receiptProvider.updateToDelivered(qrcode.getOrderId());
            return true;
        } catch (Exception e) {
            log.error("Error validating QR code: {}", e.getMessage());
            throw new RuntimeException("Error validating QR code: " + e.getMessage());
        }
    }

    @Override
    public CreateQrCodeResponse createQrCode(CreateQrCodeRequest request) {
        String qrCode = receiptProvider.getQrCodeByOrderId(request.orderId());
        QRCode code = new QRCode();
        try {
            code.validateQrCode(qrCode);
            return QrCodeMapper.QrStringToCreateQrCodeResponse(qrCode);
        } catch (Exception e) {
            log.error("Error validating QR code: {}", e.getMessage());
            throw new RuntimeException("Error validating QR code: " + e.getMessage());
        }
    }
}

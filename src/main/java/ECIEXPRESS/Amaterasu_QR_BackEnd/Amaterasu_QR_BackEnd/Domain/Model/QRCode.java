package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.Enums.OrderStatus;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.Enums.PaymentMethodType;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.Enums.ReceiptStatus;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QRCode {
    private String orderId;
    private String receiptGeneratedDate;
    private String paymentProcessedAt;
    private PaymentMethodType paymentMethodType;
    private ReceiptStatus receiptStatus;
    private OrderStatus orderStatus;

    public void validateQrCode(String qrCodeString) throws Exception {
        List<String> qrCodeList = Arrays.stream(qrCodeString.split("_")).toList();

        if (qrCodeList.size() != 6) {
            log.error("QR Code is not valid - wrong number of parts");
            throw new Exception("QR Code is not valid");
        }

        this.orderId = qrCodeList.get(0);
        this.receiptGeneratedDate = qrCodeList.get(1);
        this.paymentProcessedAt = qrCodeList.get(2);

        try {
            this.paymentMethodType = PaymentMethodType.valueOf(qrCodeList.get(3));
            this.receiptStatus = ReceiptStatus.valueOf(qrCodeList.get(4));
            this.orderStatus = OrderStatus.valueOf(qrCodeList.get(5));
        } catch (IllegalArgumentException e) {
            log.error("Invalid enum value in QR code: {}", e.getMessage());
            throw new Exception("QR Code is not valid");
        }

        Date paymentProcessedDate;
        Date receiptGeneratedDate;
        try {
            paymentProcessedDate = DateUtils.parseDate(this.paymentProcessedAt, DateUtils.TIMESTAMP_FORMAT);
            receiptGeneratedDate = DateUtils.parseDate(this.receiptGeneratedDate, DateUtils.TIMESTAMP_FORMAT);
        } catch (Exception e) {
            log.error("Invalid date format in QR code: {}", e.getMessage());
            throw new Exception("QR Code is not valid");
        }

        if (this.paymentMethodType == PaymentMethodType.CASH && receiptGeneratedDate.after(new Date())) {
            log.error("Receipt generated date can't be after today if paying in cash");
            throw new Exception("QR Code is not valid");
        }

        if (this.paymentMethodType != PaymentMethodType.CASH && receiptGeneratedDate.before(paymentProcessedDate)) {
            log.error("Receipt generated date can't be before payment processed at");
            throw new Exception("QR Code is not valid");
        }

        if (this.receiptStatus == ReceiptStatus.DELIVERED) {
            log.error("Receipt can't be delivered in creation");
            throw new Exception("Receipt can't be delivered in creation");
        }

        if (this.receiptStatus == ReceiptStatus.REFUNDED) {
            if (this.orderStatus == OrderStatus.DELIVERED) {
                log.error("Receipt can't be refunded and delivered in creation");
                throw new Exception("Receipt can't be refunded and delivered in creation");
            }
            log.error("Receipt can't be refunded in creation");
            throw new Exception("Receipt can't be refunded in creation");
        }

        if (this.orderStatus == OrderStatus.DELIVERED) {
            if (this.receiptStatus == ReceiptStatus.PENDING) {
                log.error("Receipt can't be pending and delivered");
                throw new Exception("Receipt can't be pending and delivered");
            }
            log.error("Receipt can't be delivered in creation");
            throw new Exception("Receipt can't be delivered in creation");
        }

        if (this.paymentMethodType == PaymentMethodType.CASH) {
            if (this.receiptStatus == ReceiptStatus.PAYED) {
                log.error("Receipt can't be payed as cash in creation");
                throw new Exception("Receipt can't be payed as cash in creation");
            }
            if (paymentProcessedDate.before(receiptGeneratedDate)) {
                log.error("Payment processed at can't be before receipt generated date for CASH");
                throw new Exception("Payment processed at can't be before receipt generated date");
            }
        }
        else if (this.paymentMethodType == PaymentMethodType.BANK) {
            if (paymentProcessedDate.after(receiptGeneratedDate)) {
                log.error("Payment processed at can't be after receipt generated date for BANK");
                throw new Exception("Payment processed at can't be after receipt generated date");
            }
        }
        else if (this.paymentMethodType == PaymentMethodType.WALLET) {
            if (paymentProcessedDate.after(receiptGeneratedDate)) {
                log.error("Payment processed at can't be after receipt generated date for WALLET");
                throw new Exception("Payment processed at can't be after receipt generated date");
            }
        }
    }
}

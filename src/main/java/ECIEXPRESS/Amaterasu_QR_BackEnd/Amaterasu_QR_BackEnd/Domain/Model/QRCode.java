package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.Enums.OrderStatus;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.Enums.PaymentMethodType;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model.Enums.ReceiptStatus;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Exception.QRValidationException;
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

    public void validateQrCode(String qrCodeString) throws QRValidationException {
        validateInputString(qrCodeString);
        List<String> qrCodeList = parseAndValidateParts(qrCodeString);
        setFieldsFromParts(qrCodeList);
        Date[] dates = parseDates();
        validateBusinessRules(dates[0], dates[1]);
    }

    private void validateInputString(String qrCodeString) throws QRValidationException {
        if (qrCodeString == null || qrCodeString.trim().isEmpty()) {
            throw new QRValidationException("QR code string cannot be null or empty");
        }
    }

    private List<String> parseAndValidateParts(String qrCodeString) throws QRValidationException {
        List<String> qrCodeList = Arrays.stream(qrCodeString.split("_")).toList();

        if (qrCodeList.size() != 6) {
            log.error("QR Code is not valid - wrong number of parts. Expected 6, got {}", qrCodeList.size());
            throw new QRValidationException("QR Code is not valid - wrong number of parts");
        }

        for (int i = 0; i < qrCodeList.size(); i++) {
            if (qrCodeList.get(i) == null || qrCodeList.get(i).trim().isEmpty()) {
                throw new QRValidationException("QR code part at position " + i + " is empty");
            }
        }

        return qrCodeList;
    }

    private void setFieldsFromParts(List<String> qrCodeList) throws QRValidationException {
        this.orderId = qrCodeList.get(0);
        this.receiptGeneratedDate = qrCodeList.get(1);
        this.paymentProcessedAt = qrCodeList.get(2);

        try {
            this.paymentMethodType = PaymentMethodType.valueOf(qrCodeList.get(3));
            this.receiptStatus = ReceiptStatus.valueOf(qrCodeList.get(4));
            this.orderStatus = OrderStatus.valueOf(qrCodeList.get(5));
        } catch (IllegalArgumentException e) {
            log.error("Invalid enum value in QR code: {}", e.getMessage());
            throw new QRValidationException("QR Code is not valid");
        }
    }

    private Date[] parseDates() throws QRValidationException {
        try {
            Date paymentProcessedDate = DateUtils.parseDate(this.paymentProcessedAt, DateUtils.TIMESTAMP_FORMAT);
            Date parsedReceiptDate = DateUtils.parseDate(this.receiptGeneratedDate, DateUtils.TIMESTAMP_FORMAT);
            return new Date[]{paymentProcessedDate, parsedReceiptDate};
        } catch (Exception e) {
            log.error("Invalid date format in QR code: {}", e.getMessage());
            throw new QRValidationException("QR Code is not valid");
        }
    }

    private void validateBusinessRules(Date paymentProcessedDate, Date receiptGeneratedDate) throws QRValidationException {
        validateStatusRules();
        validatePaymentMethodRules(paymentProcessedDate, receiptGeneratedDate);
    }

    private void validateStatusRules() throws QRValidationException {
        if (this.receiptStatus == ReceiptStatus.DELIVERED) {
            log.error("Receipt can't be delivered in creation");
            throw new QRValidationException("Receipt can't be delivered in creation");
        }

        if (this.receiptStatus == ReceiptStatus.REFUNDED) {
            if (this.orderStatus == OrderStatus.DELIVERED) {
                log.error("Receipt can't be refunded and delivered in creation");
                throw new QRValidationException("Receipt can't be refunded and delivered in creation");
            }
            log.error("Receipt can't be refunded in creation");
            throw new QRValidationException("Receipt can't be refunded in creation");
        }

        if (this.orderStatus == OrderStatus.DELIVERED) {
            if (this.receiptStatus == ReceiptStatus.PENDING) {
                log.error("Receipt can't be pending and delivered");
                throw new QRValidationException("Receipt can't be pending and delivered");
            }
            log.error("Receipt can't be delivered in creation");
            throw new QRValidationException("Receipt can't be delivered in creation");
        }
    }

    private void validatePaymentMethodRules(Date paymentProcessedDate, Date receiptGeneratedDate) throws QRValidationException {
        if (this.paymentMethodType == PaymentMethodType.CASH) {
            validateCashRules(paymentProcessedDate, receiptGeneratedDate);
        } else if (this.paymentMethodType == PaymentMethodType.BANK || this.paymentMethodType == PaymentMethodType.WALLET) {
            validateBankWalletRules(paymentProcessedDate, receiptGeneratedDate);
        }

        if (this.paymentMethodType == PaymentMethodType.CASH && this.receiptStatus == ReceiptStatus.PAYED) {
            log.error("Receipt can't be payed as cash in creation");
            throw new QRValidationException("Receipt can't be payed as cash in creation");
        }
    }

    private void validateCashRules(Date paymentProcessedDate, Date receiptGeneratedDate) throws QRValidationException {
        if (receiptGeneratedDate.after(new Date())) {
            log.error("Receipt generated date can't be after today if paying in cash");
            throw new QRValidationException("QR Code is not valid");
        }

        if (paymentProcessedDate.before(receiptGeneratedDate)) {
            log.error("Payment processed at can't be before receipt generated date for CASH");
            throw new QRValidationException("Payment processed at can't be before receipt generated date");
        }
    }

    private void validateBankWalletRules(Date paymentProcessedDate, Date receiptGeneratedDate) throws QRValidationException {
        if (receiptGeneratedDate.before(paymentProcessedDate)) {
            log.error("Receipt generated date can't be before payment processed at");
            throw new QRValidationException("QR Code is not valid");
        }

        if (paymentProcessedDate.after(receiptGeneratedDate)) {
            String method = this.paymentMethodType == PaymentMethodType.BANK ? "BANK" : "WALLET";
            log.error("Payment processed at can't be after receipt generated date for {}", method);
            throw new QRValidationException("Payment processed at can't be after receipt generated date");
        }
    }
}
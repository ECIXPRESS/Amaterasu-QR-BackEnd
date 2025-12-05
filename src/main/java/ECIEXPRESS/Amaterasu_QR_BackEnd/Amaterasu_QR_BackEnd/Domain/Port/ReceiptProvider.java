package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Port;

public interface ReceiptProvider {
    String getQrCodeByOrderId(String orderId);
}

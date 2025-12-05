package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Mappers;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Dto.ReceiptRequests.GetQrReceiptRequest;
import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Clients.Receipt.Dto.ReceiptResponses.GetQrReceiptResponse;

public final class ReceiptProviderMapper {
    private ReceiptProviderMapper() {
    }
    public static String mapQrResponseToString(GetQrReceiptResponse response){
        return response.QRCode();
    }
    public static GetQrReceiptResponse mapStringToQrResponse(String qrCode){
        return new GetQrReceiptResponse(qrCode);
    }
    public static GetQrReceiptRequest mapToGetQrReceiptRequest(String orderId){
        return new GetQrReceiptRequest(orderId);
    }
}

package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.Mappers;

import ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Infrastructure.Web.Dto.QrResponses.CreateQrCodeResponse;


public class QrCodeMapper {
    public static CreateQrCodeResponse QrStringToCreateQrCodeResponse(String qrCode) {
        return new CreateQrCodeResponse(qrCode);
    }

}

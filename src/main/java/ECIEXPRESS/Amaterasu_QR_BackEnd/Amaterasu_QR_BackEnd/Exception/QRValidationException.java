package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Exception;


public class QRValidationException extends RuntimeException {
    public QRValidationException(String message) {
        super(message);
    }

    public QRValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

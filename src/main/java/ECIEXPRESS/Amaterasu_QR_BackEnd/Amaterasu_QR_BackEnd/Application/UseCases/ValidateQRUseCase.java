package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Application.UseCases;

/**
 * Input port (use case) responsible for validating a decoded or encoded QR string.
 *
 */
public interface ValidateQRUseCase {

    /**
     * Validates the provided encoded QR data and determines
     * whether it represents a valid and registered QR.
     *
     * @param encodedQr Base64 or binary QR representation
     * @return true if the QR is valid, false otherwisemvn
     */
    boolean validate(String encodedQr);
}

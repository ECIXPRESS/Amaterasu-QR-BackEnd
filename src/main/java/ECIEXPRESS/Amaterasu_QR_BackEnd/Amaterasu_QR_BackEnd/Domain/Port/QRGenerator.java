/**
 * Port responsible for generating QR codes.
 * Implementations may encode the input text into PNG,
 * Base64, byte arrays or any desired format.
 */
public interface QRGenerator {

    /**
     * Generates a QR code based on the provided text.
     *
     * @param text information to be encoded inside the QR
     * @return byte array representing the generated QR (PNG or equivalent)
     */
    byte[] generate(String text);
}

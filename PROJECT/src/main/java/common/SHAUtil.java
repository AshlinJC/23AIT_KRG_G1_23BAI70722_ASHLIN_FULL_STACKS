package common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHAUtil provides SHA-256 hashing utilities.
 * Used for integrity verification of model parameters during federated learning.
 */
public class SHAUtil {

    private static final String ALGORITHM = "SHA-256";

    /**
     * Computes the SHA-256 hash of the given byte array.
     *
     * @param data The data to hash
     * @return The SHA-256 hash as a hex string
     */
    public static String hash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = digest.digest(data);
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Converts a byte array to its hexadecimal string representation.
     *
     * @param bytes The byte array to convert
     * @return The hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Verifies that the given data matches the expected hash.
     *
     * @param data         The data to verify
     * @param expectedHash The expected SHA-256 hash
     * @return true if the computed hash matches the expected hash
     */
    public static boolean verify(byte[] data, String expectedHash) {
        String computedHash = hash(data);
        return computedHash.equals(expectedHash);
    }
}

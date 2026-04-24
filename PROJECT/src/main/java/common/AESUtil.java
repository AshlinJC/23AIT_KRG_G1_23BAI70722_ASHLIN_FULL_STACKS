package common;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AESUtil provides AES-128 symmetric encryption and decryption utilities.
 * Used to secure model parameters during transmission between clients and server.
 */
public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128; // AES-128 bit key

    // Shared secret key (in production, this would be securely exchanged)
    private static final byte[] SHARED_KEY = generateFixedKey();

    /**
     * Generates a fixed AES key for demonstration purposes.
     * In a real system, key exchange protocols (e.g., Diffie-Hellman) would be used.
     */
    private static byte[] generateFixedKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            // Use a fixed seed for reproducibility in this demo
            return "FedLearn128Key!!".getBytes(); // Exactly 16 bytes for AES-128
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES algorithm not available", e);
        }
    }

    /**
     * Encrypts the given plaintext byte array using AES-128.
     *
     * @param data The plaintext data to encrypt
     * @return The encrypted byte array
     * @throws Exception If encryption fails
     */
    public static byte[] encrypt(byte[] data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SHARED_KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] ciphertext = cipher.doFinal(data);

        byte[] output = new byte[16 + ciphertext.length];
        System.arraycopy(iv, 0, output, 0, 16);
        System.arraycopy(ciphertext, 0, output, 16, ciphertext.length);
        return output;
    }

    /**
     * Decrypts the given ciphertext byte array using AES-128.
     *
     * @param encryptedData The encrypted data to decrypt
     * @return The decrypted plaintext byte array
     * @throws Exception If decryption fails
     */
    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SHARED_KEY, ALGORITHM);
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);
        byte[] ciphertext = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        return cipher.doFinal(ciphertext);
    }

    /**
     * Returns the shared secret key bytes.
     * Provided for scenarios where the key needs to be accessed directly.
     */
    public static byte[] getSharedKey() {
        return SHARED_KEY.clone();
    }
}

package client;

import common.AESUtil;
import common.ModelParameters;
import common.SHAUtil;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MaliciousClient simulates an attacker in the federated learning system.
 * It sends tampered data to test the server's ability to detect and reject
 * malicious participants.
 *
 * Two attack modes are supported:
 * 1. WRONG_HASH: Sends properly encrypted data but with an incorrect hash
 * 2. NO_ENCRYPTION: Sends raw (unencrypted) data to the server
 */
public class MaliciousClient {

    /**
     * Enum defining the types of attacks a malicious client can perform.
     */
    public enum AttackType {
        WRONG_HASH, // Send data with tampered hash
        NO_ENCRYPTION // Send raw unencrypted data
    }

    private final String clientId;
    private final String serverHost;
    private final int serverPort;
    private final AttackType attackType;

    /**
     * Constructs a MaliciousClient.
     *
     * @param clientId   Identifier for this malicious client
     * @param serverHost Server hostname
     * @param serverPort Server port
     * @param attackType Type of attack to perform
     */
    public MaliciousClient(String clientId, String serverHost, int serverPort, AttackType attackType) {
        this.clientId = clientId;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.attackType = attackType;
    }

    /**
     * Executes the malicious client's attack.
     * Depending on the attack type, it either sends data with an incorrect hash
     * or sends unencrypted raw data that the server should reject.
     */
    public void run() {
        try {
            System.out.println("[" + clientId + "] Starting MALICIOUS client (Attack: " + attackType + ")...");

            switch (attackType) {
                case WRONG_HASH:
                    sendWithWrongHash();
                    break;
                case NO_ENCRYPTION:
                    sendWithoutEncryption();
                    break;
            }

        } catch (Exception e) {
            System.err.println("[" + clientId + "] Error during attack: " + e.getMessage());
        }
    }

    /**
     * Attack Mode 1: WRONG_HASH
     * Creates fake tree data, computes a valid hash, then deliberately tampers
     * with the hash before encryption and sending. The server should detect
     * the hash mismatch during verification.
     */
    private void sendWithWrongHash() throws Exception {
        System.out.println("[" + clientId + "] Creating fake model with WRONG HASH...");

        // Create fake serialized tree data
        List<byte[]> fakeTrees = new ArrayList<>();
        Random random = new Random(42);
        for (int i = 0; i < 3; i++) {
            byte[] fakeTree = new byte[100];
            random.nextBytes(fakeTree);
            fakeTrees.add(fakeTree);
        }

        // Compute the real hash, then deliberately corrupt it
        byte[] allData = concatenateByteArrays(fakeTrees);
        String realHash = SHAUtil.hash(allData);
        String tamperedHash = "TAMPERED_" + realHash.substring(9); // Corrupt the hash
        System.out.println("[" + clientId + "] Original hash: " + realHash.substring(0, 16) + "...");
        System.out.println("[" + clientId + "] Tampered hash: " + tamperedHash.substring(0, 16) + "...");

        // Create ModelParameters with the tampered hash
        ModelParameters params = new ModelParameters(fakeTrees, tamperedHash);

        // Serialize and encrypt (encryption is valid, but hash is wrong)
        byte[] serializedParams = serializeObject(params);
        byte[] encryptedData = AESUtil.encrypt(serializedParams);

        // Send to server - server should reject due to hash mismatch
        System.out.println("[" + clientId + "] Sending encrypted data with WRONG HASH to server...");
        sendToServer(encryptedData);
        System.out.println("[" + clientId + "] Tampered data sent.");
    }

    /**
     * Attack Mode 2: NO_ENCRYPTION
     * Sends raw, unencrypted string data instead of properly encrypted byte[].
     * The server should reject this because decryption will fail.
     */
    private void sendWithoutEncryption() throws Exception {
        System.out.println("[" + clientId + "] Sending RAW UNENCRYPTED data to server...");

        // Send a raw string instead of encrypted byte[]
        // The server expects byte[], so this should cause a type check failure
        // or decryption failure
        String maliciousPayload = "THIS_IS_MALICIOUS_UNENCRYPTED_DATA";

        try (Socket socket = new Socket(serverHost, serverPort);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            // Send a String object instead of byte[] — server should reject
            oos.writeObject(maliciousPayload);
            oos.flush();
        }
        System.out.println("[" + clientId + "] Raw unencrypted data sent.");
    }

    /**
     * Concatenates multiple byte arrays into one.
     */
    private byte[] concatenateByteArrays(List<byte[]> byteArrays) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte[] arr : byteArrays) {
            baos.write(arr, 0, arr.length);
        }
        return baos.toByteArray();
    }

    /**
     * Serializes a Java object to byte array.
     */
    private byte[] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        return baos.toByteArray();
    }

    /**
     * Sends data to the server via TCP socket.
     */
    private void sendToServer(byte[] data) throws IOException {
        try (Socket socket = new Socket(serverHost, serverPort);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject(data);
            oos.flush();
        }
    }
}

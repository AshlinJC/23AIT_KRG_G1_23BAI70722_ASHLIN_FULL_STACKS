package server;

import common.AESUtil;
import common.ModelParameters;
import common.SHAUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * FederatedServer is the central aggregation server for the federated learning
 * system.
 * It accepts connections from multiple clients, validates incoming data,
 * decrypts model parameters, verifies integrity, and aggregates decision trees
 * into a global Random Forest model.
 *
 * Security checks performed:
 * 1. Type validation - ensures received data is byte[]
 * 2. AES decryption - data must be validly encrypted
 * 3. SHA-256 hash verification - integrity of model parameters
 */
public class FederatedServer {

    private final int port;
    private final int expectedClients;
    private final List<ModelParameters> aggregatedModels;
    private final List<byte[]> globalForest; // All decision trees from all clients
    private final AtomicBoolean running;
    private ServerSocket serverSocket;
    private CountDownLatch clientLatch;

    /**
     * Constructs a FederatedServer.
     *
     * @param port            The port to listen on
     * @param expectedClients Number of clients expected (including malicious)
     */
    public FederatedServer(int port, int expectedClients) {
        this.port = port;
        this.expectedClients = expectedClients;
        this.aggregatedModels = new CopyOnWriteArrayList<>();
        this.globalForest = Collections.synchronizedList(new ArrayList<>());
        this.running = new AtomicBoolean(true);
    }

    /**
     * Starts the server and listens for client connections.
     * Each client connection is handled in a separate thread.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(30000); // 30 second timeout
            System.out.println("[SERVER] Federated Learning Server started on port " + port);
            System.out.println("[SERVER] Waiting for " + expectedClients + " client connections...");
            System.out.println("=".repeat(60));

            clientLatch = new CountDownLatch(expectedClients);
            int clientCount = 0;

            while (running.get() && clientCount < expectedClients) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientCount++;
                    final int clientNum = clientCount;
                    System.out.println("\n[SERVER] Client #" + clientNum + " connected from " +
                            clientSocket.getInetAddress().getHostAddress());

                    // Handle each client in a separate thread
                    Thread handler = new Thread(() -> handleClient(clientSocket, clientNum));
                    handler.setDaemon(true);
                    handler.start();
                } catch (SocketTimeoutException e) {
                    System.out.println("[SERVER] Timeout waiting for clients. Proceeding with received models.");
                    break;
                }
            }

            // Wait for all client handlers to complete (with timeout)
            try {
                clientLatch.await(15, java.util.concurrent.TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        } catch (IOException e) {
            System.err.println("[SERVER] Error: " + e.getMessage());
        } finally {
            stop();
        }
    }

    /**
     * Handles a single client connection. Performs security validation pipeline:
     * 1. Read object from socket
     * 2. Validate it is a byte array (reject non-byte[] data)
     * 3. Decrypt using AES
     * 4. Deserialize into ModelParameters
     * 5. Verify SHA-256 hash integrity
     * 6. Accept and aggregate the model
     *
     * @param clientSocket The client's socket connection
     * @param clientNum    The client number for logging
     */
    private void handleClient(Socket clientSocket, int clientNum) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {

            // Step 1: Read the object from the client
            Object received = ois.readObject();

            // Step 2: Validate that received data is byte[] (reject non-byte[] data)
            if (!(received instanceof byte[])) {
                System.out.println("[SERVER] ✗ REJECTED Client #" + clientNum +
                        " - Received data is NOT byte[]. Type: " + received.getClass().getSimpleName());
                System.out.println("[SERVER]   Reason: Malicious client detected - invalid data type!");
                return;
            }

            byte[] encryptedData = (byte[]) received;
            System.out.println("[SERVER] Client #" + clientNum + " - Received " +
                    encryptedData.length + " bytes of encrypted data.");

            // Step 3: Decrypt the data using AES
            byte[] decryptedData;
            try {
                decryptedData = AESUtil.decrypt(encryptedData);
                System.out.println("[SERVER] Client #" + clientNum + " - AES decryption successful.");
            } catch (Exception e) {
                System.out.println("[SERVER] ✗ REJECTED Client #" + clientNum +
                        " - AES decryption FAILED!");
                System.out.println("[SERVER]   Reason: Data was not properly encrypted. " + e.getMessage());
                return;
            }

            // Step 4: Deserialize into ModelParameters
            ModelParameters params;
            try {
                params = (ModelParameters) deserializeObject(decryptedData);
                System.out.println("[SERVER] Client #" + clientNum + " - Deserialization successful. " +
                        "Trees received: " + params.getTreeCount());
            } catch (Exception e) {
                System.out.println("[SERVER] ✗ REJECTED Client #" + clientNum +
                        " - Deserialization FAILED!");
                System.out.println("[SERVER]   Reason: Invalid model parameters format.");
                return;
            }

            // Step 5: Verify SHA-256 hash integrity
            byte[] allTreeData = concatenateByteArrays(params.getSerializedTrees());
            String computedHash = SHAUtil.hash(allTreeData);
            String receivedHash = params.getHash();

            if (!SHAUtil.verify(allTreeData, receivedHash)) {
                System.out.println("[SERVER] ✗ REJECTED Client #" + clientNum +
                        " - Hash verification FAILED!");
                System.out.println("[SERVER]   Expected: " + computedHash.substring(0, 16) + "...");
                System.out.println("[SERVER]   Received: " + receivedHash.substring(0, 16) + "...");
                System.out.println("[SERVER]   Reason: Data integrity compromised - possible tampering!");
                return;
            }

            // Step 6: All checks passed — accept and aggregate the model
            System.out.println("[SERVER] ✓ ACCEPTED Client #" + clientNum +
                    " - All security checks passed!");
            System.out.println("[SERVER]   Hash verified: " + computedHash.substring(0, 16) + "...");
            aggregatedModels.add(params);
            globalForest.addAll(params.getSerializedTrees());

        } catch (Exception e) {
            System.err.println("[SERVER] Error handling Client #" + clientNum + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignore close errors
            }
            clientLatch.countDown();
        }
    }

    /**
     * Aggregates all accepted models and prints statistics.
     */
    public void aggregateModels() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("[SERVER] === MODEL AGGREGATION RESULTS ===");
        System.out.println("[SERVER] Total accepted clients: " + aggregatedModels.size());
        System.out.println("[SERVER] Total aggregated decision trees: " + globalForest.size());

        if (aggregatedModels.isEmpty()) {
            System.out.println("[SERVER] WARNING: No valid models received for aggregation.");
        } else {
            for (int i = 0; i < aggregatedModels.size(); i++) {
                ModelParameters m = aggregatedModels.get(i);
                System.out.println("[SERVER]   Model " + (i + 1) + ": " + m.getTreeCount() + " trees");
            }
        }
        System.out.println("=".repeat(60));
    }

    /**
     * Performs majority voting prediction across all aggregated trees.
     * Each deserialized tree casts a vote, and the class with the most votes wins.
     *
     * @param features The feature vector to predict on
     * @return The predicted class label
     */
    public int predict(double[] features) {
        if (globalForest.isEmpty()) {
            throw new IllegalStateException("No models aggregated yet. Cannot predict.");
        }

        int[] votes = new int[2]; // Binary classification (0 or 1)

        for (byte[] treeBytes : globalForest) {
            try {
                // Deserialize the decision tree
                Object tree = deserializeObject(treeBytes);

                // Use reflection to call the predict method on the tree
                // Smile's DecisionTree has a predict(double[]) method
                java.lang.reflect.Method predictMethod = tree.getClass().getMethod("predict",
                        smile.data.Tuple.class);

                // Create a Tuple from features for Smile's API
                smile.data.type.StructField[] fields = new smile.data.type.StructField[features.length];
                for (int i = 0; i < features.length; i++) {
                    fields[i] = new smile.data.type.StructField("feature" + (i + 1),
                            smile.data.type.DataTypes.DoubleType);
                }
                smile.data.type.StructType schema = new smile.data.type.StructType(fields);
                smile.data.Tuple tuple = smile.data.Tuple.of(features, schema);

                int prediction = (int) predictMethod.invoke(tree, tuple);

                if (prediction >= 0 && prediction < votes.length) {
                    votes[prediction]++;
                }
            } catch (Exception e) {
                // Skip trees that fail to predict
                System.err.println("[SERVER] Warning: Tree prediction failed: " + e.getMessage());
            }
        }

        // Return the class with the most votes (majority voting)
        return votes[0] >= votes[1] ? 0 : 1;
    }

    /**
     * Calculates prediction accuracy on a test dataset using majority voting.
     *
     * @param testFeatures 2D array of test feature vectors
     * @param testLabels   Array of true labels
     * @return The accuracy as a percentage
     */
    public double calculateAccuracy(double[][] testFeatures, int[] testLabels) {
        if (globalForest.isEmpty()) {
            System.out.println("[SERVER] No models to evaluate. Accuracy: 0.0%");
            return 0.0;
        }

        int correct = 0;
        int total = testFeatures.length;

        for (int i = 0; i < total; i++) {
            int prediction = predict(testFeatures[i]);
            if (prediction == testLabels[i]) {
                correct++;
            }
        }

        double accuracy = (double) correct / total * 100.0;
        return accuracy;
    }

    /**
     * Stops the server and closes the server socket.
     */
    public void stop() {
        running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    /**
     * Returns the list of aggregated model parameters.
     */
    public List<ModelParameters> getAggregatedModels() {
        return aggregatedModels;
    }

    /**
     * Returns the global forest (all aggregated trees).
     */
    public List<byte[]> getGlobalForest() {
        return globalForest;
    }

    /**
     * Deserializes a byte array back into a Java object.
     */
    private Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    /**
     * Concatenates a list of byte arrays into a single byte array.
     */
    private byte[] concatenateByteArrays(List<byte[]> byteArrays) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte[] arr : byteArrays) {
            baos.write(arr, 0, arr.length);
        }
        return baos.toByteArray();
    }
}

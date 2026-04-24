import client.FederatedClient;
import client.MaliciousClient;
import common.CSVReaderUtil;
import server.FederatedServer;

/**
 * Main entry point for the Secure Federated Learning System demo.
 *
 * This orchestrator:
 * 1. Starts the federated server in a background thread
 * 2. Launches legitimate clients that train and securely send models
 * 3. Launches malicious clients to test server rejection
 * 4. Triggers model aggregation on the server
 * 5. Evaluates and prints the global model's prediction accuracy
 */
public class Main {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9876;
    private static final String CLIENT1_DATA_FILE = "client1_diabetes.csv";
    private static final String CLIENT2_DATA_FILE = "client2_diabetes.csv";
    private static final String EVAL_DATA_FILE = "diabetes.csv";

    // Total expected clients: 2 legitimate + 2 malicious = 4
    private static final int TOTAL_CLIENTS = 4;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     SECURE FEDERATED LEARNING SYSTEM - RANDOM FOREST    ║");
        System.out.println("║         Using Smile ML | AES-128 | SHA-256              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        // Create the federated server
        FederatedServer server = new FederatedServer(SERVER_PORT, TOTAL_CLIENTS);

        // Step 1: Start the server in a background thread
        System.out.println(">>> PHASE 1: Starting Federated Server");
        System.out.println("=".repeat(60));
        Thread serverThread = new Thread(() -> server.start());
        serverThread.setDaemon(true);
        serverThread.start();

        // Give the server time to initialize
        sleep(1000);

        // Step 2: Launch legitimate clients
        System.out.println("\n>>> PHASE 2: Launching Legitimate Clients");
        System.out.println("=".repeat(60));

        // Client 1: Trains on its private partitioned dataset
        Thread client1Thread = new Thread(() -> {
            FederatedClient client1 = new FederatedClient(
                    "Client-1", SERVER_HOST, SERVER_PORT, CLIENT1_DATA_FILE);
            client1.run();
        });

        // Client 2: Trains on its private partitioned dataset
        Thread client2Thread = new Thread(() -> {
            FederatedClient client2 = new FederatedClient(
                    "Client-2", SERVER_HOST, SERVER_PORT, CLIENT2_DATA_FILE);
            client2.run();
        });

        client1Thread.start();
        sleep(500); // Stagger client starts
        client2Thread.start();

        // Wait for legitimate clients to finish
        joinThread(client1Thread, 30000);
        joinThread(client2Thread, 30000);

        // Step 3: Launch malicious clients
        System.out.println("\n>>> PHASE 3: Launching Malicious Clients (Testing Server Security)");
        System.out.println("=".repeat(60));

        // Malicious Client 1: Sends data with wrong hash
        Thread malicious1Thread = new Thread(() -> {
            MaliciousClient mal1 = new MaliciousClient(
                    "Malicious-1", SERVER_HOST, SERVER_PORT,
                    MaliciousClient.AttackType.WRONG_HASH);
            mal1.run();
        });

        // Malicious Client 2: Sends unencrypted raw data
        Thread malicious2Thread = new Thread(() -> {
            MaliciousClient mal2 = new MaliciousClient(
                    "Malicious-2", SERVER_HOST, SERVER_PORT,
                    MaliciousClient.AttackType.NO_ENCRYPTION);
            mal2.run();
        });

        malicious1Thread.start();
        sleep(500);
        malicious2Thread.start();

        // Wait for malicious clients to finish
        joinThread(malicious1Thread, 15000);
        joinThread(malicious2Thread, 15000);

        // Give server time to process all clients
        sleep(2000);

        // Step 4: Aggregate models on the server
        System.out.println("\n>>> PHASE 4: Model Aggregation");
        server.aggregateModels();

        // Step 5: Evaluate the global model's accuracy
        System.out.println("\n>>> PHASE 5: Global Model Evaluation");
        System.out.println("=".repeat(60));
        evaluateGlobalModel(server);

        // Final summary
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              FEDERATED LEARNING COMPLETE                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        // Stop the server
        server.stop();
    }

    /**
     * Evaluates the global aggregated model on test data and prints accuracy.
     */
    private static void evaluateGlobalModel(FederatedServer server) {
        try {
            // Load test data (using the original full dataset for global evaluation in this
            // demo)
            double[][] dataset = CSVReaderUtil.loadDataset(EVAL_DATA_FILE);

            // Use the last 20% as test data
            double[][][] split = CSVReaderUtil.splitDataset(dataset, 0.8);
            double[][] testSet = split[1];
            double[][] testFeatures = CSVReaderUtil.extractFeatures(testSet);
            int[] testLabels = CSVReaderUtil.extractLabels(testSet);

            System.out.println("[EVAL] Test samples: " + testFeatures.length);

            // Calculate accuracy using majority voting across all aggregated trees
            double accuracy = server.calculateAccuracy(testFeatures, testLabels);

            System.out.println("[EVAL] ══════════════════════════════════════");
            System.out.printf("[EVAL]   Final Model Accuracy: %.2f%%%n", accuracy);
            System.out.println("[EVAL] ══════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("[EVAL] Error during evaluation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sleeps for the specified milliseconds, handling InterruptedException.
     */
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Joins a thread with a timeout, handling InterruptedException.
     */
    private static void joinThread(Thread thread, long timeout) {
        try {
            thread.join(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

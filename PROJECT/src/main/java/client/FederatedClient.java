package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import common.AESUtil;
import common.CSVReaderUtil;
import common.ModelParameters;
import common.SHAUtil;
import smile.classification.RandomForest;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.data.vector.IntVector;

/**
 * FederatedClient represents a legitimate participant in the federated learning
 * system.
 * It loads local data, trains a Random Forest model using Smile ML,
 * extracts decision trees, encrypts them, and sends them securely to the
 * server.
 */
public class FederatedClient {

    private final String clientId;
    private final String serverHost;
    private final int serverPort;
    private final String dataFile;

    /**
     * Constructs a FederatedClient.
     *
     * @param clientId   Unique identifier for this client
     * @param serverHost The server hostname
     * @param serverPort The server port
     * @param dataFile   The CSV data file name in resources/data/
     */
    public FederatedClient(String clientId, String serverHost, int serverPort, String dataFile) {
        this.clientId = clientId;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.dataFile = dataFile;
    }

    /**
     * Executes the full federated learning client workflow:
     * 1. Load dataset from CSV
     * 2. Train Random Forest model locally
     * 3. Extract and serialize decision trees
     * 4. Compute SHA-256 hash for integrity
     * 5. Encrypt model parameters using AES
     * 6. Send encrypted data to server via Socket
     */
    public void run() {
        try {
            System.out.println("[" + clientId + "] Starting client...");

            // Step 1: Load and prepare the dataset
            System.out.println("[" + clientId + "] Loading dataset: " + dataFile);
            double[][] dataset = CSVReaderUtil.loadDataset(dataFile);
            double[][] features = CSVReaderUtil.extractFeatures(dataset);
            int[] labels = CSVReaderUtil.extractLabels(dataset);

            // Step 2: Train Random Forest model using Smile ML
            System.out.println("[" + clientId + "] Training Random Forest model...");
            RandomForest model = trainRandomForest(features, labels);
            int numTrees = model.trees().length;
            System.out.println("[" + clientId + "] Training complete. Number of trees: " + numTrees);

            // Step 3: Extract and serialize each decision tree
            System.out.println("[" + clientId + "] Serializing decision trees...");
            List<byte[]> serializedTrees = serializeTrees(model);
            System.out.println("[" + clientId + "] Serialized " + serializedTrees.size() + " trees.");

            // Step 4: Compute SHA-256 hash of all serialized tree data
            byte[] allTreeData = concatenateByteArrays(serializedTrees);
            String hash = SHAUtil.hash(allTreeData);
            System.out.println("[" + clientId + "] Computed SHA-256 hash: " + hash.substring(0, 16) + "...");

            // Step 5: Create ModelParameters and serialize it
            ModelParameters params = new ModelParameters(serializedTrees, hash);
            byte[] serializedParams = serializeObject(params);

            // Step 6: Encrypt the serialized ModelParameters using AES-128
            System.out.println("[" + clientId + "] Encrypting model parameters with AES-128...");
            byte[] encryptedData = AESUtil.encrypt(serializedParams);
            System.out.println(
                    "[" + clientId + "] Encryption complete. Encrypted size: " + encryptedData.length + " bytes");

            // Step 7: Send encrypted data to the server via Socket
            System.out.println("[" + clientId + "] Connecting to server at " + serverHost + ":" + serverPort + "...");
            sendToServer(encryptedData);
            System.out.println("[" + clientId + "] Data sent successfully!");

        } catch (Exception e) {
            System.err.println("[" + clientId + "] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Trains a Random Forest classifier using Smile ML.
     *
     * @param features 2D array of feature values
     * @param labels   Array of class labels
     * @return Trained RandomForest model
     */
    private RandomForest trainRandomForest(double[][] features, int[] labels) {
        // Build a DataFrame for Smile
        int numFeatures = features[0].length;
        String[] featureNames = new String[numFeatures];
        for (int i = 0; i < numFeatures; i++) {
            featureNames[i] = "feature" + (i + 1);
        }

        // Create struct fields for schema
        StructField[] fields = new StructField[numFeatures + 1];
        for (int i = 0; i < numFeatures; i++) {
            fields[i] = new StructField(featureNames[i], DataTypes.DoubleType);
        }
        fields[numFeatures] = new StructField("label", DataTypes.IntegerType);

        StructType schema = new StructType(fields);

        // Build DataFrame with features and label
        DataFrame df = DataFrame.of(features, featureNames);
        df = df.merge(IntVector.of("label", labels));

        // Train Random Forest with the formula: label ~ .
        Formula formula = Formula.lhs("label");
        return RandomForest.fit(formula, df);
    }

    /**
     * Serializes each decision tree from the Random Forest into byte arrays.
     *
     * @param model The trained Random Forest model
     * @return List of serialized tree byte arrays
     */
    private List<byte[]> serializeTrees(RandomForest model) {
        List<byte[]> serializedTrees = new ArrayList<>();
        var trees = model.trees();

        for (var tree : trees) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(tree);
                oos.flush();
                serializedTrees.add(baos.toByteArray());
            } catch (IOException e) {
                System.err.println("[" + clientId + "] Warning: Failed to serialize a tree: " + e.getMessage());
            }
        }

        return serializedTrees;
    }

    /**
     * Concatenates a list of byte arrays into a single byte array.
     * Used for computing a single hash over all tree data.
     */
    private byte[] concatenateByteArrays(List<byte[]> byteArrays) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte[] arr : byteArrays) {
            baos.write(arr, 0, arr.length);
        }
        return baos.toByteArray();
    }

    /**
     * Serializes a Java object into a byte array.
     */
    private byte[] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        return baos.toByteArray();
    }

    /**
     * Sends encrypted byte data to the federated server via a TCP socket.
     *
     * @param encryptedData The AES-encrypted model data
     */
    private void sendToServer(byte[] encryptedData) throws IOException {
        try (Socket socket = new Socket(serverHost, serverPort);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            // Send the encrypted data as a byte array object
            oos.writeObject(encryptedData);
            oos.flush();
        }
    }
}

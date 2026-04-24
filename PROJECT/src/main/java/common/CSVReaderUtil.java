package common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVReaderUtil loads and parses CSV datasets from the resources/data folder.
 * Expected format: 8 numeric feature columns + 1 integer label column, with a
 * header row.
 */
public class CSVReaderUtil {

    /**
     * Loads a CSV dataset from the resources/data directory.
     *
     * @param fileName The name of the CSV file (e.g., "dataset.csv")
     * @return A 2D double array where each row contains 8 features followed by 1
     *         label
     */
    public static double[][] loadDataset(String fileName) {
        List<double[]> dataList = new ArrayList<>();
        String resourcePath = "data/" + fileName;

        try (InputStream is = CSVReaderUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Dataset file not found: " + resourcePath);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                // Skip the header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Parse comma-separated values into double array
                String[] values = line.split(",");
                double[] row = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Double.parseDouble(values[i].trim());
                }
                dataList.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading CSV file: " + fileName, e);
        }

        return dataList.toArray(new double[0][]);
    }

    /**
     * Extracts the feature columns (first 8 columns) from the dataset.
     *
     * @param dataset The full dataset (features + label)
     * @return A 2D double array of features only
     */
    public static double[][] extractFeatures(double[][] dataset) {
        int numFeatures = dataset[0].length - 1; // Last column is the label
        double[][] features = new double[dataset.length][numFeatures];
        for (int i = 0; i < dataset.length; i++) {
            System.arraycopy(dataset[i], 0, features[i], 0, numFeatures);
        }

        return features;
    }

    /**
     * Extracts the label column (last column) from the dataset.
     *
     * @param dataset The full dataset (features + label)
     * @return An integer array of labels
     */
    public static int[] extractLabels(double[][] dataset) {
        int labelIndex = dataset[0].length - 1;
        int[] labels = new int[dataset.length];
        for (int i = 0; i < dataset.length; i++) {
            labels[i] = (int) dataset[i][labelIndex];
        }
        return labels;
    }

    /**
     * Splits the dataset into two parts based on a ratio.
     *
     * @param dataset    The full dataset
     * @param trainRatio The ratio of data to use for training (e.g., 0.8 for 80%)
     * @return An array of two datasets: [trainSet, testSet]
     */
    public static double[][][] splitDataset(double[][] dataset, double trainRatio) {
        int trainSize = (int) (dataset.length * trainRatio);
        int testSize = dataset.length - trainSize;

        double[][] trainSet = new double[trainSize][];
        double[][] testSet = new double[testSize][];

        System.arraycopy(dataset, 0, trainSet, 0, trainSize);
        System.arraycopy(dataset, trainSize, testSet, 0, testSize);

        return new double[][][] { trainSet, testSet };
    }
}

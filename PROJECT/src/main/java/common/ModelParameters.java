package common;

import java.io.Serializable;
import java.util.List;

/**
 * ModelParameters is a Serializable container for transmitting trained model data.
 * It holds the serialized decision trees and their SHA-256 integrity hash.
 */
public class ModelParameters implements Serializable {

    private static final long serialVersionUID = 1L;

    /** List of serialized decision tree byte arrays */
    private List<byte[]> serializedTrees;

    /** SHA-256 hash of the concatenated serialized trees for integrity verification */
    private String hash;

    /**
     * Constructs a ModelParameters object.
     *
     * @param serializedTrees List of serialized decision trees as byte arrays
     * @param hash            SHA-256 hash of the serialized tree data
     */
    public ModelParameters(List<byte[]> serializedTrees, String hash) {
        this.serializedTrees = serializedTrees;
        this.hash = hash;
    }

    /**
     * Returns the list of serialized decision trees.
     */
    public List<byte[]> getSerializedTrees() {
        return serializedTrees;
    }

    /**
     * Sets the list of serialized decision trees.
     */
    public void setSerializedTrees(List<byte[]> serializedTrees) {
        this.serializedTrees = serializedTrees;
    }

    /**
     * Returns the SHA-256 hash string.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the SHA-256 hash string.
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Returns the total number of trees stored in this parameter object.
     */
    public int getTreeCount() {
        return serializedTrees != null ? serializedTrees.size() : 0;
    }

    @Override
    public String toString() {
        return "ModelParameters{treeCount=" + getTreeCount() + ", hash=" + hash + "}";
    }
}

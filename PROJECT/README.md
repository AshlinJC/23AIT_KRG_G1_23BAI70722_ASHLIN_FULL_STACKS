# Secure Federated Learning System

A Java 21 demonstration of secure federated learning using local Random Forest training (Smile ML), AES encryption for transport confidentiality, and SHA-256 hashing for integrity verification.

## Overview

This project simulates a federated learning workflow with:

- A central server that accepts model updates from clients
- Legitimate clients that train local models on private datasets
- Malicious clients that attempt protocol violations
- Server-side validation to reject invalid or tampered updates
- Aggregation of accepted decision trees and global accuracy evaluation

The main orchestrator is `Main` and runs the full demo end-to-end.

## Core Features

- Federated-style local model training with Smile Random Forest
- Serialization of local decision trees as model parameters
- AES-CBC encryption/decryption for payload transport (`AES/CBC/PKCS5Padding`)
- SHA-256 hash verification for payload integrity
- Malicious client simulation:
  - Wrong hash attack
  - No-encryption/type-mismatch attack
- Aggregation of accepted trees into a global voting ensemble

## Tech Stack

- Java 21
- Maven
- Smile ML (`smile-core` 3.0.1)

## Project Structure

```text
src/main/java/Main.java                    # Demo entry point and phase orchestration
src/main/java/server/FederatedServer.java  # Socket server, validation, aggregation, prediction
src/main/java/client/FederatedClient.java  # Legitimate client training + secure upload
src/main/java/client/MaliciousClient.java  # Attack simulation clients
src/main/java/common/AESUtil.java          # AES encryption/decryption utility
src/main/java/common/SHAUtil.java          # SHA-256 hash utility
src/main/java/common/CSVReaderUtil.java    # Dataset loading and parsing helpers
src/main/java/common/DataSplitter.java     # Dataset splitting helper for client CSV files
src/main/resources/data/*.csv              # Diabetes dataset files used by the demo
```

## How It Works

1. Server starts and waits for a configured number of clients.
2. Legitimate clients:
   - Load local CSV data
   - Train Random Forest locally
   - Serialize trees
   - Compute SHA-256 hash
   - Wrap in model payload and AES-encrypt
   - Send encrypted bytes to server
3. Malicious clients send malformed/tampered payloads.
4. Server validation pipeline per client:
   - Verify incoming object type (`byte[]` expected)
   - Decrypt payload with AES
   - Deserialize model parameters
   - Recompute and verify SHA-256 hash
5. Only accepted models are aggregated.
6. Global accuracy is computed by majority voting over aggregated trees.

## Prerequisites

- JDK 21 installed and available on PATH
- Maven installed and available on PATH

Check versions:

```bash
java -version
mvn -version
```

## Build

From the project root:

```bash
mvn clean compile
```

## Run

Use Maven Exec Plugin (configured in `pom.xml`):

```bash
mvn exec:java -Dexec.mainClass="Main"
```

You can also run in one step:

```bash
mvn clean compile exec:java -Dexec.mainClass="Main"
```

## Dataset Notes

- Source dataset files are in `src/main/resources/data/`.
- `Main` uses:
  - `client1_diabetes.csv`
  - `client2_diabetes.csv`
  - `diabetes.csv` (for evaluation split)
- `DataSplitter` can regenerate client files from the base dataset.

To run the splitter:

```bash
mvn -q exec:java -Dexec.mainClass="common.DataSplitter"
```

## Expected Console Flow

During execution, output is grouped in phases similar to:

- Phase 1: Server start
- Phase 2: Legitimate clients train and send encrypted models
- Phase 3: Malicious clients attempt attacks
- Phase 4: Model aggregation report
- Phase 5: Global model evaluation (accuracy)

The server logs accepted/rejected clients with reasons.

## Security Notes

This is a demo-oriented implementation.

- The AES key is fixed in code for reproducibility.
- Encryption uses AES-CBC with a fresh random 16-byte IV per message.
- Encrypted payload format is `IV || ciphertext` (first 16 bytes are IV).
- In production, use secure key management (KMS/HSM, key rotation, secure exchange).
- Transport is raw sockets without TLS; production deployments should use TLS.

## Troubleshooting

- `Dataset file not found`: verify CSV files exist under `src/main/resources/data/`.
- `ClassNotFoundException` during execution: run `mvn clean compile` first.
- `java` or `mvn` not recognized: install tools and fix PATH.

## License

No license file is currently included. Add a `LICENSE` file if you plan to distribute this project.

## Safe GitHub Publishing Checklist

Before pushing this project to a public repository, make sure the following are not included:

- Real secrets or keys (API keys, AES keys used in production, certificates, `.env` values).
- Sensitive/private datasets (especially health or personally identifiable information).
- Build outputs and generated artifacts (`target/`, `.class`, logs).

For this repository specifically:

- `src/main/java/common/AESUtil.java` currently uses a fixed demo key for reproducibility.
  - Keep this only for demonstration.
  - For real deployments, load keys from environment variables or a secret manager.
- Keep private datasets out of tracked folders.
  - Use `src/main/resources/data/private/` for non-public data (already ignored by `.gitignore`).

If sensitive files were committed earlier, removing them locally is not enough. Rewrite history before making the repo public.

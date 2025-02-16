package org.example;

import java.io.IOException;
import java.util.random.RandomGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generator of binary files.
 */
public class RandomFilesGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(RandomFilesGenerator.class);

    private final Storage storage;
    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();

    /**
     * Constructs this generator.
     * @param storage the storage where files will be written.
     */
    RandomFilesGenerator(Storage storage) {
        this.storage = storage;
    }

    /**
     * Generates binary files.
     * @param count the number of files to generate.
     * @param minBytes the minimum size of file in bytes.
     * @param maxBytes the maximum size of file in bytes.
     * @throws IOException I/O error occurred while generating files.
     */
    void generateFiles(int count, long minBytes, long maxBytes) throws IOException {
        for (int i = 1; i <= count; i++) {
            generateFile(String.format("%d.bin", i),
                    randomGenerator.nextLong(minBytes, maxBytes));
        }
    }

    private void generateFile(String path, long size) throws IOException {
        var bytes = new byte[64 * 1024];
        long remaining = size;
        try (var os = storage.openOutputStream(path)) {
            while (remaining > 0) {
                randomGenerator.nextBytes(bytes);
                int bytesToWrite = (size > bytes.length) ? bytes.length :  (int)remaining;
                os.write(bytes, 0, bytesToWrite);
                remaining -= bytesToWrite;
            }
        }
        LOG.info("{} was generated with size: {}", path, size);
    }
}

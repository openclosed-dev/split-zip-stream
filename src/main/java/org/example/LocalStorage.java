package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An implementation of {@link Storage}
 * which create streams from local filesystem.
 */
public class LocalStorage implements Storage {

    private final Path baseDir;

    /**
     * Constructs this storage.
     * @param baseDir the base directory in local filesystem
     */
    public LocalStorage(Path baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public InputStream openInputStream(String path) throws IOException {
        return Files.newInputStream(resolve(path));
    }

    @Override
    public OutputStream openOutputStream(String path) throws IOException {
        return Files.newOutputStream(resolve(path));
    }

    private Path resolve(String path) {
        return baseDir.resolve(path);
    }
}

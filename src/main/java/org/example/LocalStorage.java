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
class LocalStorage implements Storage {

    private final Path rootDir;

    /**
     * Constructs this storage.
     * @param rootDir the base directory in local filesystem
     */
    LocalStorage(Path rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Returns the root path of this storage.
     * @return the root path of this storage.
     */
    public Path getRootDir() {
        return rootDir;
    }

    @Override
    public InputStream openInputStream(String[] dirs, String filename) throws IOException {
        var resolved = resolve(dirs, filename);
        return Files.newInputStream(resolved);
    }

    @Override
    public OutputStream openOutputStream(String[] dirs, String filename) throws IOException {
        var resolved = resolve(dirs, filename);
        // Creates directories automatically if not exist
        Files.createDirectories(resolved.getParent());
        return Files.newOutputStream(resolved);
    }

    private Path resolve(String[] dirs, String filename) {
        var dir = rootDir;
        if (dirs.length > 0) {
            dir = dir.resolve(Path.of("", dirs));
        }
        return dir.resolve(filename);
    }
}

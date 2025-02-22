package org.example;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * An archiver to create archive files.
 */
public interface Archiver extends Closeable {

    /**
     * Adds an entry to archive.
     * @param name the name of the entry.
     * @param is input stream that provides the content of the entry.
     * This should be closed by caller.
     * @throws IOException an I/O error occurred while adding the entry.
     */
    public void addEntry(String name, InputStream is) throws IOException;
}

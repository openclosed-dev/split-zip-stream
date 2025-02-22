package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *  A storage which provides {@link InputStream} and {@link OutputStream}.
 *
 *  @see LocalStorage
 */
public interface Storage {

    /**
     * Creates an input stream at the specified path in this storage.
     * @param dirs the directory hierarchy of the stream.
     * @param filename the filename of the stream.
     * @return an input stream, which should be closed by caller.
     * @throws IOException I/O error occurred while opening the stream.
     */
    InputStream openInputStream(String[] dirs, String filename) throws IOException;

    /**
     * Creates an output stream at the specified path in this storage.
     * @param dirs the directory hierarchy of the stream.
     * @param filename the filename of the stream.
     * @return an output stream, which should be closed by caller.
     * @throws IOException IOException I/O error occurred while opening the stream.
     */
    OutputStream openOutputStream(String[] dirs, String filename) throws IOException;
}

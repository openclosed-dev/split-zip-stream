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
     * @param path the path of the stream.
     * @return an input stream
     * @throws IOException I/O error occurred while opening the stream.
     */
    InputStream openInputStream(String path) throws IOException;

    /**
     * Creates an output stream at the specified path in this storage.
     * @param path the path of the stream.
     * @return an output stream
     * @throws IOException IOException I/O error occurred while opening the stream.
     */
    OutputStream openOutputStream(String path) throws IOException;
}

package org.example;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link OutputStream} writing to multiple volumes.
 */
class SplitOutputStream extends OutputStream {

    private static final Logger LOG = LoggerFactory.getLogger(SplitOutputStream.class);

    private final String basePath;
    private final long maxVolumeSize;
    private final Storage storage;

    private int volumeCount;

    private String volumeName;
    private OutputStream volumeStream;
    private long volumeSize;

    /**
     * Constructs this stream.
     * @param basePath the base path of volumes.
     * @param maxVolumeSize the maximum volume size in bytes.
     * @param storage the storage used for creating new volumes.
     * @throws IOException I/O error occurred while writing to the storage.
     */
    SplitOutputStream(String basePath, long maxVolumeSize, Storage storage) throws IOException {
        this.basePath = basePath;
        this.maxVolumeSize = maxVolumeSize;
        this.storage = storage;
        this.volumeCount = 0;
        this.volumeStream = null;
    }

    @Override
    public void write(int b) throws IOException {
        var volumeStream = requireVolumeStream();
        volumeStream.write(b);
        volumeSize++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            var volumeStream = requireVolumeStream();
            int bytesToWrite = len;
            if (volumeSize + bytesToWrite >= maxVolumeSize) {
                bytesToWrite = (int)(maxVolumeSize - volumeSize);
            }
            volumeStream.write(b, off, bytesToWrite);
            volumeSize += bytesToWrite;
            off += bytesToWrite;
            len -= bytesToWrite;
        }
    }

    @Override
    public void flush() throws IOException {
        if (volumeStream != null) {
            volumeStream.flush();
        }
    }

    @Override
    public void close() throws IOException {
        closeVolumeStream();
    }

    /**
     * Returns the current volume stream which has space to fill.
     * @return the volume stream
     * @throws IOException I/O error occurred.
     */
    private OutputStream requireVolumeStream() throws IOException {
        if (volumeStream == null) {
            volumeStream = openVolumeStream();
        } else if (volumeSize >= maxVolumeSize) {
            closeVolumeStream();
            volumeStream = openVolumeStream();
        }
        return volumeStream;
    }

    private OutputStream openVolumeStream() throws IOException {
        volumeName = String.format("%s.%03d", basePath, ++volumeCount);
        volumeSize = 0;
        LOG.info("Creating new volume {}", volumeName);
        return storage.openOutputStream(volumeName);
    }

    private void closeVolumeStream() throws IOException {
        if (volumeStream == null) {
            return;
        }
        LOG.info("Closing volume {} size {}", volumeName, volumeSize);
        volumeStream.flush();
        volumeStream.close();
        volumeStream = null;
        volumeName = null;
        volumeSize = 0;
    }
}

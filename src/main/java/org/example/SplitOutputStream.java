package org.example;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link OutputStream} writing to multiple volumes.
 */
class SplitOutputStream extends OutputStream {

    private static final Logger LOG = LoggerFactory.getLogger(SplitOutputStream.class);

    private static final int BUFFER_SIZE = 64 * 1024;

    private final String[] dirs;
    private final String basename;
    private final long maxVolumeSize;
    private final Storage storage;

    private int volumeCount;

    private String volumeName;
    private OutputStream volumeStream;
    private long volumeSize;

    /**
     * Constructs this stream.
     *
     * @param dirs the directory hierarchy where the volumes will be stored.
     * @param basename the base name of the volume files.
     * @param maxVolumeSize the maximum volume size in bytes.
     * @param storage the storage used for creating new volumes.
     * @throws IllegalArgumentException if the value of {@code maxVolumeSize} is invalid.
     */
    SplitOutputStream(
            String[] dirs,
            String basename,
            long maxVolumeSize,
            Storage storage) {

        if (maxVolumeSize <= 0) {
            throw new IllegalArgumentException("maxVolumeSize must be positive number");
        }
        this.dirs = Arrays.copyOf(dirs, dirs.length);
        this.basename = basename;
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
        volumeName = String.format("%s.%03d", basename, ++volumeCount);
        volumeSize = 0;
        LOG.info("Creating new volume {}", volumeName);

        var raw = storage.openOutputStream(dirs, volumeName);
        return new BufferedOutputStream(raw, BUFFER_SIZE);
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

package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An archiver to generate a single archive of zip-format.
 */
public class ZipArchiver implements Archiver {

    private static final Logger LOG = LoggerFactory.getLogger(ZipArchiver.class);

    private final ZipOutputStream zos;

    public ZipArchiver(String[] outputDirs, String filename, Storage outputStorage) throws IOException {
        this(outputStorage.openOutputStream(outputDirs, filename));
    }

    protected ZipArchiver(OutputStream os) {
        this.zos = new ZipOutputStream(os, StandardCharsets.UTF_8);
    }

    @Override
    public void addEntry(String name, InputStream is) throws IOException {
        LOG.info("Adding an entry \"{}\" from input stream", name);
        var entry = new ZipEntry(name);
        zos.putNextEntry(entry);
        is.transferTo(zos);
    }

    @Override
    public void close() throws IOException {
        zos.close();
    }
}

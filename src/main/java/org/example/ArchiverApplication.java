package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * An example application for creating multi-volume archive.
 */
@SpringBootApplication
public class ArchiverApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiverApplication.class);

    // Path to input directory
    private static final Path INPUT_PATH = Path.of("target", "input");
    // Path to output directory
    private static final Path OUTPUT_PATH = Path.of("target", "output");

    // The number of entry files
    private static final int ENTRY_COUNT = 8;
    // Minimum size of entry file in bytes
    private static final long MIN_ENTRY_FILE_SZIE = 0L;
    // Maximum size of entry file in bytes
    private static final long MAX_ENTRY_FILE_SZIE = 8L * 1024 * 1024 * 1024;

    // Maximum volume size in bytes
    private static final long MAX_VOLUME_SIZE = 2L * 1024 * 1024 * 1024;

    // Storage where input files reside
    private final Storage inputStorage;
    // Storage where output files will be written
    private final Storage outputStorage;

    /**
     * The entry point of this application.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(ArchiverApplication.class, args);
    }

    /**
     * Constructs this application.
     */
    public ArchiverApplication() {
        this.inputStorage = new LocalStorage(INPUT_PATH);
        this.outputStorage = new LocalStorage(OUTPUT_PATH);
    }

    /**
     * Runs this application.
     * @param args the arguments given to this application.
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            generateEntryFilesIfNotExists();
            Files.createDirectories(OUTPUT_PATH);
            //createSingleArchiveFile("single.zip");
            createMultivolumeZipFile("split.zip", MAX_VOLUME_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates single-file archive in ZIP format.
     * @param name the name of the archive file.
     * @throws IOException I/O error occurred while creating the archive.
     */
    void createSingleZipFile(String name) throws IOException {
        try (var os = outputStorage.openOutputStream(name)) {
            createZip(os);
        }
        LOG.info("Created \"{}\" successfully", name);
    }

    /**
     * Creates multi-volume archive in ZIP format.
     * @param name the base name of the archive file.
     * @param maxVolumeSize the maximum volume size in bytes.
     * @throws IOException I/O error occurred while creating the archive.
     */
    void createMultivolumeZipFile(String name, long maxVolumeSize) throws IOException {
        try (var os = new SplitOutputStream(name, maxVolumeSize, outputStorage)) {
            createZip(os);
        }
        LOG.info("Created \"{}\" successfully", name);
    }

    /**
     * Creates an archive in ZIP format.
     * @param os the stream to write the archive.
     * @throws IOException I/O error occurred while creating the archive.
     */
    void createZip(OutputStream os) throws IOException {
        try (var zos = new ZipOutputStream(os, StandardCharsets.UTF_8)) {
            for (int i = 1; i <= ENTRY_COUNT; i++) {
                String name = String.format("%d.bin", i);
                LOG.info("Adding an entry \"{}\" from input stream", name);
                try (var is = inputStorage.openInputStream(name)) {
                    var entry = new ZipEntry(name);
                    zos.putNextEntry(entry);
                    is.transferTo(zos);
                }
            }
        }
    }

    /**
     * Generates entry files for testing purpose.
     * @throws IOException I/O error occurred while creating entry files.
     */
    void generateEntryFilesIfNotExists() throws IOException {
        if (Files.exists(INPUT_PATH)) {
            LOG.info("Input directory already exists in {}", INPUT_PATH);
            return;
        }
        Files.createDirectories(INPUT_PATH);
        new RandomFilesGenerator(this.inputStorage).generateFiles(
                ENTRY_COUNT, MIN_ENTRY_FILE_SZIE, MAX_ENTRY_FILE_SZIE);
    }
}

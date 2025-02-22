package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * An example application for creating multi-volume archive.
 */
@SpringBootApplication
public class ArchiverApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiverApplication.class);

    private final Storage inputStorage;
    private final Storage outputStorage;

    /**
     * Constructs this application.
     * @param inputStorage the storage where the entries reside.
     * @param outputStorage the storage where the archive will be created.
     */
    public ArchiverApplication(
            @Qualifier("inputStorage") Storage inputStorage,
            @Qualifier("outputStorage") Storage outputStorage
            ) {
        this.inputStorage = inputStorage;
        this.outputStorage = outputStorage;
    }

    /**
     * Runs this application.
     * @param args the arguments passed to this application.
     */
    @Override
    public void run(String... args) {

        final String[] inputDirs = {"input"};
        final String[] outputDirs = {"output"};
        final int entryCount = 5;
        final long maxVolumeSize = 2L * 1024 * 1024 * 1024;
        final String archiveName =  "split.zip";

        try {

            if (inputStorage instanceof LocalStorage local) {
                generateEntryFiles(inputDirs, entryCount, local);
            }

            LOG.info("Creating archive \"{}\"...", archiveName);

            try (var a = createArchiver(outputDirs, archiveName, maxVolumeSize)) {
                for (int i = 1; i <= entryCount; i++) {
                    var entryName = String.format("%d.bin", i);
                    try (var is = inputStorage.openInputStream(inputDirs, entryName)) {
                        a.addEntry(entryName, is);
                    }
                }
            }

            LOG.info("Created archive \"{}\" successfully", archiveName);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private Archiver createArchiver(String[] outputDirs, String archiveName, long maxVolumeSize) {
        return new MultivolumeZipArchiver(outputDirs, archiveName, maxVolumeSize, outputStorage);
    }

    /**
     * Generates sample entry files in local storage for testing purpose.
     */
    private void generateEntryFiles(
            String[]inputDirs, int entryCount, LocalStorage local) throws IOException {

        var inputPath = local.getRootDir().resolve(Path.of("", inputDirs));
        if (Files.exists(inputPath)) {
            LOG.info("Entry directory \"{}\" already exists", String.join("/", inputDirs));
            LOG.info("SKipped to generate entry files");
            return;
        }

        var g = new RandomFilesGenerator(inputStorage);
        g.generateFiles(inputDirs, entryCount);
    }

    /**
     * The entry point of this application.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(ArchiverApplication.class, args);
    }
}

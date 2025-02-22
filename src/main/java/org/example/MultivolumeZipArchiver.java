package org.example;

import java.io.OutputStream;

/**
 * An archiver to generate multi-volume archive of zip-format.
 */
public class MultivolumeZipArchiver extends ZipArchiver {

    public MultivolumeZipArchiver(String[] outputDirs, String basename, long maxVolumeSize, Storage outputStorage) {
        super(createOutputStream(outputDirs, basename, maxVolumeSize, outputStorage));
    }

    private static OutputStream createOutputStream(
            String[] outputDirs, String basename, long maxVolumeSize, Storage outputStorage) {
        return new SplitOutputStream(outputDirs, basename, maxVolumeSize, outputStorage);
    }
}

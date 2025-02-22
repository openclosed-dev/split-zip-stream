package org.example;

import java.nio.file.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for storages.
 */
@Configuration
public class StorageConfiguration {

    private static final Path LOCAL_ROOT_DIR = Path.of("target");

    /**
     * Creates an instance of storage for input.
     * @return an instance of storage.
     */
    @Bean
    public Storage inputStorage() {
        return new LocalStorage(LOCAL_ROOT_DIR);
    }

    /**
     * Creates an instance of storage for output.
     * @return an instance of storage.
     */
    @Bean
    public Storage outputStorage() {
        return new LocalStorage(LOCAL_ROOT_DIR);
    }
}

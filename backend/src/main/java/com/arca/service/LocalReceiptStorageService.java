package com.arca.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocalReceiptStorageService implements ReceiptStorageService {

    private final Path baseDir;

    public LocalReceiptStorageService(@Value("${app.storage.local-dir}") String dir) {
        this.baseDir = Path.of(dir);
    }

    @Override
    public StoredReceipt store(byte[] content, String originalFilename, String contentType) {
        try {
            Files.createDirectories(baseDir);
            String ext = "";
            if (originalFilename != null) {
                int dot = originalFilename.lastIndexOf('.');
                if (dot >= 0) {
                    ext = originalFilename.substring(dot);
                }
            }
            String key = UUID.randomUUID() + ext;
            Files.write(baseDir.resolve(key), content);
            return new StoredReceipt(key, content.length);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store receipt", e);
        }
    }
}

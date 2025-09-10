package com.example.CdnOriginServer.utils;

import com.example.CdnOriginServer.model.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Βοηθητική κλάση για λειτουργίες που χρησιμοποιούνται σε διάφορα σημεία του κώδικα.

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static File getExistingFileFromFileSystem(FileMetadata fileMetadata) throws FileNotFoundException {
        File file = new File(fileMetadata.getFilepath());
        if (!file.exists()) {
            logger.error("File does not exist at path: " + file.getPath());
            throw new FileNotFoundException("File does not exist at path: " + file.getPath());
        }

        return file;
    }

    public static Path createBackupFile(Path filePath, String filename) throws IOException {
        Path backupPath = Files.createTempFile("backup_", "_" + filename);
        Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);

        return backupPath;
    }
}
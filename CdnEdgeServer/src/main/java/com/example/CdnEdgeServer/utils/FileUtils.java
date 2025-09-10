package com.example.CdnEdgeServer.utils;

import com.example.CdnEdgeServer.model.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

// Βοηθητική κλάση για λειτουργίες που χρησιμοποιούνται σε διάφορα σημεία του κώδικα.

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static File getExistingFileFromFileSystem(FileMetadata fileMetadata) throws FileNotFoundException {
        File file = new File(fileMetadata.getFilepath());
        if (!file.exists()) {
            logger.error("File does not exist at path: " + file.getPath());
            throw new FileNotFoundException("File does not exists at path: " + file.getPath());
        }

        return file;
    }
}
package com.example.CdnEdgeServer.utils;

import com.example.CdnEdgeServer.model.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

/*
Η κλάση FileUtils αποτελείται από γενικές βοηθητικές συναρτήσεις με σκοπό την επαναχρησιμοποίησή τους σε όλο το project.
 */

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /*
    Η συνάρτηση getExistingFileFromFileSystem είναι υπεύθυνη για την ανάκτηση ενός αρχείου από τον
    τοπικό φάκελο του διακομιστή κρυφής μνήμης.
     */
    public static File getExistingFileFromFileSystem(FileMetadata fileMetadata) throws FileNotFoundException {
        File file = new File(fileMetadata.getFilepath());
        if (!file.exists()) {
            logger.error("File does not exist at path: " + file.getPath());
            throw new FileNotFoundException("File does not exists at path: " + file.getPath());
        }

        return file;
    }
}
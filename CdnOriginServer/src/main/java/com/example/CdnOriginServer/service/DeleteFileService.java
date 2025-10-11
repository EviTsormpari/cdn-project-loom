package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import com.example.CdnOriginServer.model.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public class DeleteFileService {
    private final DeleteFileOnDiskAndDBService deleteFileOnDiskAndDBService;
    private final UpdateCachesService updateCachesService;
    private final Helper helper;
    private static final Logger logger = LoggerFactory.getLogger(DeleteFileService.class);

    @Autowired
    public DeleteFileService(DeleteFileOnDiskAndDBService deleteFileOnDiskAndDBService, UpdateCachesService updateCachesService, Helper helper) {
        this.deleteFileOnDiskAndDBService = deleteFileOnDiskAndDBService;
        this.updateCachesService = updateCachesService;
        this.helper = helper;
    }

    // Διαγράφει ένα αρχείο στο σύστημα, στη βάση και στους διακομιστές κρυφής μνήμης
    public String deleteFileByFilename(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = helper.getFileMetadataFromDB(filename);

        try {
            deleteFileOnDiskAndDBService.deleteOnDiskAndDB(filename, fileMetadata);
        } catch (Exception e) {
            logger.warn("Failed to delete file on origin (DB/disk): {}" , e.getMessage());
            return "Failed to delete file on origin (DB/disk): " + e.getMessage();
        }

        return updateCachesService.updateCaches(filename) + "and successfully deleted at origin for filename: " + filename;
    }
}
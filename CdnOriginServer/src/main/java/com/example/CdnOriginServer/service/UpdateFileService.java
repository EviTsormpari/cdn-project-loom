package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import com.example.CdnOriginServer.enums.Existence;
import com.example.CdnOriginServer.model.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UpdateFileService {
    private final UpdateFileOnDiskAndDBService updateFileOnDiskAndDBService;
    private final UpdateCachesService updateCachesService;
    private final Helper helper;
    private static final Logger logger = LoggerFactory.getLogger(UpdateFileService.class);

    @Autowired
    public UpdateFileService(UpdateFileOnDiskAndDBService updateFileOnDiskAndDBService, UpdateCachesService updateCachesService, Helper helper) {
        this.updateFileOnDiskAndDBService = updateFileOnDiskAndDBService;
        this.updateCachesService = updateCachesService;
        this.helper = helper;
    }

    // Ενημερώνει ένα υπάρχον αρχείο στο σύστημα, στη βάση και στους διακομιστές κρυφής μνήμης.
    public String updateFile(MultipartFile file) throws IOException {
        FileMetadata newFileMetadata = helper.getFileMetadataFromFile(file);
        FileMetadata fileMetadata = helper.getFileMetadataFromDB(newFileMetadata.getId());
        String filename = fileMetadata.getId();
        helper.validateFileExistenceInDB(filename, Existence.MUST_EXIST);

        try {
            updateFileOnDiskAndDBService.updateDiskAndDB(fileMetadata.getFilepath(), filename, newFileMetadata, file);
        } catch (Exception e) {
            logger.error("Failed to update file {} in DB or disk", filename, e);
            return "Failed to update file on origin (DB/disk): " + e.getMessage();
        }

        return updateCachesService.updateCaches(filename) + "and successfully updated at origin for filename: " + filename;
    }
}
package com.example.CdnEdgeServer.service;

import com.example.CdnEdgeServer.model.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class RestoreFileMetadataInCacheService {
    private static final Logger logger = LoggerFactory.getLogger(RestoreFileMetadataInCacheService.class);

    // Επαναφορά μεταδεδομένων αρχείου στη Redis.
    @CachePut(value = "fileMetadataCache", key = "#fileMetadata.filename")
    public FileMetadata restoreFileMetadata(FileMetadata fileMetadata) {
        logger.info("File metadata restore triggered for {}", fileMetadata.getId());
        return fileMetadata;
    }
}
package com.example.CdnEdgeServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class DeleteFileFromCacheService {
    private static final Logger logger = LoggerFactory.getLogger(DeleteFileFromCacheService.class);

    // Διαγραφή αρχείου από την κρυφή μνήμη (εάν υπάρχει).
    @CacheEvict(value = "fileMetadataCache", key = "#filename")
    public String deleteFileByFilename(String filename) {
        logger.info("Deletion triggered at edge");
        return "File deletion triggered at edge ";
    }
}
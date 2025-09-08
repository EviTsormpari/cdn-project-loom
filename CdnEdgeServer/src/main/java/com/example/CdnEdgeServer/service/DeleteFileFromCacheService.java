package com.example.CdnEdgeServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/*
Υπηρεσία για την πραγματοποίηση της διαγραφής των δεδομένων με κλειδί #filename από την κρυφή μνήμη.
 */

@Service
public class DeleteFileFromCacheService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteFileFromCacheService.class);

    /*
    Το @CacheEvict πραγματοποιεί την αναζήτηση του #filename στην κρυφή μνήμη.
    Αν το #filename υπάρχει διαγράφεται και δημιουργείται event "evicted".
    Αν το #filename δεν υπάρχει δεν ακολουθεί καμία ενέργεια αποφεύγοντας έτσι την εμφάνιση σφαλμάτων.
     */
    @CacheEvict(value = "fileMetadataCache", key = "#filename")
    public String deleteFileByFilename(String filename) {
        logger.info("Deletion triggered at edge");
        return "File deletion triggered at edge ";
    }
}
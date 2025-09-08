package com.example.CdnEdgeServer.service;

import com.example.CdnEdgeServer.dto.FileResourceDTO;
import com.example.CdnEdgeServer.model.FileMetadata;
import com.example.CdnEdgeServer.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.*;

/*
Το EdgeService αποτελεί το βασικό service του διακομιστή.
Επικοινωνεί με το EdgeController και τις υπόλοιπες υπηρεσίες για την εκτέλεση των λειτουργιών.
 */

@Service
public class EdgeService {
    private final FetchFromOriginService fetchFromOriginService;

    private final DeleteFileFromCacheService deleteFileFromCacheService;

    @Autowired
    public EdgeService(FetchFromOriginService fetchFromOriginService, DeleteFileFromCacheService deleteFileFromCacheService) {
        this.fetchFromOriginService = fetchFromOriginService;
        this.deleteFileFromCacheService = deleteFileFromCacheService;
    }

    /*
    Η συνάρτηση getFileByName επιστρέφει στον αιτούντα το αρχείο με το όνομα #filename.

    1. Επικοινωνία με FetchFromOriginService για την απόκτηση των μεταδεδομένων του αρχείου #filename.
    2. Απόκτηση του αρχείου #filename από τον τοπικό φάκελο του project.
    3. Δημιουργία InputStream και Data Transfer Object για την αποστολή του αρχείου και των μεταδεδομένων του
       στο layer του EdgeController.
     */
    public FileResourceDTO getFileByName(String filename) throws IOException {
        FileMetadata fileMetadata = fetchFromOriginService.fetchFileMetadataAndDownloadFile(filename);

        File file = FileUtils.getExistingFileFromFileSystem(fileMetadata);

        InputStreamResource resource = new InputStreamResource( new FileInputStream(file) );
        return new FileResourceDTO(resource, fileMetadata);
    }

    /*
    Η συνάρτηση deleteFileByFilename επικοινωνεί με το DeleteFileFromCacheService
    για την εκτέλεση της διαγραφής του αρχείου #filename από την κρυφή μνήμη και τον τοπικό φάκελο.
     */
    public String deleteFileByFilename(String filename) {
        return deleteFileFromCacheService.deleteFileByFilename(filename);
    }
}
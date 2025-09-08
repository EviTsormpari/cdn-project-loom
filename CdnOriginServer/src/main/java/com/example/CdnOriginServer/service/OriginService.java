package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import com.example.CdnOriginServer.dto.FileResourceDTO;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/*
Το OriginService αποτελεί το βασικό service του κεντρικού διακομιστή.
Επικοινωνεί με το OriginController και τις υπόλοιπες υπηρεσίες για την εκτέλεση των λειτουργιών.
 */

@Service
public class OriginService {
    private final UpdateFileService updateFileService;
    private final CreateFileService createFileService;
    private final DeleteFileService deleteFileService;
    private final Helper helper;

    @Autowired
    public OriginService(UpdateFileService updateFileService, DeleteFileService deleteFileService, CreateFileService createFileService, Helper helper) {
        this.updateFileService = updateFileService;
        this.deleteFileService = deleteFileService;
        this.createFileService = createFileService;
        this.helper = helper;
    }
    /*
    Η συνάρτηση getFileByName επιστρέφει στον διακομιστή κρυφής μνήμης το αρχείο με το όνομα #filename.

    1. Έλεγχος ύπαρξης των μεταδεδομένων του αρχείου και απόκτησή τους αν υπάρχουν.
    2. Απόκτηση του αρχείου #filename από τον τοπικό φάκελο του project.
    3. Δημιουργία InputStream και Data Transfer Object για την αποστολή του αρχείου και των μεταδεδομένων του
       στο layer του OriginController.
     */
    public FileResourceDTO getFileByFilename(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = helper.getFileMetadataFromDB(filename);

        File file = FileUtils.getExistingFileFromFileSystem(fileMetadata);

        InputStreamResource resource = new InputStreamResource( new FileInputStream(file) );
        return new FileResourceDTO(resource, fileMetadata);
    }

    /*
    Η συνάρτηση updateFile επικοινωνεί με το UpdateFileService
    για την τροποποίηση του αρχείου #filename.
     */
    public String updateFile(MultipartFile file) throws IOException {
        return updateFileService.updateFile(file);
    }

    /*
    Η συνάρτηση createFile επικοινωνεί με το CreateFileService
    για τη δημιουργία του αρχείου #filename.
     */
    public String createFile(MultipartFile file) throws IOException {
        return createFileService.createFile(file);
    }

    /*
    Η συνάρτηση deleteFileByFilename επικοινωνεί με το DeleteFileService
    για τη διαγραφή του αρχείου #filename.
     */
    public String deleteFileByFilename(String filename) throws IOException {
        return deleteFileService.deleteFileByFilename(filename);
    }
}
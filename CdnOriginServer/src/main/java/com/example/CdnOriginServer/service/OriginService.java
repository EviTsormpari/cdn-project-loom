package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.dto.FileResourceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/*
Το OriginService αποτελεί το βασικό service του κεντρικού διακομιστή.
Επικοινωνεί με το OriginController και τις υπόλοιπες υπηρεσίες για την εκτέλεση των λειτουργιών.
 */

@Service
public class OriginService {
    private final GetFileService getFileService;
    private final UpdateFileService updateFileService;
    private final CreateFileService createFileService;
    private final DeleteFileService deleteFileService;


    @Autowired
    public OriginService(GetFileService getFileService, UpdateFileService updateFileService, DeleteFileService deleteFileService, CreateFileService createFileService) {
        this.getFileService = getFileService;
        this.updateFileService = updateFileService;
        this.deleteFileService = deleteFileService;
        this.createFileService = createFileService;
    }

    public FileResourceDTO getFileByFilename(String filename) throws FileNotFoundException {
        return getFileService.getFileByFilename(filename);
    }

    public String updateFile(MultipartFile file) throws IOException {
        return updateFileService.updateFile(file);
    }

    public String createFile(MultipartFile file) throws IOException {
        return createFileService.createFile(file);
    }

    public String deleteFileByFilename(String filename) throws IOException {
        return deleteFileService.deleteFileByFilename(filename);
    }
}
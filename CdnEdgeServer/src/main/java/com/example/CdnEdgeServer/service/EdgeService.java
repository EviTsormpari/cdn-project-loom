package com.example.CdnEdgeServer.service;

import com.example.CdnEdgeServer.dto.FileResourceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

/*
Το EdgeService αποτελεί το βασικό service του διακομιστή.
Επικοινωνεί με το EdgeController και τις υπόλοιπες υπηρεσίες για την εκτέλεση των λειτουργιών.
 */

@Service
public class EdgeService {
    private final GetFileService getFileService;

    private final DeleteFileFromCacheService deleteFileFromCacheService;

    @Autowired
    public EdgeService(GetFileService getFileService, DeleteFileFromCacheService deleteFileFromCacheService) {
        this.getFileService = getFileService;
        this.deleteFileFromCacheService = deleteFileFromCacheService;
    }

    public FileResourceDTO getFileByName(String filename) throws IOException {
        return getFileService.getFileByFilename(filename);
    }

    public String deleteFileByFilename(String filename) {
        return deleteFileFromCacheService.deleteFileByFilename(filename);
    }
}
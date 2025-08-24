package com.example.CdnEdgeServer.service;

import com.example.CdnEdgeServer.dto.FileResourceDTO;
import com.example.CdnEdgeServer.model.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class EdgeService {
    private final FetchFromOriginService fetchFromOriginService;

    private final DeleteFileFromCacheService deleteFileFromCacheService;

    @Autowired
    public EdgeService(FetchFromOriginService fetchFromOriginService, DeleteFileFromCacheService deleteFileFromCacheService) {
        this.fetchFromOriginService = fetchFromOriginService;
        this.deleteFileFromCacheService = deleteFileFromCacheService;
    }

    public FileResourceDTO getFileByName(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = fetchFromOriginService.fetchFileMetadataAndDownloadFile(filename);

        if (fileMetadata == null) throw new RuntimeException("File metadata not found for filename: " + filename);

        File file = new File(fileMetadata.getFilepath());
        if (!file.exists()) throw new FileNotFoundException("File does not exists at path: " + file.getPath());

        InputStreamResource resource = new InputStreamResource( new FileInputStream(file) );
        return new FileResourceDTO(resource, fileMetadata);
    }

    public void deleteFileByFilename(String filename) {
        deleteFileFromCacheService.deleteFileByFilename(filename);
    }
}
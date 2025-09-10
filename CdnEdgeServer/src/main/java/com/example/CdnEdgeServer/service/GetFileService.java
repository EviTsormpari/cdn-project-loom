package com.example.CdnEdgeServer.service;

import com.example.CdnEdgeServer.dto.FileResourceDTO;
import com.example.CdnEdgeServer.model.FileMetadata;
import com.example.CdnEdgeServer.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class GetFileService {
    private final FetchFromOriginService fetchFromOriginService;

    @Autowired
    public GetFileService(FetchFromOriginService fetchFromOriginService) {
        this.fetchFromOriginService = fetchFromOriginService;
    }

    // Επιστρέφει το αρχείο και τα metadata του στη μορφή FileResourceDTO
    public FileResourceDTO getFileByFilename(String filename) throws IOException {
        FileMetadata fileMetadata = fetchFromOriginService.fetchFileMetadataAndDownloadFile(filename);
        File file = FileUtils.getExistingFileFromFileSystem(fileMetadata);
        InputStreamResource resource = new InputStreamResource( new FileInputStream(file) );

        return new FileResourceDTO(resource, fileMetadata);
    }
}

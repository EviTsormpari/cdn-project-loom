package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.dto.FileResourceDTO;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Service
public class OriginService {

    private final OriginRepository originRepository;

    @Autowired
    public OriginService(OriginRepository originRepository) { this.originRepository = originRepository; }

    public FileResourceDTO getFileByName (String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = originRepository.findByFilename(filename);

        if (fileMetadata == null) {
            throw new FileMetadataNotFoundException("File metadata not found for filename: " + filename);
        }

        File file = new File(fileMetadata.getFilepath());
        if (!file.exists()) throw new FileNotFoundException("File does not exists at path: " + file.getPath());

        InputStreamResource resource = new InputStreamResource( new FileInputStream(file) );
        return new FileResourceDTO(resource, fileMetadata);
    }

    public static class FileMetadataNotFoundException extends RuntimeException {
        public FileMetadataNotFoundException(String message) {
            super(message);
        }
    }
}
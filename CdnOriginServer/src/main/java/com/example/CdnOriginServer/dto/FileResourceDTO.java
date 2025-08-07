package com.example.CdnOriginServer.dto;

import com.example.CdnOriginServer.model.FileMetadata;
import org.springframework.core.io.InputStreamResource;

public class FileResourceDTO {
    private InputStreamResource resource;
    private FileMetadata metadata;

    public FileResourceDTO(InputStreamResource resource, FileMetadata metadata) {
        this.resource = resource;
        this.metadata = metadata;
    }

    public InputStreamResource getResource() { return resource; }

    public FileMetadata getMetadata() { return metadata; }
}
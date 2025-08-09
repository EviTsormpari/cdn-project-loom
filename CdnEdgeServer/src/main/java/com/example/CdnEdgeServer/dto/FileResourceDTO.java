package com.example.CdnEdgeServer.dto;

import com.example.CdnEdgeServer.model.FileMetadata;
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

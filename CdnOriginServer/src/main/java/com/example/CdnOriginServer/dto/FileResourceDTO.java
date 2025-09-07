package com.example.CdnOriginServer.dto;

import com.example.CdnOriginServer.model.FileMetadata;
import org.springframework.core.io.InputStreamResource;

public record FileResourceDTO(InputStreamResource resource, FileMetadata metadata) { }
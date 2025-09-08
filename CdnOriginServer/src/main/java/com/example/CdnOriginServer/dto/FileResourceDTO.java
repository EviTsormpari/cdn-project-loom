package com.example.CdnOriginServer.dto;

import com.example.CdnOriginServer.model.FileMetadata;
import org.springframework.core.io.InputStreamResource;

/*
Data Transfer Object για τη διευκόλυνση αποστολής δεδομένων από ένα layer σε ένα άλλο.
 */

public record FileResourceDTO(InputStreamResource resource, FileMetadata metadata) { }
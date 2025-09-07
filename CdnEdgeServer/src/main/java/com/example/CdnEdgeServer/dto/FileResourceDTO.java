package com.example.CdnEdgeServer.dto;

import com.example.CdnEdgeServer.model.FileMetadata;
import org.springframework.core.io.InputStreamResource;

import javax.xml.crypto.Data;

/*
Data Transfer Object για τη διευκόλυνση αποστολής δεδομένων από ένα layer σε ένα άλλο.
 */

public record FileResourceDTO(InputStreamResource resource, FileMetadata metadata) { }
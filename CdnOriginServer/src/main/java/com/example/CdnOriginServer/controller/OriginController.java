package com.example.CdnOriginServer.controller;

import com.example.CdnOriginServer.dto.FileResourceDTO;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.service.OriginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("api/v1/files")
public class OriginController {

    private final OriginService originService;

    @Autowired
    public OriginController(OriginService originService) { this.originService = originService; }

    @GetMapping("/{filename}")
    public ResponseEntity<InputStreamResource> getFileByFilename (@PathVariable String filename) throws FileNotFoundException {
        FileResourceDTO fileResourceDTO = originService.getFileByName(filename);
        FileMetadata metadata = fileResourceDTO.getMetadata();
        InputStreamResource resource = fileResourceDTO.getResource();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFilename() + "\"")
                .contentLength(metadata.getFilesize())
                .contentType(MediaType.parseMediaType(metadata.getFiletype()))
                .body(resource);
    }
}
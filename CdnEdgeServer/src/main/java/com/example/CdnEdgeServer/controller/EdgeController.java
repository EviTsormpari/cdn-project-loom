package com.example.CdnEdgeServer.controller;

import com.example.CdnEdgeServer.dto.FileResourceDTO;
import com.example.CdnEdgeServer.model.FileMetadata;
import com.example.CdnEdgeServer.service.EdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("api/v1/files")
public class EdgeController {

    private final EdgeService edgeService;

    @Autowired
    public EdgeController(EdgeService edgeService) { this.edgeService = edgeService; }

    @GetMapping("/{filename}")
    public ResponseEntity<InputStreamResource> getFileByFilename (@PathVariable String filename) throws FileNotFoundException {
        FileResourceDTO fileResourceDTO = edgeService.getFileByName(filename);
        FileMetadata metadata = fileResourceDTO.getMetadata();
        InputStreamResource resource = fileResourceDTO.getResource();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFilename() + "\"")
                .contentLength(metadata.getFilesize())
                .contentType(MediaType.parseMediaType(metadata.getFiletype()))
                .body(resource);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<String> deleteFileByFilename (@PathVariable String filename) {
        edgeService.deleteFileByFilename(filename);

        return ResponseEntity.ok("Deletion triggered for file: " + filename);
    }
}
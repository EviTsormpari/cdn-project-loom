package com.example.CdnOriginServer.controller;

import com.example.CdnOriginServer.dto.FileResourceDTO;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.service.OriginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

/*
Το OriginController είναι υπεύθυνο για την έκθεση και διαχείριση των REST endpoints του συστήματος.

Μέσω αυτών επιτρέπεται η επικοινωνία εξωτερικών εφαρμογών με τον κεντρικό διακομιστή.
Τα endpoints που παρέχονται είναι για:
1. Την απόκτηση ενός αρχείου με βάση το όνομά του.
2. Την επεξεργασία ενός αρχείου με βάση το όνομά του.
3. Τη δημιουργία ενός αρχείου.
4. Τη διαγραφή ενός αρχείου με βάση το όνομά του.
 */

@RestController
@RequestMapping("api/v1/files")
public class OriginController {

    private final OriginService originService;
    @Autowired
    public OriginController(OriginService originService) { this.originService = originService; }

    @GetMapping("/{filename}")
    public ResponseEntity<InputStreamResource> getFileByFilename(@PathVariable String filename) throws FileNotFoundException {
        FileResourceDTO fileResourceDTO = originService.getFileByFilename(filename);
        FileMetadata metadata = fileResourceDTO.metadata();
        InputStreamResource resource = fileResourceDTO.resource();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getId() + "\"")
                .contentLength(metadata.getFilesize())
                .contentType(MediaType.parseMediaType(metadata.getFiletype()))
                .body(resource);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateFile(@RequestParam("file") MultipartFile file) throws IOException {
        String response = originService.updateFile(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String response = originService.createFile(file);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<String> deleteFileByFilename(@PathVariable String filename) throws IOException {
        String response = originService.deleteFileByFilename(filename);
        return ResponseEntity.ok(response);
    }
}
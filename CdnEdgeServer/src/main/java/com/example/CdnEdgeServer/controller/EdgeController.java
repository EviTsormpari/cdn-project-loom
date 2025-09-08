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

import java.io.IOException;

/*
Το EdgeController είναι υπεύθυνο για την έκθεση και διαχείριση των REST endpoints του συστήματος.

Μέσω αυτών επιτρέπεται η επικοινωνία εξωτερικών εφαρμογών με τον διακομιστή κρυφής μνήμης.
Κάθε endpoint αντιστοιχεί σε μια συγκεκριμένη λειτουργικότητα, καλεί το αντίστοιχο service και επιστρέφει
την κατάλληλη απόκριση (επιτυχία ή σφάλμα) προς τον αιτούντα.

Τα endpoints που παρέχονται είναι για:
1. Την απόκτηση ενός αρχείου με βάση το όνομά του.
2. Τη διαγραφή ενός αρχείου με βάση το όνομά του.
 */

@RestController
@RequestMapping("api/v1/files")
public class EdgeController {

    private final EdgeService edgeService;

    @Autowired
    public EdgeController(EdgeService edgeService) { this.edgeService = edgeService; }

    @GetMapping("/{filename}")
    public ResponseEntity<InputStreamResource> getFileByFilename (@PathVariable String filename) throws IOException {
        FileResourceDTO fileResourceDTO = edgeService.getFileByName(filename);
        FileMetadata metadata = fileResourceDTO.metadata();
        InputStreamResource resource = fileResourceDTO.resource();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getId() + "\"")
                .contentLength(metadata.getFilesize())
                .contentType(MediaType.parseMediaType(metadata.getFiletype()))
                .body(resource);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<String> deleteFileByFilename (@PathVariable String filename) {
        String response = edgeService.deleteFileByFilename(filename);
        return ResponseEntity.ok(response);
    }
}
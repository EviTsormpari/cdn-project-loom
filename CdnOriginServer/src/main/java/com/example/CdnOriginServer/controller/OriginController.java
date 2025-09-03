package com.example.CdnOriginServer.controller;

import com.example.CdnOriginServer.dto.FileResourceDTO;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.service.OriginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("api/v1/files")
public class OriginController {

    private final OriginService originService;

    @Value("${origin.local.filepath}")
    private String originFilepath;

    @Autowired
    public OriginController(OriginService originService) { this.originService = originService; }

    @GetMapping("/{filename}")
    public ResponseEntity<InputStreamResource> getFileByFilename(@PathVariable String filename) throws FileNotFoundException {
        FileResourceDTO fileResourceDTO = originService.getFileByFilename(filename);
        FileMetadata metadata = fileResourceDTO.getMetadata();
        InputStreamResource resource = fileResourceDTO.getResource();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFilename() + "\"")
                .contentLength(metadata.getFilesize())
                .contentType(MediaType.parseMediaType(metadata.getFiletype()))
                .body(resource);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String filetype = file.getContentType();
        long filesize = file.getSize();
        String filepath = originFilepath + filename;

        FileMetadata metadata = new FileMetadata();
        metadata.setFilename(filename);
        metadata.setFilepath(filepath);
        metadata.setFiletype(filetype);
        metadata.setFilesize(filesize);

        originService.createFile(metadata, file.getInputStream());

        return ResponseEntity.ok("Upload triggered for file: " + filename);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<String> deleteFileByFilename(@PathVariable String filename) throws FileNotFoundException {
        ResponseEntity<String> response = originService.deleteFileByFilename(filename);

        return ResponseEntity.ok(response.getBody());
    }
}
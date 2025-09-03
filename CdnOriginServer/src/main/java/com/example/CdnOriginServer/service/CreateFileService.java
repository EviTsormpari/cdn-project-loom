package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class CreateFileService {

    private final OriginRepository originRepository;

    @Value("${origin.local.filepath}")
    private String originFilepath;

    public CreateFileService(OriginRepository originRepository) { this.originRepository = originRepository; }

    @Transactional(rollbackFor = Exception.class)
    public void createFile(FileMetadata fileMetadata, InputStream inputStream) throws IOException {
        if (originRepository.existsByFilename(fileMetadata.getFilename())) {
            throw new RuntimeException("This file already exists");
        }

        Path pathForDownloadFile = Paths.get(originFilepath).resolve(fileMetadata.getFilename());

        try {
            Files.copy(inputStream, pathForDownloadFile, StandardCopyOption.REPLACE_EXISTING);
            originRepository.save(fileMetadata);
        } catch (Exception e) { //Rollback episis kai gia to arxeio sto disko. To transactional kanei mono gia db
            Files.deleteIfExists(pathForDownloadFile);
            throw e;
        }
    }
}
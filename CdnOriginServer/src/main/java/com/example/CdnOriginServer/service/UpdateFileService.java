package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UpdateFileService {

    private final OriginRepository originRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${origin.local.filepath}")
    private String originFilepath;
    @Value("${edge.server.url}")
    private String edgeUrl;

    public UpdateFileService(OriginRepository originRepository) { this.originRepository = originRepository; }

    @Transactional(rollbackFor = Exception.class)
    public String updateFile(FileMetadata fileMetadata, InputStream inputStream) throws IOException {
        FileMetadata metadata = originRepository.findByFilename(fileMetadata.getId());

        if (metadata == null) {
            throw new FileNotFoundException("File " + fileMetadata.getId() + " not found");
        }

        originRepository.save(fileMetadata);
        updateFileOnDisk(fileMetadata, inputStream);
        return updateCaches(fileMetadata.getId()).getBody() + "and update at origin for filename: " + fileMetadata.getId(); //Update tous edge pou exoun to arxeio
    }

    private void updateFileOnDisk(FileMetadata fileMetadata, InputStream inputStream) throws IOException {
        Path pathForDownloadFile = Paths.get(originFilepath).resolve(fileMetadata.getId());
        Files.copy(inputStream, pathForDownloadFile, StandardCopyOption.REPLACE_EXISTING);
    }

    private ResponseEntity<String> updateCaches(String filename) {
        //TODO find all caches

        return restTemplate.exchange(
                edgeUrl + filename,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );
    }
}

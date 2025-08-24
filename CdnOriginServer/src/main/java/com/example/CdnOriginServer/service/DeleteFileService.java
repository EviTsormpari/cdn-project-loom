package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;

@Service
public class DeleteFileService {

    private final OriginRepository originRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${edge.server.url}")
    private String edgeUrl;

    private static final Logger logger = LoggerFactory.getLogger(DeleteFileService.class);

    @Autowired
    public DeleteFileService(OriginRepository originRepository) { this.originRepository = originRepository; }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> deleteFileByFilename(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = originRepository.findByFilename(filename);

        if (fileMetadata == null) {
            throw new FileNotFoundException("File metadata not found for filename: " + filename);
        }

        //No need for if statement because if the file doesnt exists we have an exception
        File file = getExistingFile(fileMetadata, filename);

        //Deletes file from system
        if (file.delete()) {
            logger.info("Deleted local file with filename: {}", filename);
        }
        else {
            throw new RuntimeException("Failed to delete file: " + file.getPath());
        }

        originRepository.deleteByFilename(filename);
        return deleteFileFromCaches(filename);
    }

    private File getExistingFile(FileMetadata fileMetadata, String filename) throws FileNotFoundException {
        File file = new File(fileMetadata.getFilepath());
        if (!file.exists()) throw new FileNotFoundException("File does not exists at path: " + file.getPath());

        //If we reach here return the file
        return file;
    }

    private ResponseEntity<String> deleteFileFromCaches(String filename) {
        //TODO find all caches

        return restTemplate.exchange(
                edgeUrl + filename,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );
    }
}
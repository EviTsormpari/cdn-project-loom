package com.example.CdnOriginServer.component;

import com.example.CdnOriginServer.enums.Existence;
import com.example.CdnOriginServer.handler.GlobalException;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

// Βοηθητική κλάση για λειτουργίες που χρησιμοποιούνται σε διάφορα σημεία του κώδικα.

@Component
public class Helper {
    private final OriginRepository originRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(Helper.class);
    @Value("${origin.local.filepath}")
    private String originFilepath;
    @Value("${edge1.server.url}")
    private String edge1Url;
    @Value("${edge2.server.url}")
    private String edge2Url;

    @Autowired
    public Helper(OriginRepository originRepository) { this.originRepository = originRepository; }

    public FileMetadata getFileMetadataFromDB(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = originRepository.findByFilename(filename);

        if (fileMetadata == null) {
            logger.error("File metadata not found for filename: {}" , filename);
            throw new FileNotFoundException("File metadata not found for filename: " + filename);
        }

        return fileMetadata;
    }

    public FileMetadata getFileMetadataFromFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String filetype = file.getContentType();
        long filesize = file.getSize();
        String filepath = originFilepath + "/" + filename;

        FileMetadata metadata = new FileMetadata();
        metadata.setId(filename);
        metadata.setFilepath(filepath);
        metadata.setFiletype(filetype);
        metadata.setFilesize(filesize);

        return metadata;
    }

    public void validateFileExistenceInDB(String filename, Existence existence) {
        boolean exists = originRepository.existsByFilename(filename);

        switch(existence) {
            case MUST_EXIST -> {
                if(exists) logger.info("Success: file exists");
                else throw new GlobalException.FileConflictException("File " + filename + " does not exist");
            }
            case MUST_NOT_EXIST -> {
                if(!exists) logger.info("File " + filename + " already exists");
                else throw new GlobalException.FileConflictException("File " + filename + " already exists");
            }
        }
    }

    public void restoreFileOnDisk(Path backupPath, FileMetadata fileMetadata) throws IOException {
        Path pathForDownloadFile = Paths.get(originFilepath).resolve(fileMetadata.getId());
        Files.copy(backupPath, pathForDownloadFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public ResponseEntity<String> informCaches(String filename) {
        restTemplate.exchange(
                edge1Url + filename,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );

        return restTemplate.exchange(
                edge2Url + filename,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );
    }
}
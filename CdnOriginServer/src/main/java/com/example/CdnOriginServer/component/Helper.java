package com.example.CdnOriginServer.component;

import com.example.CdnOriginServer.enums.Existence;
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

@Component
public class Helper {

    private final OriginRepository originRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(Helper.class);
    @Value("${origin.local.filepath}")
    private String originFilepath;
    @Value("${edge.server.url}")
    private String edgeUrl;

    @Autowired
    public Helper(OriginRepository originRepository) { this.originRepository = originRepository; }

    public FileMetadata getFileMetadataFromDB(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = originRepository.findByFilename(filename);

        if (fileMetadata == null) {
            logger.warn("File metadata not found for filename: {}" , filename);
            throw new FileNotFoundException("File metadata not found for filename: " + filename);
        }

        return fileMetadata;
    }

    public FileMetadata getFileMetadataFromFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String filetype = file.getContentType();
        long filesize = file.getSize();
        String filepath = originFilepath + filename;

        FileMetadata metadata = new FileMetadata();
        metadata.setId(filename);
        metadata.setFilepath(filepath);
        metadata.setFiletype(filetype);
        metadata.setFilesize(filesize);

        return metadata;
    }

    public void validateFileExistence(String filename, Existence existence) {
        switch (existence) {
            case MUST_EXIST -> {
                logger.info("Success: file exists");
            }
            case MUST_NOT_EXIST -> {
                logger.error("File " + filename + " already exists");
                throw new RuntimeException("File " + filename + " already exists");
            }
        }
    }

    public void restoreFile(Path backupPath, FileMetadata fileMetadata) throws IOException {
        Path pathForDownloadFile = Paths.get(originFilepath).resolve(fileMetadata.getId());
        Files.copy(backupPath, pathForDownloadFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public ResponseEntity<String> informCaches(String filename) {
        //TODO find all caches

        return restTemplate.exchange(
                edgeUrl + filename,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );
    }
}

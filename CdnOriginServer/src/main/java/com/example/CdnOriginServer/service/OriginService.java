package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.dto.FileResourceDTO;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class OriginService {

    private final OriginRepository originRepository;

    private final DeleteFileService deleteFileService;

    private final CreateFileService createFileService;

    @Autowired
    public OriginService(OriginRepository originRepository, DeleteFileService deleteFileService, CreateFileService createFileService) {
        this.originRepository = originRepository;
        this.deleteFileService = deleteFileService;
        this.createFileService = createFileService;
    }

    public FileResourceDTO getFileByFilename(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = originRepository.findByFilename(filename);

        if (fileMetadata == null) {
            throw new RuntimeException("File metadata not found for filename: " + filename);
        }

        //No need for if statement because if the file doesnt exists we have an exception
        File file = getExistingFile(fileMetadata, filename);

        InputStreamResource resource = new InputStreamResource( new FileInputStream(file) );
        return new FileResourceDTO(resource, fileMetadata);
    }

    public ResponseEntity<String> deleteFileByFilename(String filename) throws FileNotFoundException {
        return deleteFileService.deleteFileByFilename(filename);
    }

    public void createFile(FileMetadata fileMetadata, InputStream inputStream) throws IOException {
        createFileService.createFile(fileMetadata, inputStream);
    }

    private File getExistingFile(FileMetadata fileMetadata, String filename) throws FileNotFoundException {
        File file = new File(fileMetadata.getFilepath());
        if (!file.exists()) throw new FileNotFoundException("File does not exists at path: " + file.getPath());

        //If we reach here return the file
        return file;
    }
}
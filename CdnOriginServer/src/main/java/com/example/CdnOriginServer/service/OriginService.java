package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.dto.FileResourceDTO;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class OriginService {

    private final OriginRepository originRepository;
    private final UpdateFileService updateFileService;
    private final CreateFileService createFileService;
    private final DeleteFileService deleteFileService;


    @Value("${origin.local.filepath}")
    private String originFilepath;

    @Autowired
    public OriginService(OriginRepository originRepository, UpdateFileService updateFileService, DeleteFileService deleteFileService, CreateFileService createFileService) {
        this.originRepository = originRepository;
        this.updateFileService = updateFileService;
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

    public String updateFile(MultipartFile file) throws IOException {
        FileMetadata fileMetadata = getFileMetadataFromFile(file);
        return updateFileService.updateFile(fileMetadata, file.getInputStream());
    }

    public String createFile(MultipartFile file) throws IOException {
        FileMetadata fileMetadata = getFileMetadataFromFile(file);
        return createFileService.createFile(fileMetadata, file.getInputStream());
    }

    public String deleteFileByFilename(String filename) throws FileNotFoundException {
        return deleteFileService.deleteFileByFilename(filename);
    }

    private File getExistingFile(FileMetadata fileMetadata, String filename) throws FileNotFoundException {
        File file = new File(fileMetadata.getFilepath());
        if (!file.exists()) throw new FileNotFoundException("File does not exists at path: " + file.getPath());

        //If we reach here return the file
        return file;
    }

    private FileMetadata getFileMetadataFromFile(MultipartFile file) {
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
}
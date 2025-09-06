package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import com.example.CdnOriginServer.dto.FileResourceDTO;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class OriginService {
    private final UpdateFileService updateFileService;
    private final CreateFileService createFileService;
    private final DeleteFileService deleteFileService;
    private final Helper helper;

    @Autowired
    public OriginService(UpdateFileService updateFileService, DeleteFileService deleteFileService, CreateFileService createFileService, Helper helper) {
        this.updateFileService = updateFileService;
        this.deleteFileService = deleteFileService;
        this.createFileService = createFileService;
        this.helper = helper;
    }

    public FileResourceDTO getFileByFilename(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = helper.getFileMetadataFromDB(filename);

        //No need for if statement because if the file doesnt exists we have an exception
        File file = FileUtils.getExistingFileFromFileSystem(fileMetadata);

        InputStreamResource resource = new InputStreamResource( new FileInputStream(file) );
        return new FileResourceDTO(resource, fileMetadata);
    }

    public String updateFile(MultipartFile file) throws IOException {
        return updateFileService.updateFile(file);
    }

    public String createFile(MultipartFile file) throws IOException {
        return createFileService.createFile(file);
    }

    public String deleteFileByFilename(String filename) throws IOException {
        return deleteFileService.deleteFileByFilename(filename);
    }
}
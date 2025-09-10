package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import com.example.CdnOriginServer.dto.FileResourceDTO;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Service
public class GetFileService {
    private final Helper helper;

    @Autowired
    public GetFileService(Helper helper) { this.helper = helper; }

    // Επιστρέφει το αρχείο και τα μεταδεδομένα του στη μορφή FileResourceDTO.
    public FileResourceDTO getFileByFilename(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = helper.getFileMetadataFromDB(filename);

        File file = FileUtils.getExistingFileFromFileSystem(fileMetadata);

        InputStreamResource resource = new InputStreamResource( new FileInputStream(file) );
        return new FileResourceDTO(resource, fileMetadata);
    }
}

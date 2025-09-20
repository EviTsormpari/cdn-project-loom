package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import com.example.CdnOriginServer.enums.Existence;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import com.example.CdnOriginServer.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UpdateFileService {
    private final OriginRepository originRepository;
    private final Helper helper;
    private static final Logger logger = LoggerFactory.getLogger(UpdateFileService.class);
    @Value("${origin.local.filepath}")
    private String originFilepath;

    @Autowired
    public UpdateFileService(OriginRepository originRepository, Helper helper) { this.originRepository = originRepository;
        this.helper = helper;
    }

    /*
    Ενημερώνει ένα υπάρχον αρχείο στο σύστημα, στη βάση και στους διακομιστές κρυφής μνήμης.
    Σε περίπτωση αποτυχίας γίνεται rollback: επαναφορά από backup και αναίρεση αλλαγών στη βάση
    μέσω @Transactional.
    */
    @Transactional(rollbackFor = Exception.class)
    public String updateFile(MultipartFile file) throws IOException {
        FileMetadata newFileMetadata = helper.getFileMetadataFromFile(file);
        FileMetadata fileMetadata = helper.getFileMetadataFromDB(newFileMetadata.getId());
        String filename = fileMetadata.getId();
        helper.validateFileExistenceInDB(filename, Existence.MUST_EXIST);

        Path filePath = Paths.get(fileMetadata.getFilepath());
        Path backupPath = FileUtils.createBackupFile(filePath, filename);

        boolean updatedLocally = false;

        try{
            updateFileOnDisk(newFileMetadata, file.getInputStream());
            updatedLocally = true;
            originRepository.save(newFileMetadata);
            String response = helper.informCaches(filename).getBody() + "and successfully updated at origin for filename: " + filename;
            logger.info("The file {} updated successfully on the filesystem, database and edge caches", filename);

            return response;
        } catch (Exception e) {
            if (updatedLocally) {
                helper.restoreFileOnDisk(backupPath, newFileMetadata);
                logger.warn("Rollback: restored file {} due to failure", filename, e);
            }

            throw e;
        } finally {
            Files.deleteIfExists(backupPath);
        }
    }

    private void updateFileOnDisk(FileMetadata fileMetadata, InputStream inputStream) throws IOException {
        Path pathForDownloadFile = Paths.get(originFilepath).resolve(fileMetadata.getId());
        Files.copy(inputStream, pathForDownloadFile, StandardCopyOption.REPLACE_EXISTING);
    }
}
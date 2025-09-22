package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import com.example.CdnOriginServer.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DeleteFileService {
    private final OriginRepository originRepository;
    private final Helper helper;

    private static final Logger logger = LoggerFactory.getLogger(DeleteFileService.class);

    @Autowired
    public DeleteFileService(OriginRepository originRepository, Helper helper) {
        this.originRepository = originRepository;
        this.helper = helper;
    }

    /*
    Διαγράφει αρχείο από το σύστημα, τη βάση και τους διακομιστές κρυφής μνήμης.
    Σε περίπτωση αποτυχίας γίνεται rollback: επαναφορά αρχείου από backup και αναίρεση
    αλλαγών στη βάση μέσω @Transactional.
    */
    @Transactional(rollbackFor = Exception.class)
    public String deleteFileByFilename(String filename) throws IOException {
        FileMetadata fileMetadata = helper.getFileMetadataFromDB(filename);
        File file = FileUtils.getExistingFileFromFileSystem(fileMetadata);
        Path backupPath = FileUtils.createBackupFile(file.toPath(), filename);
        boolean deletedLocally = false;

        try{
            deleteLocalFile(file, filename);
            deletedLocally = true;
            originRepository.deleteByFilename(filename);
            String response = helper.informCaches(filename).getBody() + "and origin for file: " + filename;
            logger.info("The file {} deleted successfully from the filesystem, database and edge caches", filename);

            return response;
        } catch (Exception e) {
            if (deletedLocally) {
                helper.restoreFileOnDisk(backupPath, fileMetadata);
                logger.warn("Rollback: restored file {} due to failure", fileMetadata.getId(), e);
            }

            throw e;
        } finally {
            Files.deleteIfExists(backupPath);
        }
    }

    private void deleteLocalFile(File file, String filename) throws IOException {
        if (!file.delete()) {
            logger.error("Failed to delete file: " + filename + " at path: " + file.getPath());
            throw new IOException("Failed to delete file: " + filename);
        }
    }
}
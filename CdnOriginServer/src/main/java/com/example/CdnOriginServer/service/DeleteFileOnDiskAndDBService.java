package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import com.example.CdnOriginServer.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DeleteFileOnDiskAndDBService {
    private final OriginRepository originRepository;
    private final Helper helper;
    private static final Logger logger = LoggerFactory.getLogger(DeleteFileOnDiskAndDBService.class);

    public DeleteFileOnDiskAndDBService(OriginRepository originRepository, Helper helper) {
        this.originRepository = originRepository;
        this.helper = helper;
    }

    /*
    Διαγράφει αρχείο από το σύστημα και στη βάση δεδομένων.
    Σε περίπτωση αποτυχίας στον κετρικό διακομιστή γίνεται rollback:
    επαναφορά αρχείου από backup και αναίρεση αλλαγών στη βάση μέσω @Transactional.
    */
     @Transactional(rollbackFor = Exception.class)
    public void deleteOnDiskAndDB(String filename, FileMetadata fileMetadata) throws IOException {
        File file = FileUtils.getExistingFileFromFileSystem(fileMetadata);
        Path backupPath = FileUtils.createBackupFile(file.toPath(), filename);

        try {
            deleteLocalFile(file, filename);
            originRepository.deleteByFilename(filename);
            logger.info("The file {} deleted successfully from the filesystem and database and edge caches", filename);
        } catch (Exception e) {
            helper.restoreFileOnDisk(backupPath, fileMetadata);
            originRepository.save(fileMetadata);
            logger.warn("Rollback: restored file {} due to failure", fileMetadata.getId(), e);

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

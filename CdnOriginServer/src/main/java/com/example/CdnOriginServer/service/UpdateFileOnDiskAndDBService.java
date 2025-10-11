package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
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
public class UpdateFileOnDiskAndDBService {
    private final OriginRepository originRepository;
    private final Helper helper;
    private static final Logger logger = LoggerFactory.getLogger(UpdateFileOnDiskAndDBService.class);
    @Value("${origin.local.filepath}")
    private String originFilepath;

    @Autowired
    public UpdateFileOnDiskAndDBService(OriginRepository originRepository, Helper helper) {
        this.originRepository = originRepository;
        this.helper = helper;
    }

    /*
    Ενημερώνει τη βάση δεδομένων και τον τοπικό φάκελο.
    Σε περίπτωση αποτυχίας ενημέρωσης του κεντρικού διακομιστή γίνεται rollback:
    επαναφορά στον δίσκο από backup αρχείο και αναίρεση αλλαγών στη βάση μέσω @Transactional.
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDiskAndDB(String path, String filename, FileMetadata newFileMetadata, MultipartFile file) throws IOException {
        Path filePath = Paths.get(path);
        Path backupPath = FileUtils.createBackupFile(filePath, filename);

        try{
            updateFileOnDiskAndDB(newFileMetadata, file);
        } catch (Exception e) {
            helper.restoreFileOnDisk(backupPath, newFileMetadata);
            logger.warn("Rollback: restored file {} due to failure", filename, e);
            throw e;
        } finally {
            Files.deleteIfExists(backupPath);
        }
    }

    private void updateFileOnDiskAndDB(FileMetadata newFileMetadata, MultipartFile file) throws IOException {
        updateFileOnDisk(newFileMetadata, file.getInputStream());
        originRepository.save(newFileMetadata);
    }

    private void updateFileOnDisk(FileMetadata fileMetadata, InputStream inputStream) throws IOException {
        Path pathForDownloadFile = Paths.get(originFilepath).resolve(fileMetadata.getId());
        Files.copy(inputStream, pathForDownloadFile, StandardCopyOption.REPLACE_EXISTING);
    }
}

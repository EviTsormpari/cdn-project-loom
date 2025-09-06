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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DeleteFileService {

    private final OriginRepository originRepository;
    private final Helper helper;
    @Value("${origin.local.filepath}")
    private String originFilepath;

    private static final Logger logger = LoggerFactory.getLogger(DeleteFileService.class);

    @Autowired
    public DeleteFileService(OriginRepository originRepository, Helper helper) {
        this.originRepository = originRepository;
        this.helper = helper;
    }

    @Transactional(rollbackFor = Exception.class)
    public String deleteFileByFilename(String filename) throws IOException {
        FileMetadata fileMetadata = helper.getFileMetadataFromDB(filename);

        //No need for if statement because if the file doesnt exists we have an exception
        File file = FileUtils.getExistingFileFromFileSystem(fileMetadata);

        //Kanoume backup arxeio se periptwsi pou apotuxei i synartisi (gia to rollback)
        Path backupPath = FileUtils.createBackupFile(file.toPath(), filename);

        boolean deletedLocally = false;

        try{
            deleteLocalFile(file, filename); // diagrafi topika
            deletedLocally = true; //diagraftike topika to arxeio
            originRepository.deleteByFilename(filename); // diagrafi apo tin basi
            String response = helper.informCaches(filename).getBody() + "and origin for file: "; //diagrafi apo ta caches
            logger.info("The file {} deleted successfully from the filesystem, database and edge caches", filename);

            return response;
        } catch (Exception e) { //an kapoia diagrafei apotuxei epanaferw to arxeio sto filesystem
            if (deletedLocally) { //mono an diagraftike topika to kanw restore - mporei na ftasame se auto to exception logw allis diakopis
                helper.restoreFile(backupPath, fileMetadata);
                logger.warn("Rollback: restored file {} due to failure", fileMetadata.getId(), e);
            }

            throw e;
        } finally {
            //ama den sumbei tipota kai ola pane kala mporoume na diagrapsoume to backup arxeio
            Files.deleteIfExists(backupPath);
        }
    }

    private void deleteLocalFile(File file, String filename) {
        //Deletes file from filesystemsystem
        if (!file.delete()) {
            logger.warn("Failed to delete file: " + file.getPath());
            throw new RuntimeException("Failed to delete file: " + file.getPath());
        }
    }
}
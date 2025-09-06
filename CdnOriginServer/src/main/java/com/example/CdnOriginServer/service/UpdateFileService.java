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

    @Transactional(rollbackFor = Exception.class)
    public String updateFile(MultipartFile file) throws IOException {
        FileMetadata metadata = helper.getFileMetadataFromFile(file); //pairnoume ta metadata tou kainoutgiou arxeiou
        FileMetadata fileMetadata = helper.getFileMetadataFromDB(metadata.getId()); //8a finei elegxos an uparxei stin db kai an uparxoun ua ferei ta metadata toy paliou arxeio sti basi
        String filename = fileMetadata.getId();
        helper.validateFileExistence(filename, Existence.MUST_EXIST);

        Path filePath = Paths.get(fileMetadata.getFilepath());
        Path backupPath = FileUtils.createBackupFile(filePath, filename);

        boolean updatedLocally = false;

        try{
            updateFileOnDisk(metadata, file.getInputStream());
            updatedLocally = true;
            originRepository.save(metadata);
            String response = helper.informCaches(filename).getBody() + "and updated at origin for filename: " + filename; //Update tous edge pou exoun to arxeio
            logger.info("The file {} updated successfully on the filesystem, database and edge caches", filename);

            return response;
        } catch (Exception e) { //an kapoia diagrafei apotuxei epanaferw to arxeio sto filesystem
            if (updatedLocally) {
                helper.restoreFile(backupPath, metadata);
                logger.warn("Rollback: restored file {} due to failure", filename, e);
            }

            throw e;
        } finally {
            //ama den sumbei tipota kai ola pane kala mporoume na diagrapsoume to backup arxeio
            Files.deleteIfExists(backupPath);
        }
    }

    private void updateFileOnDisk(FileMetadata fileMetadata, InputStream inputStream) throws IOException {
        Path pathForDownloadFile = Paths.get(originFilepath).resolve(fileMetadata.getId());
        Files.copy(inputStream, pathForDownloadFile, StandardCopyOption.REPLACE_EXISTING);
    }
}
package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import com.example.CdnOriginServer.enums.Existence;
import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class CreateFileService {
    private final OriginRepository originRepository;
    private final Helper helper;
    private static final Logger logger = LoggerFactory.getLogger(CreateFileService.class);
    @Value("${origin.local.filepath}")
    private String originFilepath;

    @Autowired
    public CreateFileService(OriginRepository originRepository, Helper helper) {
        this.originRepository = originRepository;
        this.helper = helper;
    }

    /*
    Δημιουργεί νέο αρχείο στο σύστημα και ταυτόχρονα αποθηκεύει τα μεταδεδομένα του στη βάση.
    Αν αποτύχει η διαδικασία, γίνεται rollback στη βάση (μέσω @Transactional) και διαγραφή του
    αρχείου που πιθανόν γράφτηκε στον δίσκο.
    */
    @Transactional(rollbackFor = Exception.class)
    public String createFile(MultipartFile file) throws IOException {
        FileMetadata fileMetadata = helper.getFileMetadataFromFile(file);
        String filename = fileMetadata.getId();
        helper.validateFileExistenceInDB(filename, Existence.MUST_NOT_EXIST);
        Path pathForDownloadFile = Paths.get(originFilepath).resolve(filename);

        try {
            Files.copy(file.getInputStream(), pathForDownloadFile, StandardCopyOption.REPLACE_EXISTING);
            originRepository.save(fileMetadata);
            logger.info("Upload triggered for file: {}" , filename);

            return "Upload triggered for file: " + filename;
        } catch (Exception e) {
            Files.deleteIfExists(pathForDownloadFile);
            logger.warn("Rollback: deleted file {} due to failure", filename, e);

            throw e;
        }
    }
}
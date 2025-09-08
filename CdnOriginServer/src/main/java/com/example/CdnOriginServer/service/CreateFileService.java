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

/*
Υπηρεσία για τη δημιουργία αρχείου στον τοπικό φάκελο και την αποθήκευση των μεταδεδομένων του στη βάση δεδομένων.
 */

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
    Η createFile δημιουργεί και αποθηκεύει ένα νέο αρχείο τόσο στο σύστημα αρχείων όσο και στη βάση δεδομένων.

    Αναλυτικά:
    1. Λαμβάνει τα μεταδεδομένα του νέου αρχείου και με βάση το #filename επιβεβαιώνει ότι το αρχείο
    δεν υπάρχει ήδη στη βάση.
    2. Καθορίζει τη διαδρομή αποθήκευσης του αρχείου στον δίσκο και προσπαθεί να το αντιγράψει σε αυτόν
    και να αποθηκεύσει τα μεταδεδομένα στη βάση δεδομένων.

    Αν τα παραπάνω ολοκληρωθούν επιστρέφεται κατάλληλο μήνυμα επιτυχίας.
    Σε περίπτωση αποτυχίας το αρχείο που πιθανόν γράφτηκε στον δίσκο διαγράφεται και μέσω του @Transactional
    αναιρείται και οποιαδήποτε ενέργεια πραγματοποιήθηκε στη βάση.

    Το @Transactional εξασφαλίζει ότι αν προκύψει exception μέσα στη μέθοδο θα γίνει αυτόματη αναίρεση
    (rollback) των αλλαγών στη βάση δεδομένων. Ωστόσο, δεν καλύπτει αυτόματα και τα αρχεία από το σύστημα
    αρχείων και γι΄αυτό απαιτείται η χειροκίνητη αναίρεση των αλλαγών σε αυτό.
     */

    @Transactional(rollbackFor = Exception.class)
    public String createFile(MultipartFile file) throws IOException {
        FileMetadata fileMetadata = helper.getFileMetadataFromFile(file);
        String filename = fileMetadata.getId();
        helper.validateFileExistence(filename, Existence.MUST_NOT_EXIST);

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
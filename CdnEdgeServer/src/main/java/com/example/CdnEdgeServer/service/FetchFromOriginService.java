package com.example.CdnEdgeServer.service;

import com.example.CdnEdgeServer.model.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/*
Service για την απόκτηση των μεταδεδομένων ενός αρχείου από την κρυφή μνήμη ή από τον κεντρικό διακομιστή
στην περίπτωση που η κρυφή μνήμη δεν παρέχει τα μεταδεδομένα του αρχείου.
 */

@Service
public class FetchFromOriginService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(FetchFromOriginService.class);

    @Value("${origin.server.url}")
    private String originUrl;

    @Value("${edge.local.filepath}")
    private String edgeLocalFilepath;

    /*
    Το @Cacheable πραγματοποιεί την αναζήτηση του #filename στην κρυφή μνήμη.
    Θέτοντας sync = true μόνο ένα νήμα κάθε φορά μπορεί να εκτελέσει την αναζήτηση.
    -Το πρώτο νήμα που φτάνει και δε βρίσκει το αρχείο στη cache το ανακτά από τον κεντρικό διακομιστή.
    -Τα υπόλοιπα νήματα που περιμένουν βλέπουν το αρχείο ήδη στη cache και έτσι αποφεύγεται η διπλή ανάκτηση και εγγραφή.

    Αν το #filename υπάρχει επιστρέφει τα μεταδεδομένα απευθείας από την κρυφή μνήμη.
    Αν το #filename δεν υπάρχει στην κρυφή μνήμη εκτελείται η συνάρτηση fetchFileMetadataAndDownLoadFile η οποία:

    1. Καλεί το endpoint του κεντρικού διακομιστή για να αποκτήσει το αρχείο και τα μεταδεδομένα του.
    2. Δημιουργεί Java αντικείμενο με τα μεταδεδομένα του αρχείου για να είναι εφικτή η αποθήκευση του αντικειμένου
    στην κρυφή μνήμη.
    3. Αποθηκεύει το κανονικό αρχείο στον τοπικό φάκελο του project.
    4. Επιστρέφει τα μεταδεδομένα του αρχείου.
     */
    @Cacheable(value = "fileMetadataCache", key = "#filename", sync = true)
    public FileMetadata fetchFileMetadataAndDownloadFile (String filename) throws IOException {
        logger.info("Fetch from origin started for file: {}", filename);

        ResponseEntity<Resource> response = fetchFromOrigin(filename);

        HttpHeaders httpHeaders = response.getHeaders();

        FileMetadata fileMetadata = createFileMetadataObject(httpHeaders, filename);

        downloadFile(response, fileMetadata.getFilepath());

        logger.info("Fetch from origin completed for file: {}", filename);

        return fileMetadata;
    }

    private ResponseEntity<Resource> fetchFromOrigin(String filename) {
        return restTemplate.exchange(
                originUrl + filename,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Resource.class
        );
    }

    private FileMetadata createFileMetadataObject(HttpHeaders httpHeaders, String filename) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(filename);
        fileMetadata.setFiletype(httpHeaders.getContentType().toString());
        fileMetadata.setFilesize(httpHeaders.getContentLength());
        // Το αρχείο πλέον βρίσκεται στον διακομιστή κρυφής μνήμης και όχι στον κεντρικό διακομιστή.
        fileMetadata.setFilepath(Paths.get(edgeLocalFilepath).resolve(filename).toString());

        return fileMetadata;
    }

    private void downloadFile(ResponseEntity<Resource> response, String filepath) throws IOException {
        Resource resource = response.getBody();

        /*
        Αν το αρχείο είναι κενό δημιουργείται ένα κενό αρχείο καθώς το .getInputStream οδηγεί σε σφάλμα
        στην περίπτωση κενού αρχείου
         */
        if (resource == null) {
            logger.warn("File is empty or missing, creating empty file at {}", filepath);
            Files.createFile(Paths.get(filepath));
            return;
        }

        /*
        Αν το αρχείο δεν είναι κενό τότε δημιουργείται κανονικά.
         */
        try (InputStream is = response.getBody().getInputStream()) {
            Files.copy(is, Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.warn("Failed to download file to local cache at path: {}", filepath, e);
            throw new IOException("Failed to download file to local cache", e);
        }
    }
}
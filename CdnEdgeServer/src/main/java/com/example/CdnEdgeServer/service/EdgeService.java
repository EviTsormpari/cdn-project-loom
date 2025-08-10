package com.example.CdnEdgeServer.service;

import com.example.CdnEdgeServer.dto.FileResourceDTO;
import com.example.CdnEdgeServer.model.FileMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class EdgeService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${origin.server.url}")
    private String originUrl;

    @Value("${edge.local.filepath}")
    private String edgeLocalFilepath;

    public FileResourceDTO getFileByName(String filename) throws FileNotFoundException {
        FileMetadata fileMetadata = fetchFileMetadataAndDownloadFile(filename);

        if (fileMetadata == null) throw new RuntimeException("File metadata not found for filename: " + filename);

        File file = new File(fileMetadata.getFilepath());
        if (!file.exists()) throw new FileNotFoundException("File does not exists at path: " + file.getPath());

        InputStreamResource resource = new InputStreamResource( new FileInputStream(file) );
        return new FileResourceDTO(resource, fileMetadata);
    }

    //To @Cachable apo tin Redis elegxei apo mono tou an yparxei to filename sto redis
    //an denyparxei kalei tin synartisi fetchFileMetadataAndDownloadFile gia na ginei i klisi apo ton origin kai epistrefei to FileMetadata
    //an yparxei epistrefei apo tin redis to FileMetdata
    @Cacheable(value = "fileMetadataCache", key = "#filename", sync = true)
    public FileMetadata fetchFileMetadataAndDownloadFile (String filename) {
        //Klisi ston origin gia na paroume ta metadata kai to arxeio
        //Using RestTemplate
        ResponseEntity<InputStreamResource> response = restTemplate.exchange(
                originUrl + filename,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                InputStreamResource.class);

        //Take the headers of the response
        HttpHeaders httpHeaders = response.getHeaders();

        FileMetadata fileMetadata = createFileMetadataObject(httpHeaders, filename);

        downloadFile(response, fileMetadata);

        return fileMetadata;
    }

    private FileMetadata createFileMetadataObject(HttpHeaders httpHeaders, String filename) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setFilename(filename);
        fileMetadata.setFiletype(httpHeaders.getContentType().toString());
        fileMetadata.setFilesize(httpHeaders.getContentLength());
        //Edw bazoume to path pou 8a apo8ikeutei ston edge, dne 8eloyme tou origin den maw noiazei
        fileMetadata.setFilepath(edgeLocalFilepath);

        return fileMetadata;
    }

    private void downloadFile(ResponseEntity<InputStreamResource> response, FileMetadata fileMetadata) {
        String filename = fileMetadata.getFilename();
        String filepath = fileMetadata.getFilepath();

        //Edw kanoume download to arxeio sto pc tou edge server
        //Pairnoume to InputStream apo to InputStreamResource kai me files.copy to kanoyme copy opou 8eloume
        try (InputStream is = response.getBody().getInputStream()) {
            Files.copy(is, Paths.get(filepath).resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }
}
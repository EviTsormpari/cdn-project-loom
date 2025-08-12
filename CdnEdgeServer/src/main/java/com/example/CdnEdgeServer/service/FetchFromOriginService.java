package com.example.CdnEdgeServer.service;

import com.example.CdnEdgeServer.model.FileMetadata;
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

//Auto prepei na einai ksexwristo service gia na doulepsei to @Cachable
//Stin ousia to cachable elegxetai apo ton Spring Proxy opote prepei na kli8ei apo to ena bean sto allo
//an to eixa sto allo service kai to kalousa kateyueian sto idio bean tote den 8a pairnoyse apo ton Spring Proxy kai
//den 8a akoyge tin cache wste na synde8ei me redis

@Service
public class FetchFromOriginService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${origin.server.url}")
    private String originUrl;

    @Value("${edge.local.filepath}")
    private String edgeLocalFilepath;

    //To @Cachable apo tin Redis elegxei apo mono tou an yparxei to filename sto redis
    //an denyparxei kalei tin synartisi fetchFileMetadataAndDownloadFile gia na ginei i klisi apo ton origin kai epistrefei to FileMetadata
    //an yparxei epistrefei apo tin redis to FileMetdata
    @Cacheable(value = "fileMetadataCache", key = "#filename", sync = true)
    public FileMetadata fetchFileMetadataAndDownloadFile (String filename) {
        System.out.println("fetchFileMetadataAndDownloadFile called for " + filename);
        //Klisi ston origin gia na paroume ta metadata kai to arxeio
        //Using RestTemplate
        ResponseEntity<Resource> response = restTemplate.exchange(
                originUrl + filename,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Resource.class);

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
        fileMetadata.setFilepath(Paths.get(edgeLocalFilepath).resolve(filename).toString());

        return fileMetadata;
    }

    private void downloadFile(ResponseEntity<Resource> response, FileMetadata fileMetadata) {
        String filename = fileMetadata.getFilename();
        String filepath = fileMetadata.getFilepath();

        //Edw kanoume download to arxeio sto pc tou edge server
        //Pairnoume to InputStream apo to InputStreamResource kai me files.copy to kanoyme copy opou 8eloume
        try (InputStream is = response.getBody().getInputStream()) {
            Files.copy(is, Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }
}
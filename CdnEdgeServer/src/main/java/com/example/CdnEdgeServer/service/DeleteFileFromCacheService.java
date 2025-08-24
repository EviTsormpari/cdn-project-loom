package com.example.CdnEdgeServer.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class DeleteFileFromCacheService {

    //To @CacheEvict kaleitai kai diagrafetai to key filename
    //To @CacheEvict elegxei mono tou gia to an yparxei to arxeio opote i efamogi den 8a skasei
    @CacheEvict(value = "fileMetadataCache", key = "#filename")
    public void deleteFileByFilename(String filename) { }
}
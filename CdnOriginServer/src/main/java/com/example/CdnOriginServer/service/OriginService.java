package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OriginService {
    private OriginRepository originRepository;

    @Autowired
    public OriginService (OriginRepository originRepository) { this.originRepository = originRepository; }

//    public FileMetadata getFilenameByName (String filename) {
//
//    }
}

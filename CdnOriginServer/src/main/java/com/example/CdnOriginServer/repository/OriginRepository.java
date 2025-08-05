package com.example.CdnOriginServer.repository;

import com.example.CdnOriginServer.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginRepository extends JpaRepository<FileMetadata, Long> {

    FileMetadata findByFilename (String filename);
}

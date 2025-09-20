package com.example.CdnOriginServer.repository;

import com.example.CdnOriginServer.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.*;

/* Το OriginRepository αξιοποιεί το Spring Data JPA για την παροχή βασικών CRUD λειτουργιών,
 ενώ επιτρέπει τον ορισμό επιπλέον μεθόδων αναζήτησης ή διαγραφής με βάση συγκεκριμένα πεδία (π.χ. filename).
 */

@Repository
public interface OriginRepository extends JpaRepository<FileMetadata, String> {
    FileMetadata findByFilename(String filename);
    boolean existsByFilename(String filename);
    void deleteByFilename(String filename);
}
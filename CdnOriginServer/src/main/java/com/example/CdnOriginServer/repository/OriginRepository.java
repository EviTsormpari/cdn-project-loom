package com.example.CdnOriginServer.repository;

import com.example.CdnOriginServer.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
Το OriginRepository μέσω του Spring Data JPA παρέχει μεθόδους για την αναζήτηση, εισαγωγή και διαγραφή
αντικειμένων στη βάση δεδομένων με βάση το Id.

Επιπλέον παρέχεται η δυνατότητα ορισμού νέων μεθόδων αναζήτησης ή τροποποίησης με βάση άλλα
πεδία του αντικειμένου, ώστε να βελτιωθεί η αναγνωσιμότητα και η ευχρηστία του κώδικα.

Για απλές μεθόδους το Spring Data JPA δημιουργεί αυτόματα το κατάλληλο query με βάση τη σύμβαση
ονοματοδοσίας της μεθόδου, χωρίς να απαιτείται χειροκίνητη υλοποίηση.
 */

@Repository
public interface OriginRepository extends JpaRepository<FileMetadata, String> {

    FileMetadata findByFilename(String filename);

    void deleteByFilename(String filename);

}
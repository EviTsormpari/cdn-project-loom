package com.example.CdnOriginServer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.domain.Persistable;


//Το FileMetadata είναι η κλάση για την αναπαράσταση των δεδομένων που αποθηκεύονται στη βάση δεδομένων.

@Entity
public class FileMetadata implements Persistable<String> {
    @Id
    private String filename;
    private String filepath;
    private String filetype;
    private Long filesize;

    public FileMetadata() { }

    /*
    Η μέθοδος isNew() χρησιμοποιείται από το Spring Data για να καθορίσει
    αν το αντικείμενο είναι καινούριο ή αν υπάρχει ήδη στη βάση δεδομένων ελέγχοντας το id.

    Κάνοντας Override τη μέθοδο δίνεται η δυνατότητα ελέγχου ύπαρξης ενός αντικειμένου
    στη βάση σύμφωνα με το filename (το οποίο για τις ανάγκες του project αποτελεί το id).

    Αν επιστρέψει true (δηλαδή όταν το filename είναι null), η .save() θα κάνει INSERT στη βάση.
    Αν επιστρέψει false (δηλαδή όταν υπάρχει τιμή στο filename), η .save() θα θεωρήσει ότι το
    αντικείμενο υπάρχει ήδη και θα κάνει UPDATE με βάση το filename.
     */
    @Override
    public boolean isNew() { return filename == null; }

    public String getId() { return filename; }

    public void setId(String id) { this.filename = id; }

    public String getFilepath() { return filepath; }

    public void setFilepath(String filepath) { this.filepath = filepath; }

    public String getFiletype() { return filetype; }

    public void setFiletype(String filetype) { this.filetype = filetype; }

    public Long getFilesize() { return filesize; }

    public void setFilesize(Long filesize) { this.filesize = filesize; }
}
package com.example.CdnOriginServer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.domain.Persistable;

@Entity
public class FileMetadata implements Persistable<String> {
    @Id
    private String filename;
    private String filepath;
    private String filetype;
    private Long filesize;

    public FileMetadata() { }

    //Me auti tin synartisi i .save() 8a elegxei an yparxei stin basi to arxeio symfwna me to filename
    @Override
    public boolean isNew() { return filename == null; }

    public String getId() { return filename; }

    public void setId(String id) { this.filename = id; }

//    public String getFilename() { return filename; }
//
//    public void setFilename(String filename) { this.filename = filename; }

    public String getFilepath() { return filepath; }

    public void setFilepath(String filepath) { this.filepath = filepath; }

    public String getFiletype() { return filetype; }

    public void setFiletype(String filetype) { this.filetype = filetype; }

    public Long getFilesize() { return filesize; }

    public void setFilesize(Long filesize) { this.filesize = filesize; }
}
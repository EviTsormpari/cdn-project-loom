package com.example.CdnEdgeServer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/*
Το FileMetadata είναι η κλάση για την αναπαράσταση των δεδομένων που αποθηκεύονται στην κρυφή μνήμη.
 */

@RedisHash
public class FileMetadata {
    @Id
    private String filename;
    private String filepath;
    private String filetype;
    private Long filesize;

    public FileMetadata() { }

    public String getId() { return filename; }

    public void setId(String filename) { this.filename = filename; }

    public String getFilepath() { return filepath; }

    public void setFilepath(String filepath) { this.filepath = filepath; }

    public String getFiletype() { return filetype; }

    public void setFiletype(String filetype) { this.filetype = filetype; }

    public Long getFilesize() { return filesize; }

    public void setFilesize(Long filesize) { this.filesize = filesize; }
}
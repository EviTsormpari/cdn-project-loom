package com.example.CdnEdgeServer.model;

public class FileMetadata {

    private long id;
    private String filename;
    private String filepath;
    private String filetype;
    private Long filesize;

    public FileMetadata() { }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public String getFilepath() { return filepath; }

    public void setFilepath(String filepath) { this.filepath = filepath; }

    public String getFiletype() { return filetype; }

    public void setFiletype(String filetype) { this.filetype = filetype; }

    public Long getFilesize() { return filesize; }

    public void setFilesize(Long filesize) { this.filesize = filesize; }
}
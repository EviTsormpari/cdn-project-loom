package com.example.CdnOriginServer.component;

import com.example.CdnOriginServer.model.FileMetadata;
import com.example.CdnOriginServer.repository.OriginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;

@Component
public class FileInitializer implements CommandLineRunner {

    private final OriginRepository originRepository;
    @Value("${origin.local.filepath}")
    private String originFilepath;

    @Autowired
    public FileInitializer(OriginRepository originRepository) { this.originRepository = originRepository; }

    @Override
    public void run(String... args) throws Exception {
        File folder = new File(originFilepath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File f: files) {
                if (f.isFile()) {
                    FileMetadata existingFile = originRepository.findByFilename(f.getName());
                    if (existingFile == null) {
                        FileMetadata fileMetadata = new FileMetadata();
                        fileMetadata.setId(f.getName());
                        fileMetadata.setFilepath(f.getPath());
                        fileMetadata.setFilesize(f.length());
                        String mimeType = Files.probeContentType(f.toPath());
                        if (mimeType == null) {
                            mimeType = "application/octet-stream";
                        }
                        fileMetadata.setFiletype(mimeType);
                        originRepository.save(fileMetadata);
                    }
                }
            }
        }
    }
}
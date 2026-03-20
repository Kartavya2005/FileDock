package com.fileupload.controller;

import com.fileupload.Service.FileService;
import com.fileupload.config.FileuploadConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/files")
@CrossOrigin
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileuploadConfig config;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            String uploadDir = config.getUploadDir();
            Path path = new File(uploadDir).toPath().resolve(filename);

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("File not found");
            }

            // Detect file type
            String contentType = java.nio.file.Files.probeContentType(path);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        File folder = new File(config.getUploadDir());

        String[] files = folder.list();

        if (files == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        return ResponseEntity.ok(Arrays.asList(files));
    }
}
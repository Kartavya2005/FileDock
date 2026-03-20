package com.fileupload.Service;

import com.fileupload.config.FileuploadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private FileuploadConfig config;

    private final String[] allowedTypes = {
            "image/png",
            "image/jpeg",
            "application/pdf",
            "text/plain"
    };

    public String uploadFile(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        boolean isValid = false;
        for (String type : allowedTypes) {
            if (type.equals(file.getContentType())) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new RuntimeException("Invalid file type");
        }

        String uploadDir = config.getUploadDir();
        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        String filePath = uploadDir + File.separator + fileName;
        file.transferTo(new File(filePath));

        return fileName;
    }
}
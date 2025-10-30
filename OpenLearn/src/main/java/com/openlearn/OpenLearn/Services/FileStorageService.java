package com.openlearn.OpenLearn.Services;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    private final String fileServePath; // Base path for the URL

    // Inject the storage directory path from application.properties
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir,
                              @Value("${file.serve-path}") String servePath) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.fileServePath = servePath; // e.g., "/uploads"
    }

    @PostConstruct // Run after bean creation
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
            System.out.println("Upload directory created/checked at: " + this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the upload directory.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Filename contains invalid path sequence " + originalFilename);
            }

            // Generate a unique filename using UUID + extension
            String fileExtension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFilename.substring(dotIndex);
            }
            String uniqueFilename = UUID.randomUUID() + fileExtension;

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            String fileUrl = fileServePath + uniqueFilename;
            System.out.println("File stored: " + targetLocation);
            System.out.println("Access URL: " + fileUrl);
            return fileUrl;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }
        public boolean deleteFile(String filename) {
            try {
                Path targetLocation = this.fileStorageLocation.resolve(filename).normalize();
                return Files.deleteIfExists(targetLocation);
            } catch (IOException ex) {
                System.err.println("Error deleting file: " + filename + " " + ex.getMessage());
                return false;
            }
        }
}

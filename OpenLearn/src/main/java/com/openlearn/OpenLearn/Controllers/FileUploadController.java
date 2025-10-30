package com.openlearn.OpenLearn.Controllers;
import com.openlearn.OpenLearn.Services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/content")
public class FileUploadController {
    @Autowired
    private FileStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = storageService.storeFile(file);
            UploadResponse response = new UploadResponse(fileUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new UploadResponse("Failed to upload file: " + e.getMessage()));
        }
    }
    @DeleteMapping("/delete/{filename:.+}") // :.+ para capturar nombres con puntos
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            boolean deleted = storageService.deleteFile(filename);
            if (deleted) {
                return ResponseEntity.ok("File deleted successfully: " + filename);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + filename);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not delete file: " + filename + ". Error: " + e.getMessage());
        }
    }
}
record UploadResponse(String fileUrl) {}


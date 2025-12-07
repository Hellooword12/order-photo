package com.example.Order_Photo.controller.front;

import com.example.Order_Photo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        try {
            // Проверяем существование файла
            if (!fileStorageService.fileExists(fileName)) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = fileStorageService.getFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Определяем Content-Type
                String contentType = determineContentType(fileName);

                // Определяем размер файла для заголовков
                long fileSize = fileStorageService.getFileSize(fileName);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .contentLength(fileSize)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error serving file " + fileName + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            // Проверяем существование файла
            if (!fileStorageService.fileExists(fileName)) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = fileStorageService.getFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                long fileSize = fileStorageService.getFileSize(fileName);

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(fileSize)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error downloading file " + fileName + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // Метод для проверки существования файла
    @GetMapping("/exists/{fileName:.+}")
    public ResponseEntity<Boolean> fileExists(@PathVariable String fileName) {
        boolean exists = fileStorageService.fileExists(fileName);
        return ResponseEntity.ok(exists);
    }

    // Метод для получения информации о файле
    @GetMapping("/info/{fileName:.+}")
    public ResponseEntity<?> getFileInfo(@PathVariable String fileName) {
        try {
            if (!fileStorageService.fileExists(fileName)) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = fileStorageService.getFilePath(fileName);
            long fileSize = fileStorageService.getFileSize(fileName);
            String contentType = determineContentType(fileName);

            java.util.Map<String, Object> fileInfo = new java.util.HashMap<>();
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("contentType", contentType);
            fileInfo.put("exists", true);

            return ResponseEntity.ok(fileInfo);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String determineContentType(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();

        if (lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCaseFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerCaseFileName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCaseFileName.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerCaseFileName.endsWith(".pdf")) {
            return "application/pdf";
        } else {
            // Пытаемся определить по содержимому
            try {
                Path filePath = fileStorageService.getFilePath(fileName);
                String detectedType = Files.probeContentType(filePath);
                return detectedType != null ? detectedType : "application/octet-stream";
            } catch (Exception e) {
                return "application/octet-stream";
            }
        }
    }
}
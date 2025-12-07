package com.example.Order_Photo.controller.front;

import com.example.Order_Photo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("=== FILE UPLOAD DEBUG ===");
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());
            System.out.println("Is empty: " + file.isEmpty());

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Файл пустой");
            }

            if (file.getSize() == 0) {
                return ResponseEntity.badRequest().body("Размер файла 0 байт");
            }

            String fileName = fileStorageService.storeFile(file);
            System.out.println("Stored as: " + fileName);
            System.out.println("========================");

            return ResponseEntity.ok(fileName);
        } catch (Exception e) {
            System.err.println("File upload error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
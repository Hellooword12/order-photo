package com.example.Order_Photo.controller.front;

import com.example.Order_Photo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class PhotoUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/photo")
    public ResponseEntity<?> uploadOrderPhoto(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("=== ORDER PHOTO UPLOAD DEBUG ===");
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Файл пустой");
            }

            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Файл должен быть изображением");
            }

            String fileName = fileStorageService.storeFile(file);

            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("fileUrl", "/api/files/" + fileName);
            response.put("message", "Фото успешно загружено");

            System.out.println("Upload successful: " + fileName);
            System.out.println("========================");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Order photo upload error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Ошибка загрузки: " + e.getMessage());
        }
    }
}
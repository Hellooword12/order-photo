package com.example.Order_Photo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private final AtomicInteger counter = new AtomicInteger(1);

    public String storeFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }

            String fileName = generateSimpleName(file);
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            System.out.println("=== FILE STORAGE DEBUG ===");
            System.out.println("Original filename: " + file.getOriginalFilename());
            System.out.println("Stored as: " + fileName);
            System.out.println("Full path: " + filePath.toAbsolutePath());
            System.out.println("File size: " + Files.size(filePath) + " bytes");
            System.out.println("Web URL: /api/files/" + fileName);
            System.out.println("==========================");

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл: " + e.getMessage(), e);
        }
    }

    public String storeServiceImage(MultipartFile file) {
        try {
            // Создаем директорию services внутри uploads
            Path servicesDir = Paths.get(uploadDir, "services");
            if (!Files.exists(servicesDir)) {
                Files.createDirectories(servicesDir);
                System.out.println("Created directory: " + servicesDir.toAbsolutePath());
            }

            String fileName = generateServiceImageName(file);
            Path filePath = servicesDir.resolve(fileName);

            // Сохраняем файл
            Files.copy(file.getInputStream(), filePath);

            System.out.println("Service image saved: " + fileName);
            System.out.println("Full path: " + filePath.toAbsolutePath());
            System.out.println("Web URL: /uploads/services/" + fileName);

            // Возвращаем относительный путь для web-доступа
            return "/uploads/services/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить изображение: " + e.getMessage(), e);
        }
    }

    private String generateServiceImageName(MultipartFile file) {
        long timestamp = System.currentTimeMillis();
        int count = counter.getAndIncrement();
        String extension = getSafeExtension(file.getOriginalFilename());
        return "service_" + timestamp + "_" + count + extension;
    }

    // Метод для получения списка всех изображений услуг
    public List<String> getAllServiceImages() {
        try {
            Path servicesPath = Paths.get(uploadDir, "services");
            if (!Files.exists(servicesPath)) {
                Files.createDirectories(servicesPath);
                return List.of();
            }

            return Files.list(servicesPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                                fileName.endsWith(".png") || fileName.endsWith(".webp") ||
                                fileName.endsWith(".gif") || fileName.endsWith(".bmp");
                    })
                    .map(path -> "/uploads/services/" + path.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось получить список изображений", e);
        }
    }

    // Метод для удаления изображения услуги
    public boolean deleteServiceImage(String imageUrl) {
        try {
            if (imageUrl == null || !imageUrl.startsWith("/uploads/services/")) {
                return false;
            }

            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, "services", fileName);

            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                System.out.println("Deleted image: " + filePath.toAbsolutePath());
            }
            return deleted;
        } catch (IOException e) {
            System.err.println("Error deleting service image: " + e.getMessage());
            return false;
        }
    }

    // Метод для получения полного пути к файлу
    public Path getFilePath(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("FileName cannot be null or empty");
        }

        // Убираем начальный слеш если есть
        String cleanFileName = fileName.startsWith("/") ? fileName.substring(1) : fileName;

        Path filePath = Paths.get(uploadDir).resolve(cleanFileName);
        System.out.println("Resolved file path: " + filePath.toAbsolutePath());

        return filePath;
    }

    // Метод для проверки существования файла
    public boolean fileExists(String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                return false;
            }

            // Убираем начальный слеш если есть
            String cleanFileName = fileName.startsWith("/") ? fileName.substring(1) : fileName;

            Path filePath = Paths.get(uploadDir).resolve(cleanFileName);
            boolean exists = Files.exists(filePath) && Files.isRegularFile(filePath);

            System.out.println("Checking file: " + cleanFileName);
            System.out.println("Full path: " + filePath.toAbsolutePath());
            System.out.println("Exists: " + exists);

            return exists;
        } catch (Exception e) {
            System.err.println("Error checking file existence: " + e.getMessage());
            return false;
        }
    }

    // Метод для получения размера файла
    public long getFileSize(String fileName) {
        try {
            Path filePath = getFilePath(fileName);
            return Files.size(filePath);
        } catch (Exception e) {
            return 0;
        }
    }

    private String generateSimpleName(MultipartFile file) {
        // timestamp + счетчик
        long timestamp = System.currentTimeMillis();
        int count = counter.getAndIncrement();
        String extension = getSafeExtension(file.getOriginalFilename());
        return "photo_" + timestamp + "_" + count + extension;
    }

    private String getSafeExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }

        String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        if (ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") ||
                ext.equals(".gif") || ext.equals(".webp") || ext.equals(".bmp")) {
            return ext;
        }

        return ".jpg";
    }

    // Метод для удаления файла
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = getFilePath(fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }
    }

    // Метод для получения списка всех файлов
    public java.util.List<String> listAllFiles() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                return java.util.Collections.emptyList();
            }

            return Files.list(uploadPath)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(java.util.stream.Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось получить список файлов", e);
        }
    }
}
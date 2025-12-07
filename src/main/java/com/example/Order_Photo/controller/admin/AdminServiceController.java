package com.example.Order_Photo.controller.admin;

import com.example.Order_Photo.model.Service;
import com.example.Order_Photo.model.ServiceSize;
import com.example.Order_Photo.service.FileStorageService;
import com.example.Order_Photo.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private FileStorageService fileStorageService;

    // Страница списка услуг
    @GetMapping
    public String servicesList(Model model) {
        List<Service> services = serviceService.getAllServices();
        model.addAttribute("services", services);
        model.addAttribute("pageTitle", "Управление услугами");
        return "admin/services-list";
    }

    // Форма создания новой услуги
    @GetMapping("/new")
    public String createServiceForm(Model model) {
        Service service = new Service();
        service.setActive(true);
        service.setDisplayOrder(0);
        service.setPrice(BigDecimal.ZERO);

        // Получаем список доступных изображений
        List<String> availableImages = fileStorageService.getAllServiceImages();

        model.addAttribute("service", service);
        model.addAttribute("availableImages", availableImages);
        model.addAttribute("pageTitle", "Добавить услугу");
        return "admin/service-form";
    }

    // Форма редактирования услуги
    @GetMapping("/edit/{id}")
    public String editServiceForm(@PathVariable Long id, Model model) {
        Service service = serviceService.getServiceById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Получаем список доступных изображений
        List<String> availableImages = fileStorageService.getAllServiceImages();

        model.addAttribute("service", service);
        model.addAttribute("availableImages", availableImages);
        model.addAttribute("pageTitle", "Редактировать услугу");
        return "admin/service-form";
    }

    // Сохранение новой услуги
    @PostMapping("/save")
    public String saveService(@ModelAttribute("service") Service service,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            // Обработка загрузки изображения
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileStorageService.storeServiceImage(imageFile);
                service.setImageUrl(imageUrl);
            }

            // Устанавливаем связь для всех размеров с сервисом
            if (service.getSizes() != null) {
                for (ServiceSize size : service.getSizes()) {
                    size.setService(service);
                    size.setActive(true);
                    if (size.getDisplayOrder() == null) {
                        size.setDisplayOrder(0);
                    }
                }
            }

            serviceService.saveService(service);
            redirectAttributes.addFlashAttribute("successMessage", "Услуга успешно сохранена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при сохранении услуги: " + e.getMessage());
            e.printStackTrace(); // Добавьте для отладки
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/update/{id}")
    public String updateService(@PathVariable Long id,
                                @ModelAttribute("service") Service service,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                @RequestParam(value = "deleteCurrentImage", defaultValue = "false") boolean deleteCurrentImage,
                                RedirectAttributes redirectAttributes) {
        try {
            Service existingService = serviceService.getServiceById(id)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            // Обработка удаления текущего изображения
            if (deleteCurrentImage && existingService.getImageUrl() != null) {
                fileStorageService.deleteServiceImage(existingService.getImageUrl());
                service.setImageUrl(null);
            }

            // Обработка загрузки нового изображения
            if (imageFile != null && !imageFile.isEmpty()) {
                if (existingService.getImageUrl() != null) {
                    fileStorageService.deleteServiceImage(existingService.getImageUrl());
                }
                String imageUrl = fileStorageService.storeServiceImage(imageFile);
                service.setImageUrl(imageUrl);
            } else if (!deleteCurrentImage) {
                // Сохраняем текущее изображение, если не загружено новое и не отмечено удаление
                service.setImageUrl(existingService.getImageUrl());
            }

            // Устанавливаем связь для всех размеров с сервисом
            if (service.getSizes() != null) {
                for (ServiceSize size : service.getSizes()) {
                    size.setService(service);
                    if (size.getDisplayOrder() == null) {
                        size.setDisplayOrder(0);
                    }
                }
            }

            serviceService.updateService(id, service);
            redirectAttributes.addFlashAttribute("successMessage", "Услуга успешно обновлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении услуги: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/services";
    }

    // Удаление услуги
    @PostMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            Service service = serviceService.getServiceById(id)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            // Удаляем связанное изображение
            if (service.getImageUrl() != null) {
                fileStorageService.deleteServiceImage(service.getImageUrl());
            }

            serviceService.deleteService(id);
            redirectAttributes.addFlashAttribute("successMessage", "Услуга успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении услуги: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    // Быстрое обновление активности
    @PostMapping("/toggle-active/{id}")
    public String toggleActive(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            Service service = serviceService.getServiceById(id)
                    .orElseThrow(() -> new RuntimeException("Service not found"));
            service.setActive(!service.isActive());
            serviceService.saveService(service);
            redirectAttributes.addFlashAttribute("successMessage", "Статус услуги изменен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при изменении статуса: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    // API для загрузки изображения (для AJAX)
    @PostMapping("/upload-image")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return "Ошибка: файл пустой";
            }

            String imageUrl = fileStorageService.storeServiceImage(file);
            return imageUrl;
        } catch (Exception e) {
            return "Ошибка загрузки: " + e.getMessage();
        }
    }
}
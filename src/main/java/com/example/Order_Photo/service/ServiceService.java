package com.example.Order_Photo.service;

import com.example.Order_Photo.model.Service;
import com.example.Order_Photo.model.ServiceSize;
import com.example.Order_Photo.repository.OrderItemRepository;
import com.example.Order_Photo.repository.OrderRepository;
import com.example.Order_Photo.repository.ServiceRepository;
import com.example.Order_Photo.repository.ServiceSizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceSizeRepository serviceSizeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<Service> getAllActiveServices() {
        List<Service> services = serviceRepository.findByActiveTrueOrderByDisplayOrderAsc();

        // загружаем все размеры и фильтруем
        for (Service service : services) {
            List<ServiceSize> sizes = serviceSizeRepository.findByServiceIdOrderByDisplayOrderAsc(service.getId());
            if (sizes != null) {
                // Фильтруем активные размеры
                List<ServiceSize> activeSizes = sizes.stream()
                        .filter(ServiceSize::isActive)
                        .collect(Collectors.toList());
                service.setSizes(activeSizes);
            } else {
                service.setSizes(new ArrayList<>());
            }
        }

        return services;
    }

    public List<Service> getAllServices() {
        List<Service> services = serviceRepository.findAllByOrderByDisplayOrderAsc();
        // Загружаем размеры для каждого сервиса
        services.forEach(service ->
                service.setSizes(serviceSizeRepository.findByServiceIdOrderByDisplayOrderAsc(service.getId()))
        );
        return services;
    }

    public Optional<Service> getServiceById(Long id) {
        Optional<Service> service = serviceRepository.findById(id);
        service.ifPresent(s ->
                s.setSizes(serviceSizeRepository.findByServiceIdOrderByDisplayOrderAsc(s.getId()))
        );
        return service;
    }

    @Transactional
    public Service saveService(Service service) {
        // Сохраняем сервис сначала (без размеров)
        Service savedService = serviceRepository.save(service);

        // Сохраняем размеры с установленной связью
        if (service.getSizes() != null && !service.getSizes().isEmpty()) {
            for (ServiceSize size : service.getSizes()) {
                size.setService(savedService); // Устанавливаем связь
                serviceSizeRepository.save(size);
            }
        }

        return savedService;
    }

    @Transactional
    public Service updateService(Long id, Service serviceDetails) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Обновляем основные поля
        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setImageUrl(serviceDetails.getImageUrl());
        service.setIconClass(serviceDetails.getIconClass());
        service.setGradientFrom(serviceDetails.getGradientFrom());
        service.setGradientTo(serviceDetails.getGradientTo());
        service.setButtonColor(serviceDetails.getButtonColor());
        service.setDisplayOrder(serviceDetails.getDisplayOrder());
        service.setActive(serviceDetails.isActive());

        // Обновляем размеры через правильный метод
        if (serviceDetails.getSizes() != null) {
            updateServiceSizes(service, serviceDetails.getSizes());
        }

        return serviceRepository.save(service);
    }

    @Transactional
    public void deleteService(Long id) {
        // Сначала удаляем связанные order_items
        orderItemRepository.deleteByServiceId(id);

        // Затем удаляем размеры
        serviceSizeRepository.deleteByServiceId(id);

        // Затем удаляем сервис
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Удаляем изображение
        if (service.getImageUrl() != null) {
            fileStorageService.deleteServiceImage(service.getImageUrl());
        }

        serviceRepository.deleteById(id);
    }

    @Transactional
    private void updateServiceSizes(Service service, List<ServiceSize> newSizes) {
        // Получаем текущие размеры
        List<ServiceSize> currentSizes = new ArrayList<>(service.getSizes());

        // Удаляем размеры, которых нет в новых данных
        List<ServiceSize> sizesToRemove = currentSizes.stream()
                .filter(currentSize -> newSizes.stream()
                        .noneMatch(newSize -> newSize.getId() != null &&
                                newSize.getId().equals(currentSize.getId())))
                .collect(Collectors.toList());

        for (ServiceSize sizeToRemove : sizesToRemove) {
            service.removeSize(sizeToRemove);
            serviceSizeRepository.delete(sizeToRemove);
        }

        // Обновляем или добавляем размеры
        for (int i = 0; i < newSizes.size(); i++) {
            ServiceSize newSize = newSizes.get(i);
            newSize.setDisplayOrder(i);

            if (newSize.getId() != null) {
                // Обновляем существующий размер
                ServiceSize existingSize = service.getSizes().stream()
                        .filter(s -> s.getId().equals(newSize.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingSize != null) {
                    existingSize.setSize(newSize.getSize());
                    existingSize.setSizeName(newSize.getSizeName());
                    existingSize.setPrice(newSize.getPrice());
                    existingSize.setDisplayOrder(newSize.getDisplayOrder());
                    existingSize.setActive(true);
                }
            } else {
                // Добавляем новый размер
                newSize.setService(service);
                service.addSize(newSize);
            }
        }
    }

    // Методы для работы с размерами
    public ServiceSize saveServiceSize(ServiceSize serviceSize) {
        return serviceSizeRepository.save(serviceSize);
    }

    public void deleteServiceSize(Long sizeId) {
        serviceSizeRepository.deleteById(sizeId);
    }

    public List<ServiceSize> getServiceSizes(Long serviceId) {
        return serviceSizeRepository.findByServiceIdOrderByDisplayOrderAsc(serviceId);
    }
}
package com.example.Order_Photo.config;

import com.example.Order_Photo.model.Service;
import com.example.Order_Photo.model.ServiceSize;
import com.example.Order_Photo.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader {

    @Autowired
    private ServiceRepository serviceRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        // Проверяем, есть ли уже услуги в базе данных
        List<Service> existingServices = serviceRepository.findAll();

        if (existingServices.isEmpty()) {
            createSampleData();
        } else {
            System.out.println("В базе данных уже есть услуги. Пропускаем создание тестовых данных.");
        }
    }

    private void createSampleData() {
        System.out.println("Создание тестовых данных...");

        // Пример создания услуги 1
        Service service1 = new Service();
        service1.setName("Печать фотографий");
        service1.setDescription("Качественная печать фотографий на матовой бумаге");
        service1.setImageUrl("/images/printing.jpg");
        service1.setIconClass("fa-print");
        service1.setGradientFrom("#667eea");
        service1.setGradientTo("#764ba2");
        service1.setButtonColor("#667eea");
        service1.setDisplayOrder(1);
        service1.setActive(true);

        ServiceSize size1 = new ServiceSize();
        size1.setSize("10x15");
        size1.setSizeName("10x15 см");
        size1.setPrice(new BigDecimal("50.00"));
        size1.setDisplayOrder(1);
        size1.setActive(true);

        ServiceSize size2 = new ServiceSize();
        size2.setSize("15x20");
        size2.setSizeName("15x20 см");
        size2.setPrice(new BigDecimal("80.00"));
        size2.setDisplayOrder(2);
        size2.setActive(true);

        ServiceSize size3 = new ServiceSize();
        size3.setSize("20x30");
        size3.setSizeName("20x30 см");
        size3.setPrice(new BigDecimal("120.00"));
        size3.setDisplayOrder(3);
        size3.setActive(true);

        service1.addSize(size1);
        service1.addSize(size2);
        service1.addSize(size3);

        serviceRepository.save(service1);

        // Пример создания услуги 2
        Service service2 = new Service();
        service2.setName("Обработка фото");
        service2.setDescription("Профессиональная обработка и ретушь фотографий");
        service2.setImageUrl("/images/editing.jpg");
        service2.setIconClass("fa-magic");
        service2.setGradientFrom("#f093fb");
        service2.setGradientTo("#f5576c");
        service2.setButtonColor("#f093fb");
        service2.setDisplayOrder(2);
        service2.setActive(true);

        ServiceSize size4 = new ServiceSize();
        size4.setSize("basic");
        size4.setSizeName("Базовая обработка");
        size4.setPrice(new BigDecimal("200.00"));
        size4.setDisplayOrder(1);
        size4.setActive(true);

        ServiceSize size5 = new ServiceSize();
        size5.setSize("pro");
        size5.setSizeName("Профессиональная ретушь");
        size5.setPrice(new BigDecimal("500.00"));
        size5.setDisplayOrder(2);
        size5.setActive(true);

        service2.addSize(size4);
        service2.addSize(size5);

        serviceRepository.save(service2);

        System.out.println("Тестовые данные успешно созданы!");
    }
}
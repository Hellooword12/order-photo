package com.example.Order_Photo.controller.front;

import com.example.Order_Photo.dto.ServiceDTO;
import com.example.Order_Photo.model.Service;
import com.example.Order_Photo.repository.ServiceRepository;
import com.example.Order_Photo.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServicesController {

    private final ServiceRepository serviceRepository;
    @Autowired
    private ServiceService serviceService;

    @GetMapping("/active")
    public ResponseEntity<List<ServiceDTO>> getActiveServices() {
        List<Service> services = serviceService.getAllActiveServices();

        List<ServiceDTO> serviceDTOs = services.stream()
                .map(ServiceDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(serviceDTOs);
    }
}
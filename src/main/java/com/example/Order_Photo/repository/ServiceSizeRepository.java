package com.example.Order_Photo.repository;

import com.example.Order_Photo.model.ServiceSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceSizeRepository extends JpaRepository<ServiceSize, Long> {
    List<ServiceSize> findByServiceIdAndActiveTrueOrderByDisplayOrderAsc(Long serviceId);
    List<ServiceSize> findByServiceIdOrderByDisplayOrderAsc(Long serviceId);
    void deleteByServiceId(Long serviceId);
    Optional<ServiceSize> findById(Long id);


}
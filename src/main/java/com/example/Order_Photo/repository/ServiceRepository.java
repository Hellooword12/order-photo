package com.example.Order_Photo.repository;


import com.example.Order_Photo.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByActiveTrue();
    List<Service> findByActiveTrueOrderByPriceAsc();
    Long countByActiveTrue();
    List<Service> findByActiveTrueOrderByDisplayOrderAsc();
    List<Service> findAllByOrderByDisplayOrderAsc();
}
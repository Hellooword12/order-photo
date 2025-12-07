package com.example.Order_Photo.repository;

import com.example.Order_Photo.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Найти все OrderItem по service_id
    List<OrderItem> findByServiceId(Long serviceId);

    // Удалить все OrderItem по service_id
    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.service.id = :serviceId")
    void deleteByServiceId(@Param("serviceId") Long serviceId);

    // существуют ли OrderItem для данного service_id
    boolean existsByServiceId(Long serviceId);
}
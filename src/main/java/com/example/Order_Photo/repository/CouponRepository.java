package com.example.Order_Photo.repository;

import com.example.Order_Photo.model.DiscountCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<DiscountCard, Long> {
    Optional<DiscountCard> findByCodeAndActiveTrue(String code);
    Optional<DiscountCard> findByCodeAndActiveTrueAndValidFromBeforeAndValidToAfter(
            String code, LocalDateTime now1, LocalDateTime now2);
}
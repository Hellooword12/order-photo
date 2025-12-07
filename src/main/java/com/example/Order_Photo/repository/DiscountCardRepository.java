package com.example.Order_Photo.repository;

import com.example.Order_Photo.model.DiscountCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCardRepository extends JpaRepository<DiscountCard, Long> {
    Optional<DiscountCard> findByCode(String code);
    List<DiscountCard> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<DiscountCard> findByUserIdAndActiveTrue(Long userId);
    List<DiscountCard> findAllByOrderByCreatedAtDesc();

    @Query("SELECT dc FROM DiscountCard dc WHERE dc.user.username = :username AND dc.active = true")
    List<DiscountCard> findActiveByUserUsername(@Param("username") String username);
}
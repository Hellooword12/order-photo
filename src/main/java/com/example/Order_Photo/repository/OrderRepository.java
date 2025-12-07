package com.example.Order_Photo.repository;

import com.example.Order_Photo.model.DiscountCard;
import com.example.Order_Photo.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);
    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Long countByUserUsername(String username);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user.username = :username")
    BigDecimal getTotalSpentByUser(@Param("username") String username);

    Long countByUserUsernameAndUsedDiscountCard(String username, DiscountCard discountCard);

    @Query("SELECT COALESCE(SUM(o.discountAmount), 0) FROM Order o WHERE o.user.username = :username AND o.usedDiscountCard = :discountCard")
    BigDecimal getTotalSavedWithCard(@Param("username") String username, @Param("discountCard") DiscountCard discountCard);

    // методы для поиска активных карт пользователя
    @Query("SELECT o.usedDiscountCard FROM Order o WHERE o.user.username = :username AND o.usedDiscountCard IS NOT NULL ORDER BY o.createdAt DESC")
    List<DiscountCard> findRecentUsedCardsByUser(@Param("username") String username);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.usedDiscountCard WHERE o.user.username = :username ORDER BY o.createdAt DESC")
    List<Order> findByUserUsernameOrderByCreatedAtDesc(@Param("username") String username);

    // Для пагинации и фильтрации
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    Page<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Order> findByStatusAndCreatedAtBetween(Order.OrderStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Для статистики
    long countByStatus(Order.OrderStatus status);

    // Поиск по email
    Page<Order> findByCustomerEmailContainingIgnoreCase(String email, Pageable pageable);

    // Получить заказы за последние N дней
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("startDate") LocalDateTime startDate);

    default List<Order> findTopNByOrderByCreatedAtDesc(int limit) {
        return findAll().stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }


    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    BigDecimal getTotalRevenue();

}
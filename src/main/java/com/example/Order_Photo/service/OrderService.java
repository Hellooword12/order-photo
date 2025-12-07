package com.example.Order_Photo.service;

import com.example.Order_Photo.dto.*;
import com.example.Order_Photo.model.*;
import com.example.Order_Photo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceSizeRepository serviceSizeRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final DiscountCardService discountCardService;
    private final DiscountCardRepository discountCardRepository;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        return createOrderInternal(orderRequest, null);
    }

    // метод для авторизованных пользователей
    @Transactional
    public OrderResponseDTO createOrderForUser(OrderRequestDTO orderRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return createOrderInternal(orderRequest, user);
    }

    // внутренний метод для создания заказа
    private OrderResponseDTO createOrderInternal(OrderRequestDTO orderRequest, User user) {
        Order order = new Order();

        // Устанавливаем пользователя если авторизован
        if (user != null) {
            order.setUser(user);
            order.setCustomerEmail(user.getEmail() != null ? user.getEmail() : orderRequest.getCustomerEmail());
            order.setCustomerPhone(user.getPhoneNumber() != null ? user.getPhoneNumber() : orderRequest.getCustomerPhone());
        } else {
            order.setCustomerEmail(orderRequest.getCustomerEmail());
            order.setCustomerPhone(orderRequest.getCustomerPhone());
        }

        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO itemDto : orderRequest.getItems()) {
            String photoUrl = itemDto.getPhotoUrl();

            // Обрабатываем все услуги для этого фото
            for (OrderServiceRequestDTO serviceDto : itemDto.getServices()) {
                com.example.Order_Photo.model.Service service = serviceRepository.findById(serviceDto.getServiceId())
                        .orElseThrow(() -> new RuntimeException("Услуга не найдена: " + serviceDto.getServiceId()));

                ServiceSize serviceSize = serviceSizeRepository.findById(serviceDto.getServiceSizeId())
                        .orElseThrow(() -> new RuntimeException("Размер услуги не найден: " + serviceDto.getServiceSizeId()));

                if (serviceSize.getPrice() == null) {
                    throw new RuntimeException("Цена не установлена для размера услуги: " + serviceSize.getSizeName());
                }

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setService(service);
                item.setServiceSize(serviceSize);
                item.setQuantity(serviceDto.getQuantity());

                // Обработка photoUrl
                if (photoUrl != null && photoUrl.length() > 255) {
                    photoUrl = photoUrl.substring(0, 255);
                }
                item.setPhotoUrl(photoUrl);
                item.setPrice(serviceSize.getPrice()); // Устанавливаем цену

                orderItems.add(item);

                // Расчет стоимости
                BigDecimal itemTotal = serviceSize.getPrice().multiply(BigDecimal.valueOf(serviceDto.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);

                System.out.println("Added item: " + service.getName() + " - " + serviceSize.getSizeName() +
                        ", Price: " + serviceSize.getPrice() + ", Qty: " + serviceDto.getQuantity() +
                        ", Total: " + itemTotal);
            }

        }

        order.setItems(orderItems);

        DiscountCard appliedCard = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        String cardCode = orderRequest.getDiscountCardCode();
        if (cardCode != null && !cardCode.isEmpty()) {
            try {

                DiscountCard card = discountCardService.findByCodeOrThrow(cardCode);
                if (card.isValid()) {
                    // Проверяем минимальную сумму заказа
                    if (card.getMinOrderAmount() != null && totalAmount.compareTo(card.getMinOrderAmount()) < 0) {
                        throw new RuntimeException(String.format("Минимальная сумма заказа для карты %s: %.2f ₽",
                                card.getCardName(), card.getMinOrderAmount()));
                    }

                    // Рассчитываем скидку
                    if (card.getDiscountType() == DiscountCard.DiscountType.PERCENTAGE) {
                        discountAmount = totalAmount.multiply(card.getDiscountValue().divide(BigDecimal.valueOf(100)));
                    } else {
                        discountAmount = card.getDiscountValue().min(totalAmount);
                    }

                    appliedCard = card;
                    order.setUsedDiscountCard(appliedCard);
                    order.setDiscountAmount(discountAmount);

                    // Обновляем статистику карты
                    discountCardService.updateCardStats(appliedCard.getId(), totalAmount);
                } else {
                    throw new RuntimeException("Карта недействительна");
                }
            } catch (RuntimeException e) {
                throw new RuntimeException("Ошибка применения карты: " + e.getMessage());
            }
        }

        // Устанавливаем итоговые суммы
        order.setTotalAmount(totalAmount.subtract(discountAmount));
        order.setDiscountAmount(discountAmount);

        Order savedOrder = orderRepository.save(order);

        // Отправляем уведомление
        notificationService.notifyAdminAboutNewOrder(savedOrder);

        return OrderResponseDTO.fromEntity(savedOrder);
    }

    public void updateCardStats(Long cardId, BigDecimal totalAmount) {
        // Логика обновления статистики карты
        DiscountCard card = discountCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));

        card.setUsageCount(card.getUsageCount() + 1);
        card.setTotalSpent(card.getTotalSpent().add(totalAmount));
        card.setTotalOrders(card.getTotalOrders() + 1);

        discountCardRepository.save(card);
    }

    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // Получение заказов пользователя
    public List<OrderResponseDTO> getUserOrders(String username) {
        List<Order> userOrders = orderRepository.findByUserUsernameOrderByCreatedAtDesc(username);
        return userOrders.stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public AdminStatisticsDTO getAdminStatistics() {
        Long totalOrders = orderRepository.count();
        Long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        Long totalServices = serviceRepository.countByActiveTrue();
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();

        return new AdminStatisticsDTO(totalOrders, pendingOrders, totalServices, totalRevenue);
    }

    public List<OrderDashboardDTO> getRecentOrdersForDashboard(int limit) {
        List<Order> recentOrders = orderRepository.findTopNByOrderByCreatedAtDesc(limit);
        return recentOrders.stream()
                .map(this::convertToDashboardDTO)
                .collect(Collectors.toList());
    }

    private OrderDashboardDTO convertToDashboardDTO(Order order) {
        OrderDashboardDTO dto = new OrderDashboardDTO();
        dto.setId(order.getId());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    public DiscountCardStatsDTO getDiscountCardStats(Long userId) {
        DiscountCardStatsDTO statsDTO = new DiscountCardStatsDTO();

        // Получаем заказы пользователя
        List<Order> userOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Базовая статистика
        statsDTO.setTotalOrders((long) userOrders.size());
        BigDecimal totalSpent = userOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statsDTO.setTotalSpent(totalSpent);

        // Статистика по активной карте
        List<DiscountCard> activeCards = discountCardService.getUserActiveCards(userId);
        if (!activeCards.isEmpty()) {
            DiscountCard card = activeCards.get(0);

            // Информация о карте
            DiscountCardStatsDTO.DiscountCardInfoDTO cardInfo = new DiscountCardStatsDTO.DiscountCardInfoDTO();
            cardInfo.setCode(card.getCode());
            cardInfo.setCardName(card.getCardName());
            cardInfo.setDiscountValue(card.getDiscountValue());
            cardInfo.setDiscountType(card.getDiscountType().name());
            cardInfo.setUsageCount(card.getUsageCount());
            cardInfo.setUsageLimit(card.getUsageLimit());
            statsDTO.setActiveCard(cardInfo);

            // Статистика использования карты
            DiscountCardStatsDTO.CardUsageStatsDTO cardStats = new DiscountCardStatsDTO.CardUsageStatsDTO();
            long ordersWithCard = userOrders.stream()
                    .filter(order -> order.getUsedDiscountCard() != null &&
                            order.getUsedDiscountCard().getId().equals(card.getId()))
                    .count();
            cardStats.setOrdersWithCard(ordersWithCard);

            BigDecimal totalSaved = userOrders.stream()
                    .filter(order -> order.getUsedDiscountCard() != null &&
                            order.getUsedDiscountCard().getId().equals(card.getId()))
                    .map(Order::getDiscountAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cardStats.setTotalSaved(totalSaved);

            statsDTO.setCardStats(cardStats);
        }

        return statsDTO;
    }

    @Transactional
    public OrderResponseDTO createOrderWithAuthCheck(OrderRequestDTO orderRequest, String username) {
        if (username != null && !username.isEmpty()) {
            return createOrderForUser(orderRequest, username);
        } else {
            return createOrder(orderRequest);
        }
    }

    public List<OrderResponseDTO> getAllOrdersForAdmin() {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return orders.stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderResponseDTO.fromEntity(order);
    }

    public UserStatsDto getUserStats(Long userId) {
        UserStatsDto stats = new UserStatsDto();

        List<Order> userOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Базовая статистика
        stats.setTotalOrders(userOrders.size());
        BigDecimal totalSpent = userOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalSpent(totalSpent);

        // Статистика по карте
        try {
            List<DiscountCard> activeCards = discountCardService.getUserActiveCards(userId);
            if (!activeCards.isEmpty()) {
                DiscountCard card = activeCards.get(0);
                stats.setDiscountCard(DiscountCardDto.fromEntity(card));

                // Рассчитываем статистику по использованию карты
                int ordersWithCard = 0;
                BigDecimal totalSaved = BigDecimal.ZERO;

                for (Order order : userOrders) {
                    if (order.getUsedDiscountCard() != null &&
                            order.getUsedDiscountCard().getId().equals(card.getId())) {
                        ordersWithCard++;
                        if (order.getDiscountAmount() != null) {
                            totalSaved = totalSaved.add(order.getDiscountAmount());
                        }
                    }
                }

                System.out.println("User ID: " + userId + ", Card ID: " + card.getId() +
                        ", Orders with card: " + ordersWithCard +
                        ", Total saved: " + totalSaved);

                stats.setOrdersWithCard(ordersWithCard);
                stats.setTotalSaved(totalSaved);
            } else {
                System.out.println("No active cards found for user: " + userId);
            }
        } catch (Exception e) {
            System.err.println("Error calculating card stats: " + e.getMessage());
            e.printStackTrace();
            stats.setOrdersWithCard(0);
            stats.setTotalSaved(BigDecimal.ZERO);
        }

        System.out.println("Final stats - Orders: " + stats.getTotalOrders() +
                ", Spent: " + stats.getTotalSpent() +
                ", Orders with card: " + stats.getOrdersWithCard() +
                ", Total saved: " + stats.getTotalSaved());

        return stats;
    }
}
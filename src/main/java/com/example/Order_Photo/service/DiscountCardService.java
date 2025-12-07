package com.example.Order_Photo.service;

import com.example.Order_Photo.dto.DiscountCardDto;
import com.example.Order_Photo.dto.DiscountCardStatsDTO;
import com.example.Order_Photo.dto.DiscountCardValidationResult;
import com.example.Order_Photo.model.DiscountCard;
import com.example.Order_Photo.model.User;
import com.example.Order_Photo.repository.DiscountCardRepository;
import com.example.Order_Photo.repository.OrderRepository;
import com.example.Order_Photo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiscountCardService {

    private final DiscountCardRepository discountCardRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public String generateCardCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            if (i > 0 && i % 4 == 0) {
                code.append("-");
            }
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }

        // Проверяем уникальность
        while (discountCardRepository.findByCode(code.toString()).isPresent()) {
            code = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                if (i > 0 && i % 4 == 0) {
                    code.append("-");
                }
                int index = (int) (Math.random() * chars.length());
                code.append(chars.charAt(index));
            }
        }

        return code.toString();
    }

    public List<DiscountCard> getAllCards() {
        return discountCardRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<DiscountCard> getCardById(Long id) {
        return discountCardRepository.findById(id);
    }

    public Optional<DiscountCard> getCardByCode(String code) {
        return discountCardRepository.findByCode(code);
    }

    public List<DiscountCard> getUserCards(Long userId) {
        return discountCardRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<DiscountCard> getUserActiveCards(Long userId) {
        return discountCardRepository.findByUserIdAndActiveTrue(userId);
    }

    @Transactional
    public DiscountCard createCard(DiscountCard card) {
        // Если код не указан, генерируем автоматически
        if (card.getCode() == null || card.getCode().isEmpty()) {
            card.setCode(generateCardCode());
        }

        // Проверяем уникальность кода
        if (discountCardRepository.findByCode(card.getCode()).isPresent()) {
            throw new RuntimeException("Карта с таким кодом уже существует");
        }

        return discountCardRepository.save(card);
    }

    @Transactional
    public DiscountCard updateCard(Long id, DiscountCard card) {
        DiscountCard existingCard = discountCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));

        // Проверяем уникальность кода (если изменился)
        if (!existingCard.getCode().equals(card.getCode()) &&
                discountCardRepository.findByCode(card.getCode()).isPresent()) {
            throw new RuntimeException("Карта с таким кодом уже существует");
        }

        existingCard.setCode(card.getCode());
        existingCard.setCardName(card.getCardName());
        existingCard.setDiscountType(card.getDiscountType());
        existingCard.setDiscountValue(card.getDiscountValue());
        existingCard.setUsageLimit(card.getUsageLimit());
        existingCard.setValidFrom(card.getValidFrom());
        existingCard.setValidTo(card.getValidTo());
        existingCard.setActive(card.isActive());

        // Обработка пользователя - только если передан userId
        if (card.getUser() != null && card.getUser().getId() != null) {
            User user = userRepository.findById(card.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            existingCard.setUser(user);
        } else {
            // Если userId не передан или null, отвязываем карту от пользователя
            existingCard.setUser(null);
        }

        return discountCardRepository.save(existingCard);
    }

    @Transactional
    public void deleteCard(Long id) {
        discountCardRepository.deleteById(id);
    }

    @Transactional
    public DiscountCard assignCardToUser(String cardCode, String userEmail) {
        DiscountCard card = discountCardRepository.findByCode(cardCode)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));

        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (card.getUser() != null) {
            throw new RuntimeException("Карта уже привязана к другому пользователю");
        }

        card.setUser(user);
        return discountCardRepository.save(card);
    }

    public DiscountCardValidationResult validateCard(String code, BigDecimal orderAmount) {
        Optional<DiscountCard> cardOpt = discountCardRepository.findByCode(code);

        if (cardOpt.isEmpty()) {
            return new DiscountCardValidationResult(false, "Карта не найдена");
        }

        DiscountCard card = cardOpt.get();

        if (!card.isActive()) {
            return new DiscountCardValidationResult(false, "Карта не активна");
        }

        LocalDateTime now = LocalDateTime.now();
        if (card.getValidFrom() != null && now.isBefore(card.getValidFrom())) {
            return new DiscountCardValidationResult(false, "Карта еще не действует");
        }

        if (card.getValidTo() != null && now.isAfter(card.getValidTo())) {
            return new DiscountCardValidationResult(false, "Срок действия карты истек");
        }

        if (card.getUsageLimit() != null && card.getUsageCount() >= card.getUsageLimit()) {
            return new DiscountCardValidationResult(false, "Лимит использований карты исчерпан");
        }

        if (orderAmount != null && card.getMinOrderAmount() != null &&
                orderAmount.compareTo(card.getMinOrderAmount()) < 0) {
            return new DiscountCardValidationResult(false,
                    String.format("Минимальная сумма заказа для этой карты: %.2f ₽", card.getMinOrderAmount()));
        }

        return new DiscountCardValidationResult(true, "Карта действительна", DiscountCardDto.fromEntity(card));
    }

    @Transactional
    public void updateCardStats(Long cardId, BigDecimal orderAmount) {
        DiscountCard card = discountCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));

        card.setUsageCount(card.getUsageCount() + 1);
        card.setTotalOrders(card.getTotalOrders() + 1);
        card.setTotalSpent(card.getTotalSpent().add(orderAmount));

        discountCardRepository.save(card);
    }

    public DiscountCard findByCodeOrThrow(String code) {
        return discountCardRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Карта не найдена с кодом: " + code));
    }

    public DiscountCardStatsDTO getUserDiscountCardStats(String username) {
        DiscountCardStatsDTO stats = new DiscountCardStatsDTO();

        // Общая статистика
        stats.setTotalOrders(orderRepository.countByUserUsername(username));
        stats.setTotalSpent(orderRepository.getTotalSpentByUser(username));

        // Активная скидочная карта
        DiscountCard activeCard = discountCardRepository.findActiveByUserUsername(username)
                .stream()
                .findFirst()
                .orElse(null);

        if (activeCard != null) {
            DiscountCardStatsDTO.DiscountCardInfoDTO cardInfo = new DiscountCardStatsDTO.DiscountCardInfoDTO();
            cardInfo.setCode(activeCard.getCode());
            cardInfo.setCardName(activeCard.getCardName());
            cardInfo.setDiscountValue(activeCard.getDiscountValue());
            cardInfo.setDiscountType(activeCard.getDiscountType().name());
            cardInfo.setUsageCount(activeCard.getUsageCount());
            cardInfo.setUsageLimit(activeCard.getUsageLimit());
            stats.setActiveCard(cardInfo);

            // Статистика использования карты
            DiscountCardStatsDTO.CardUsageStatsDTO cardStats = new DiscountCardStatsDTO.CardUsageStatsDTO();
            cardStats.setOrdersWithCard(orderRepository.countByUserUsernameAndUsedDiscountCard(username, activeCard));
            cardStats.setTotalSaved(orderRepository.getTotalSavedWithCard(username, activeCard));
            stats.setCardStats(cardStats);
        }

        return stats;
    }
}
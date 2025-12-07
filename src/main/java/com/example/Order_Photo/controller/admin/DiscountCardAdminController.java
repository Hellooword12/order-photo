package com.example.Order_Photo.controller.admin;

import com.example.Order_Photo.dto.DiscountCardDto;
import com.example.Order_Photo.model.DiscountCard;
import com.example.Order_Photo.model.User;
import com.example.Order_Photo.repository.UserRepository;
import com.example.Order_Photo.service.DiscountCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/discount-cards")
@RequiredArgsConstructor
public class DiscountCardAdminController {

    private final DiscountCardService discountCardService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<DiscountCardDto>> getAllCards() {
        try {
            List<DiscountCard> cards = discountCardService.getAllCards();
            List<DiscountCardDto> cardDtos = cards.stream()
                    .map(DiscountCardDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(cardDtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountCardDto> getCardById(@PathVariable Long id) {
        try {
            DiscountCard card = discountCardService.getCardById(id)
                    .orElseThrow(() -> new RuntimeException("Карта не найдена"));
            return ResponseEntity.ok(DiscountCardDto.fromEntity(card));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody DiscountCardDto cardDto) {
        try {
            DiscountCard card = cardDto.toEntity();

            // Обработка пользователя
            if (cardDto.getUserId() != null) {
                User user = userRepository.findById(cardDto.getUserId())
                        .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
                card.setUser(user);
            }

            DiscountCard savedCard = discountCardService.createCard(card);
            return ResponseEntity.ok(DiscountCardDto.fromEntity(savedCard));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при создании карты");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(@PathVariable Long id, @RequestBody DiscountCardDto cardDto) {
        try {
            DiscountCard card = cardDto.toEntity();

            // Обработка пользователя
            if (cardDto.getUserId() != null) {
                User user = userRepository.findById(cardDto.getUserId())
                        .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
                card.setUser(user);
            } else {
                // Если userId не передан, устанавливаем user в null
                card.setUser(null);
            }

            DiscountCard updatedCard = discountCardService.updateCard(id, card);
            return ResponseEntity.ok(DiscountCardDto.fromEntity(updatedCard));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при обновлении карты");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id) {
        try {
            discountCardService.deleteCard(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/unassign")
    public ResponseEntity<?> unassignCard(@PathVariable Long id) {
        try {
            DiscountCard card = discountCardService.getCardById(id)
                    .orElseThrow(() -> new RuntimeException("Карта не найдена"));

            card.setUser(null);
            discountCardService.updateCard(id, card);

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при отвязке карты");
        }
    }
}
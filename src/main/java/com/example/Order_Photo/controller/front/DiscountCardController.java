package com.example.Order_Photo.controller.front;

import com.example.Order_Photo.dto.DiscountCardDto;
import com.example.Order_Photo.dto.DiscountCardValidationResult;
import com.example.Order_Photo.model.DiscountCard;
import com.example.Order_Photo.model.User;
import com.example.Order_Photo.repository.UserRepository;
import com.example.Order_Photo.service.DiscountCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/discount-cards")
@RequiredArgsConstructor
public class DiscountCardController {

    private final DiscountCardService discountCardService;
    private final UserRepository userRepository;

    // Endpoint для проверки карты
    @GetMapping("/{code}/validate")
    public ResponseEntity<?> validateCard(@PathVariable String code,
                                          @RequestParam(required = false) BigDecimal orderAmount) {
        try {
            DiscountCardValidationResult result = discountCardService.validateCard(code, orderAmount);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("valid", false, "message", e.getMessage())
            );
        }
    }

    // Endpoint для получения активных карт пользователя
    @GetMapping("/my-active")
    public ResponseEntity<List<DiscountCardDto>> getUserActiveCards(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            List<DiscountCard> activeCards = discountCardService.getUserActiveCards(user.getId());
            List<DiscountCardDto> cardDtos = activeCards.stream()
                    .map(DiscountCardDto::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(cardDtos);
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}
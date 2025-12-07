package com.example.Order_Photo.controller.front;

import com.example.Order_Photo.dto.DiscountCardStatsDTO;
import com.example.Order_Photo.dto.OrderResponseDTO;
import com.example.Order_Photo.model.User;
import com.example.Order_Photo.repository.UserRepository;
import com.example.Order_Photo.service.DiscountCardService;
import com.example.Order_Photo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;
    private final DiscountCardService discountCardService;
    private final UserRepository userRepository;

    @GetMapping("/orders")
    public String getUserOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        DiscountCardStatsDTO stats = discountCardService.getUserDiscountCardStats(userDetails.getUsername());
        model.addAttribute("discountCardStats", stats);

        List<OrderResponseDTO> orders = orderService.getUserOrders(userDetails.getUsername());
        model.addAttribute("orders", orders);
        return "profile/orders";
    }

    @GetMapping("/my-orders")
    public String getUserOrders(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Получаем заказы
            List<OrderResponseDTO> orders = orderService.getUserOrders(userDetails.getUsername());
            model.addAttribute("orders", orders);

            // Получаем статистику в правильном формате
            DiscountCardStatsDTO discountCardStats = orderService.getDiscountCardStats(user.getId());
            model.addAttribute("discountCardStats", discountCardStats);

            return "orders";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке заказов: " + e.getMessage());
            return "orders";
        }
    }
}
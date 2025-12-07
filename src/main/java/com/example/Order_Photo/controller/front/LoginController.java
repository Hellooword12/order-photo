package com.example.Order_Photo.controller.front;

import com.example.Order_Photo.dto.UserRegistrationDTO;
import com.example.Order_Photo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "auth/register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/user-login";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegistrationDTO userDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // Ручная проверка совпадения паролей
        if (userDto.getPassword() != null && userDto.getConfirmPassword() != null
                && !userDto.getPassword().equals(userDto.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Пароли не совпадают");
            model.addAttribute("user", userDto); // Сохраняем введенные данные
            return "auth/register";
        }

        try {
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Регистрация прошла успешно! Теперь вы можете войти.");
            return "redirect:/login";
        } catch (UserService.UserRegistrationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", userDto); // Сохраняем введенные данные
            return "auth/register";
        }
    }
}
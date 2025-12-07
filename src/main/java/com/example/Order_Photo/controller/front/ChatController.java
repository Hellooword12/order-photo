package com.example.Order_Photo.controller.front;

import com.example.Order_Photo.model.ChatMessage;
import com.example.Order_Photo.model.User;
import com.example.Order_Photo.service.ChatService;
import com.example.Order_Photo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @GetMapping("/chat")
    public String showChat(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
            Page<ChatMessage> messagesPage = chatService.getMessages(pageable);

            String currentUsername = userDetails != null ? userDetails.getUsername() : "anonymous";

            // Компактная обработка с Optional
            String currentUserName = userDetails != null ?
                    userService.findByUsername(userDetails.getUsername())
                            .map(user -> user.getName() != null && !user.getName().isEmpty() ?
                                    user.getName() : user.getUsername())
                            .orElse(currentUsername) : "Гость";

            model.addAttribute("messages", messagesPage);
            model.addAttribute("currentUser", currentUsername);
            model.addAttribute("currentUserName", currentUserName);
            logger.info("Loaded {} messages for user {}", messagesPage.getTotalElements(), currentUsername);

        } catch (Exception e) {
            logger.error("Error loading chat messages", e);
            Page<ChatMessage> emptyPage = Page.empty();
            model.addAttribute("messages", emptyPage);
            model.addAttribute("currentUser", "anonymous");
            model.addAttribute("currentUserName", "Гость");
        }

        return "chat/chat";
    }

    @PostMapping("/send-chat")
    public String sendMessage(@RequestParam String content,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        try {
            if (userDetails == null) {
                redirectAttributes.addFlashAttribute("error", "Для отправки сообщения необходимо войти в систему");
                return "redirect:/login";
            }

            chatService.createMessage(userDetails.getUsername(), content);
            redirectAttributes.addFlashAttribute("success", "Сообщение отправлено");
            logger.info("Message sent by user: {}", userDetails.getUsername());

        } catch (Exception e) {
            logger.error("Error sending message from user: {}",
                    userDetails != null ? userDetails.getUsername() : "unknown", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при отправке сообщения: " + e.getMessage());
        }
        return "redirect:/chat";
    }

    @PostMapping("/delete-message/{id}")
    public String deleteMessage(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            chatService.deleteMessage(id);
            redirectAttributes.addFlashAttribute("success", "Сообщение успешно удалено");
        } catch (Exception e) {
            logger.error("Ошибка при удалении сообщения", e);
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить сообщение: " + e.getMessage());
        }
        return "redirect:/chat";
    }
}
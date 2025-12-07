package com.example.Order_Photo.service;

import com.example.Order_Photo.dto.MessageDTO;
import com.example.Order_Photo.model.ChatMessage;
import com.example.Order_Photo.model.User;
import com.example.Order_Photo.repository.ChatRepository;
import com.example.Order_Photo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Transactional
    public MessageDTO createMessage(String username, String content) {
        try {
            logger.info("Creating message for user: {}", username);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ChatMessage message = new ChatMessage();
            message.setContent(content);
            message.setSender(user);
            message.setReceiver(null); // Для общего чата
            message.setTimestamp(LocalDateTime.now());

            ChatMessage savedMessage = chatRepository.save(message);
            logger.info("Message saved with ID: {}", savedMessage.getId());

            return convertToDto(savedMessage);

        } catch (Exception e) {
            logger.error("Error creating message for user: {}", username, e);
            throw new RuntimeException("Не удалось отправить сообщение: " + e.getMessage(), e);
        }
    }

    public Page<ChatMessage> getMessages(Pageable pageable) {
        try {
            return chatRepository.findAllWithSenders(pageable);
        } catch (Exception e) {
            logger.error("Error getting messages", e);
            return Page.empty();
        }
    }

    private MessageDTO convertToDto(ChatMessage message) {
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(message.getSender().getUsername())
                .receiver(message.getReceiver() != null ? message.getReceiver().getUsername() : null)
                .timestamp(message.getTimestamp())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMessage(Long id) {

        ChatMessage message = chatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сообщение не найдено"));

        chatRepository.delete(message);
    }

}
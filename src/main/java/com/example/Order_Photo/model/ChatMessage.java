package com.example.Order_Photo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "reply_to_id")
    @JsonIgnoreProperties({"replyTo", "sender", "receiver", "hibernateLazyInitializer", "handler"})
    private ChatMessage replyTo;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonIgnoreProperties({"password", "orders", "hibernateLazyInitializer", "handler"})
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    @JsonIgnoreProperties({"password", "orders", "hibernateLazyInitializer", "handler"})
    private User receiver;

    private LocalDateTime timestamp;
}
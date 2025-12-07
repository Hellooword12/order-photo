package com.example.Order_Photo.dto;
import java.time.LocalDateTime;

public record MessageDTO(
        Long id,
        String content,
        String sender,
        String receiver,
        LocalDateTime timestamp
) {

    // Метод создания Builder
    public static Builder builder() {
        return new Builder();
    }

    // Внутренний класс Builder
    public static final class Builder {
        private Long id;
        private String content;
        private String sender;
        private String receiver;
        private LocalDateTime timestamp;


        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder receiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public MessageDTO build() {

            return new MessageDTO(id, content, sender, receiver, timestamp);
        }

    }
}
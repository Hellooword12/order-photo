package com.example.Order_Photo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileDTO {
    private String name;
    private String email;
    private String phone;
    private LocalDateTime registrationDate;
}
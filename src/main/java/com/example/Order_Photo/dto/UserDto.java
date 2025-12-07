package com.example.Order_Photo.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String name;

    public UserDto(Long id, String username, String email, String phoneNumber, String name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public UserDto() {

    }
}
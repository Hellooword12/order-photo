package com.example.Order_Photo.service;

import com.example.Order_Photo.dto.UserProfileDTO;
import com.example.Order_Photo.dto.UserRegistrationDTO;
import com.example.Order_Photo.model.User;
import com.example.Order_Photo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO) {
        // Проверка совпадения паролей
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new UserRegistrationException("Пароли не совпадают");
        }

        // Проверка существования пользователя
        if (userRepository.findByEmailIgnoreCase(registrationDTO.getEmail()).isPresent()) {
            throw new UserRegistrationException("Пользователь с таким email уже существует");
        }

        // Создание пользователя
        User user = User.builder()
                .name(registrationDTO.getName())
                .email(registrationDTO.getEmail())
                .phoneNumber(registrationDTO.getPhone())
                .username(registrationDTO.getEmail()) // используем email как username
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .role("ROLE_USER")
                .enabled(true)
                .registrationDate(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public static class UserRegistrationException extends RuntimeException {
        public UserRegistrationException(String message) {
            super(message);
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateProfile(String username, UserProfileDTO profileDTO) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (profileDTO.getName() != null) {
            user.setName(profileDTO.getName());
        }
        if (profileDTO.getEmail() != null && !profileDTO.getEmail().equals(user.getEmail())) {
            // Проверка на уникальность email
            if (userRepository.existsByEmail(profileDTO.getEmail())) {
                throw new RuntimeException("Email уже используется другим пользователем");
            }
            user.setEmail(profileDTO.getEmail());
        }
        if (profileDTO.getPhone() != null) {
            user.setPhoneNumber(profileDTO.getPhone());
        }

        userRepository.save(user);
    }
}
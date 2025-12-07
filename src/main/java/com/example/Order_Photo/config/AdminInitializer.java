package com.example.Order_Photo.config;

import com.example.Order_Photo.model.User;
import com.example.Order_Photo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, существует ли уже администратор
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("Admin")
                    .password(passwordEncoder.encode("123456"))
                    .email("admin@photoprint.ru")
                    .role("ROLE_ADMIN")
                    .enabled(true)
                    .build();

            userRepository.save(admin);
            System.out.println("✅ Администратор создан: Admin / 123456");
        } else {
            System.out.println("ℹ️ Администратор уже существует");
        }
    }
}
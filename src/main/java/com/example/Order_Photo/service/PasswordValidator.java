package com.example.Order_Photo.service;

import com.example.Order_Photo.repository.StringValidator;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service("passwordValidator")
public class PasswordValidator implements StringValidator {
    private static final String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*)(?=\\S+$).{6,20}$";
    private static final String VALIDATION_MESSAGE =
            """
                    Пароль должен содержать:
                    - 6-20 символов
                    - минимум 1 цифру
                    - без пробелов""";

    @Override
    public boolean isValid(String password) {
        return Pattern.compile(REGEX_PASSWORD)
                .matcher(password)
                .matches(); // возвращаем true когда валидно
    }

    @Override
    public String getValidationMessage() {
        return VALIDATION_MESSAGE;
    }
}

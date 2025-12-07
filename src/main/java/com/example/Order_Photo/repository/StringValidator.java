package com.example.Order_Photo.repository;

public interface StringValidator {
    boolean isValid(String input);
    String getValidationMessage();
}
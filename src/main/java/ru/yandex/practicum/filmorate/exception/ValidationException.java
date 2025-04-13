package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String inputValue;

    public ValidationException(String inputValue, String message) {
        super(message);
        this.inputValue = inputValue;
    }
}

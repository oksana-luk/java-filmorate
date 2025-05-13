package ru.yandex.practicum.filmorate.dto.user;

import java.time.LocalDate;

public interface BaseUserDto {

    String getEmail();

    String getLogin();

    LocalDate getBirthday();

    String getName();
}

package ru.yandex.practicum.filmorate.dto.film;

import java.time.LocalDate;

public interface BaseFilmDto {
    String getName();

    String getDescription();

    LocalDate getReleaseDate();

    Integer getDuration();
}

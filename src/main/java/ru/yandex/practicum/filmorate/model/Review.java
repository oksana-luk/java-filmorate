package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Review {
    private Long reviewId;
    private String content;
    private boolean isPositive;
    private Long userId;
    private Long filmId;
    private Integer useful;
}

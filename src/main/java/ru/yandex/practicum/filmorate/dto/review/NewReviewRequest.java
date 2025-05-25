package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewReviewRequest implements BaseReviewDto {
    @NotBlank(message = "Review content should not be empty")
    private String content;

    @NotNull(message = "isPositive cannot be null")
    private Boolean isPositive;

    @NotNull(message = "Film ID cannot be null")
    private Long filmId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

}

package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReviewRequest implements BaseReviewDto {
    @NotNull
    private Long reviewId;

    private String content;

    private Boolean isPositive;



    public boolean hasContent() {
        return ! (content == null || content.isBlank());
    }

    public boolean hasIsPositive() {
        return ! (isPositive == null);
    }

}

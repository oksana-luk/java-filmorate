package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;

import java.util.Collection;
import java.util.Map;

public interface ReviewService {

    ReviewDto createReview(NewReviewRequest newReviewRequest);

    ReviewDto updateReview(UpdateReviewRequest updateReviewRequest);

    boolean deleteReview(Long id);

    ReviewDto findReviewById(Long id);

    Collection<ReviewDto> findReviewsByFilmId(Long filmId, Integer count);

    void likeReview(Long id, Long userId);

    void dislikeReview(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    void deleteDislike(Long id, Long userId);
}

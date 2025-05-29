package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;

import java.util.Collection;

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

package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> findReviewById(Long id);

    Review addReview(Review review);

    Review updateReview(Review review);

    boolean deleteReviewById(Long id);

    Collection<Review> findReviewsByFilmId(Long filmId, Integer count);

    void likeReview(Long id, Long userId);

    void dislikeReview(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    void deleteDislike(Long id, Long userId);

    Optional<Integer> findRating(Long id, Long userId);
}

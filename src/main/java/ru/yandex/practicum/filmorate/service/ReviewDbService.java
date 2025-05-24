package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewDbService implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewDbService(
            @Qualifier("reviewRepository") ReviewStorage reviewStorage,
            @Qualifier("filmRepository") FilmStorage filmStorage,
            @Qualifier("userRepository") UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ReviewDto createReview(NewReviewRequest newReviewRequest) {
        validateFilmId(newReviewRequest.getFilmId());
        validateUserId(newReviewRequest.getUserId());
        Review review = ReviewMapper.mapToReview(newReviewRequest);
        review = reviewStorage.addReview(review);
        validateNotFound(review.getReviewId());
        return ReviewMapper.mapToReviewDto(review);
    }

    @Override
    public ReviewDto updateReview(UpdateReviewRequest updateReviewRequest) {
        Review review = validateNotFound(updateReviewRequest.getReviewId());
        review = ReviewMapper.updateReviewFields(review, updateReviewRequest);
        review = reviewStorage.updateReview(review);
        review = validateNotFound(review.getReviewId());
        return ReviewMapper.mapToReviewDto(review);
    }

    @Override
    public boolean deleteReview(Long id) {
        validateNotFound(id);
        return reviewStorage.deleteReviewById(id);
    }

    @Override
    public Collection<ReviewDto> findReviewsByFilmId(Long filmId, Integer count) {
        if (filmId != null) {
            validateFilmId(filmId);
        }
        return reviewStorage.findReviewsByFilmId(filmId, count)
                .stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDto findReviewById(Long id) {
        Review review = validateNotFound(id);
        return ReviewMapper.mapToReviewDto(review);
    }

    @Override
    public void likeReview(Long id, Long userId) {
        validateNotFound(id);
        validateUserId(userId);
        reviewStorage.likeReview(id, userId);

    }

    @Override
    public void dislikeReview(Long id, Long userId) {
        validateNotFound(id);
        validateUserId(userId);
        reviewStorage.dislikeReview(id, userId);

    }

    @Override
    public void deleteLike(Long id, Long userId) {
        validateNotFound(id);
        validateUserId(userId);
        validateRating(id, userId);
        reviewStorage.deleteLike(id, userId);
    }

    @Override
    public void deleteDislike(Long id, Long userId) {
        validateNotFound(id);
        validateUserId(userId);
        reviewStorage.deleteDislike(id, userId);
    }

    private Integer validateRating(Long id, Long userId) {
        return reviewStorage.findRating(id, userId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("No rating with " + id + " not found");
            log.error(e.getMessage());
            return e;
        });
    }

    private void validateFilmId(Long id) {
        filmStorage.findFilmById(id).orElseThrow(() -> {
                NotFoundException e = new NotFoundException("Film " + id + " not found");
                log.error(e.getMessage());
                return e;
        });
    }

    private void validateUserId(Long id) {
        userStorage.findUserById(id).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("User " + id + " not found");
            log.error(e.getMessage());
            return e;
        });
    }

    private Review validateNotFound(Long id) {
        return reviewStorage.findReviewById(id).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Review " + id + " not found");
            log.error(e.getMessage());
            return e;
        });
    }

}

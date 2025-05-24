package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(@Qualifier("reviewDbService") ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(@Valid @RequestBody NewReviewRequest newReviewRequest) {
        log.debug("POST/reviews: start of creating of new review");
        ReviewDto reviewDto = reviewService.createReview(newReviewRequest);
        log.debug("POST/reviews: the process was completed successfully. A new review with id {} has been created", reviewDto.getReviewId());
        return reviewDto;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto updateReview(@Valid @RequestBody UpdateReviewRequest updateReviewRequest) {
        log.debug("PUT/reviews: start of updating of review {}", updateReviewRequest.getReviewId());
        ReviewDto reviewDto = reviewService.updateReview(updateReviewRequest);
        log.debug("PUT/reviews: the process was completed successfully. A  with id {} has been updated", updateReviewRequest.getReviewId());
        return reviewDto;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteReview(@PathVariable Long id) {
        log.debug("DELETE/reviews/id: start of deleting of review with id {}", id);
        reviewService.deleteReview(id);
        log.debug("DELETE/reviews/id: the process was completed successfully. A review with id {} has been deleted", id);
        return ResponseEntity.ok(Map.of("result",String.format("Review with id %s has been deleted successfully.", id)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto findReviewById(@PathVariable Long id) {
        log.debug("GET/reviews/id: start of finding of review {}", id);
        ReviewDto reviewDto = reviewService.findReviewById(id);
        log.debug("GET/reviews/id: the process was completed successfully. A review with id {} has been found", reviewDto.getReviewId());
        return reviewDto;
    }

    @GetMapping
    public Collection<ReviewDto> findReviewsByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        log.debug("GET/reviews/filmId: start of finding of {} most useful reviews for film {}", count, filmId);
        Collection<ReviewDto> reviews = reviewService.findReviewsByFilmId(filmId, count);
        log.debug("GET/reviews/filmId: success");
        return reviews;
    }

    @PutMapping("{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> likeReview(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("PUT/reviews/id/like/id: start of liking of review with id {}", id);
        reviewService.likeReview(id, userId);
        log.debug("PUT/reviews/id/like/id: the process was completed successfully. A review with id {} has been liked of user with id {}", id, userId);
        return ResponseEntity.ok(Map.of("result", String.format("A review with id %d has an like from user with id %d", id, userId)));
    }

    @PutMapping("{id}/dislike/{userId}")
    public ResponseEntity<Map<String, String>> dislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("PUT/reviews/id/like/id: start of disliking of review with id {}", id);
        reviewService.dislikeReview(id, userId);
        log.debug("PUT/reviews/id/like/id: the process was completed successfully. A review with id {} has been disliked of user with id {}", id, userId);
        return ResponseEntity.ok(Map.of("result", String.format("A review with id %d has an dislike from user with id %d", id, userId)));
    }

    @DeleteMapping("{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("DELETE/reviews/id/like/id: start of deleting like of review with id {}", id);
        reviewService.deleteLike(id, userId);
        log.debug("DELETE/reviews/id/like/id: the process was completed successfully.  From review with id {} has been deleted like of user with id {}", id, userId);
        return ResponseEntity.ok(Map.of("result", String.format("From review with id %d has been deleted an like from user with id %d", id, userId)));
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public ResponseEntity<Map<String, String>> deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("DELETE/reviews/id/like/id: start of deleting dislike of review with id {}", id);
        reviewService.deleteDislike(id, userId);
        log.debug("DELETE/reviews/id/like/id: the process was completed successfully.  From review with id {} has been deleted dislike of user with id {}", id, userId);
        return ResponseEntity.ok(Map.of("result", String.format("From review with id %d has been deleted an dislike from user with id %d", id, userId)));
    }


}

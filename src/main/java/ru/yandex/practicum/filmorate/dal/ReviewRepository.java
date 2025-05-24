package ru.yandex.practicum.filmorate.dal;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> implements ReviewStorage {

    protected final ReviewRowMapper mapper;

    private static final String FIND_ONE = "SELECT * FROM reviews WHERE id = ?";

    private static final String INSERT_REVIEW = """
            INSERT INTO reviews (content, is_positive, user_id, film_id)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_REVIEW = """
            UPDATE reviews
            SET content = ?,
                is_positive = ?,
                useful = ?
            WHERE id = ?
            """;

    private static final String FIND_FOR_ALL_FILMS = """
            SELECT * FROM reviews
            ORDER BY useful DESC
            LIMIT ?
            """;

    private static final String FIND_FOR_FILM = """
            SELECT * FROM reviews
            WHERE film_id = ?
            ORDER BY useful DESC
            LIMIT ?
            """;

    private static final String FIND_RATING = """
            SELECT rating_value FROM reviews_ratings 
            WHERE review_id = ? AND user_id = ?
            """;

    private static final String DELETE_REVIEW = "DELETE reviews WHERE id = ?";

    private static final String INSERT_RATING = """
            INSERT INTO reviews_ratings (review_id, user_id, rating_value)
            VALUES (?, ?, ?)
            """;

    private static final String UPDATE_RATING = """
            UPDATE reviews_ratings
            SET rating_value = ?
            WHERE review_id = ? AND user_id = ?
            """;

    private static final String DELETE_RATING = """
            DELETE FROM reviews_ratings
            WHERE review_id = ? AND user_id = ?;
            """;

    public ReviewRepository(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

    @Override
    public Optional<Review> findReviewById(Long id) {
        return findOne(FIND_ONE, mapper, id);
    }

    @Override
    public Review addReview(Review review) {
        long id = insert(
                INSERT_REVIEW,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId()
        );
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        update(
                UPDATE_REVIEW,
                review.getContent(),
                review.isPositive(),
                review.getUseful(),
                review.getReviewId()
        );
        return review;
    }

    @Override
    public boolean deleteReviewById(Long id) {
        return delete(DELETE_REVIEW, id);
    }

    @Override
    public Collection<Review> findReviewsByFilmId(Long filmId, Integer count) {
        if (filmId != null) {
            return findMany(FIND_FOR_FILM, mapper, filmId, count);
        }
        return findMany(FIND_FOR_ALL_FILMS, mapper, count);
    }

    @Override
    public void likeReview(Long id, Long userId) {
        Optional<Integer> rating = findRating(id, userId);
        if (rating.isPresent()) {
            if (rating.get() == 1) {
                throw new DuplicateException("Already liked by this user");
            }
            jdbc.update("UPDATE reviews SET useful = useful + 2 WHERE id = ?", id);
            jdbc.update(UPDATE_RATING, 1, id, userId);
        } else {
            jdbc.update("UPDATE reviews SET useful = useful + 1 WHERE id = ?", id);
            jdbc.update(INSERT_RATING, id, userId, 1);
        }
    }

    @Override
    public void dislikeReview(Long id, Long userId) {
        Optional<Integer> rating = findRating(id, userId);
        if (rating.isPresent()) {
            if (rating.get() == -1) {
                throw new DuplicateException("Already liked by this user");
            }
            jdbc.update("UPDATE reviews SET useful = useful - 2 WHERE id = ?", id);
            jdbc.update(UPDATE_RATING, -1, id, userId);
        } else {
            jdbc.update("UPDATE reviews SET useful = useful - 1 WHERE id = ?", id);
            jdbc.update(INSERT_RATING, id, userId, -1);
        }
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        Optional<Integer> rating = findRating(id, userId);
        if (rating.isPresent()) {
            if (rating.get() == 1) {
                jdbc.update("UPDATE reviews SET useful = useful -1 WHERE id = ?", id);
                delete(DELETE_RATING, id, userId);
            } else {
                throw new NotFoundException("You can't delete dislike");
            }
        } else {
            throw new NotFoundException("No such rating exists");
        }
    }

    @Override
    public void deleteDislike(Long id, Long userId) {
        Optional<Integer> rating = findRating(id, userId);
        if (rating.isPresent()) {
            if (rating.get() == -1) {
                jdbc.update("UPDATE reviews SET useful = useful + 1 WHERE id = ?", id);
                delete(DELETE_RATING, id, userId);
            } else {
                throw new NotFoundException("You can't delete like");
            }
        } else {
            throw new NotFoundException("No such rating exists");
        }
    }

    @Override
    public Optional<Integer> findRating(Long id, Long userId) {
        try {
            return jdbc.queryForObject(
                    FIND_RATING,
                    (rs, rowNum) -> Optional.of(rs.getInt("rating_value")),
                    id, userId
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
